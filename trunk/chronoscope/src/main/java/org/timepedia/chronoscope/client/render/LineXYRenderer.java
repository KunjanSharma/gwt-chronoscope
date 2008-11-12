package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.exporter.client.Exportable;

/**
 * Renders scatter plot, lines, points+lines, or filled areas depending on 
 * GSS styling used.
 */
public class LineXYRenderer<T extends Tuple2D> extends DatasetRenderer<T> 
    implements GssElement, Exportable {
  
  protected GssProperties activeGssLineProps, activeGssPointProps;

  protected double lx = -1, ly = -1;
  
  protected double fx = -1;
  
  @Override
  public void beginCurve(Layer layer, RenderState renderState) {
    activeGssLineProps = renderState.isDisabled() ? gssDisabledLineProps : gssLineProps;
    layer.save();
    layer.beginPath();

    lx = ly = -1;
    fx = -1;
  }

  @Override
  public void beginPoints(Layer layer, RenderState renderState) {
    activeGssPointProps = renderState.isDisabled() ? gssDisabledPointProps : gssPointProps;
    lx = ly = -1;
    layer.save();
  }
  
  @Override
  public double calcLegendIconWidth(View view) {
    GssProperties apointProp = 
      (plot.getFocus() != null) ? gssPointProps 
                                : gssDisabledPointProps;
    return apointProp.size + 10;
  }

  @Override
  public void drawCurvePart(Layer layer, T point, int methodCallCount, 
      RenderState renderState) {
    
      double ux = plot.domainToScreenX(point.getFirst(), datasetIndex);
      double uy = plot.rangeToScreenY(point.getSecond(), datasetIndex);
      
      // guard webkit bug, coredump if draw two identical lineTo in a row
      if (activeGssLineProps.visible) {
        if (methodCallCount == 0) {
          // This is the first point to be rendered, so just store the coordinates.
          fx = lx = ux;
          ly = uy;
        }
        else {
          if (methodCallCount == 1) {
            // This is the 2nd point to be rendered, and also the 1st line segment
            // to be rendered, so need to position the cursor at point 0 (the 
            // previous point)
            layer.moveTo(lx, ly);
          }
          
          // Draw a line from the end of the previous line segment (or the 1st point
          // of the visible curve if this is the first line segment to be drawn) 
          // to (ux, uy).
          layer.lineTo(ux, uy);
          
          lx = ux;
          ly = uy;
        }
      }
  }
  
  @Override
  public void drawHoverPoint(Layer layer, T point, int datasetIndex) {
    GssProperties layerProps = this.gssHoverProps;
    
    if (layerProps.size < 1) {
      layerProps.size = 1;
    }
    
    final double dataX = point.getFirst();
    final double dataY = point.getSecond();
    final double ux = plot.domainToScreenX(dataX, datasetIndex);
    final double uy = plot.rangeToScreenY(dataY, datasetIndex);
    drawPoint(ux, uy, layer, layerProps);
  }
  
  @Override
  public void drawLegendIcon(Layer layer, double x, double y) {
    layer.save();

    GssProperties alineProp, apointProp;
    if (plot.getFocus() != null) {
      alineProp = gssDisabledLineProps;
      apointProp = gssDisabledPointProps;
    } else {
      alineProp = gssLineProps;
      apointProp = gssPointProps;
    }

    layer.beginPath();
    layer.moveTo(x, y);
    layer.setLineWidth(alineProp.lineThickness);
    layer.setShadowBlur(alineProp.shadowBlur);
    layer.setShadowColor(alineProp.shadowColor);
    layer.setShadowOffsetX(alineProp.shadowOffsetX);
    layer.setShadowOffsetY(alineProp.shadowOffsetY);
    layer.setStrokeColor(alineProp.color);
    layer.setTransparency((float) alineProp.transparency);
    layer.lineTo(x + 5 + apointProp.size + 5, y);
    layer.stroke();

    if (apointProp.visible) {
      layer.translate(x, y - apointProp.size / 2 + 1);
      layer.beginPath();
      layer.setFillColor(apointProp.bgColor);
      layer.setTransparency((float) apointProp.transparency);
      layer.arc(6, 0, apointProp.size, 0, 2 * Math.PI, 1);
      layer.setShadowBlur(0);
      layer.fill();
      layer.beginPath();
      layer.setLineWidth(apointProp.lineThickness);
      if (apointProp.size < 1) {
        apointProp.size = 1;
      }
      layer.arc(6, 0, apointProp.size, 0, 2 * Math.PI, 1);
      layer.setLineWidth(apointProp.lineThickness);
      layer.setShadowBlur(apointProp.shadowBlur);
      layer.setStrokeColor(apointProp.color);
      layer.stroke();
    }
    
    layer.restore();
  }

  @Override
  public void drawPoint(Layer layer, T point, RenderState renderState) {
    final boolean isFocused = renderState.isFocused();
    final double dataX = point.getFirst();
    final double dataY = point.getSecond();
    
    GssProperties gssProps = activeGssPointProps;
    if (gssProps.visible || isFocused) {

      if (gssProps.size < 1) {
        gssProps.size = 1;
      }
      
      double ux = plot.domainToScreenX(dataX, datasetIndex);
      double uy = plot.rangeToScreenY(dataY, datasetIndex);
      double dx = ux - lx;

      if (lx == -1 || isFocused || dx > (gssProps.size * 2 + 4)) {
        drawPoint(ux, uy, layer, gssProps);
        lx = ux;
        ly = uy;
      }
    }
  }

  @Override
  public void endCurve(Layer layer, RenderState renderState) {
    GssProperties fillProp = renderState.isDisabled() 
        ? gssDisabledFillProps : gssFillProps;

    layer.setFillColor(activeGssLineProps.bgColor);
    layer.setLineWidth(activeGssLineProps.lineThickness);
    layer.setShadowBlur(activeGssLineProps.shadowBlur);
    layer.setShadowColor(activeGssLineProps.shadowColor);
    layer.setShadowOffsetX(activeGssLineProps.shadowOffsetX);
    layer.setShadowOffsetY(activeGssLineProps.shadowOffsetY);
    layer.setStrokeColor(activeGssLineProps.color);
    layer.setTransparency((float) activeGssLineProps.transparency);
    
    layer.stroke();
    layer.lineTo(lx, layer.getHeight());
    layer.lineTo(fx, layer.getHeight());
    
    layer.setFillColor(fillProp.bgColor);
    layer.setTransparency((float) fillProp.transparency);
    
    layer.fill();
    layer.restore();
  }

  @Override
  public void endPoints(Layer layer, RenderState renderState) {
    layer.restore();
  }

  public String getType() {
    return "line";
  }

  public String getTypeClass() {
    return null;
  }

  /**
   * Draws a point at the specified screen coordinates
   */
  private void drawPoint(double ux, double uy, Layer layer, GssProperties gssProps) {
    layer.setFillColor(gssProps.bgColor);
    layer.setLineWidth(gssProps.lineThickness);
    layer.setShadowBlur(gssProps.shadowBlur);
    layer.setStrokeColor(gssProps.color);
    
    layer.beginPath();
//  layer.translate(ux, uy);
    layer.arc(ux, uy, gssProps.size, 0, 2 * Math.PI, 1);
    layer.fill();
    layer.stroke();
//  layer.translate(-ux, -uy);
  }
  
}