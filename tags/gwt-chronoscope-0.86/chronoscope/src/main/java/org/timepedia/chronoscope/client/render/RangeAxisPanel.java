package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel.Position;
import org.timepedia.chronoscope.client.util.MathUtil;

/**
 * Renders a vertical range axis.
 */
public class RangeAxisPanel extends AxisPanel {

  public enum TickPosition {

    INSIDE, OUTSIDE
  }

  private double axisLabelWidth, axisLabelHeight;

  private boolean boundsSet;

  private GssProperties gridProperties, tickProperties;

  private Bounds drawBounds = new Bounds();

  private double maxLabelWidth, maxLabelHeight;

  private RangeAxis rangeAxis;

  private double rotationAngle;

  public void computeLabelWidths(View view) {
    final String valueAxisLabel = valueAxis.getLabel();

    maxLabelWidth =
        stringSizer.getRotatedWidth(getDummyLabel(), gssProperties, 0) + 10;
    maxLabelHeight =
        stringSizer.getRotatedHeight(getDummyLabel(), gssProperties, 0) + 10;
    axisLabelWidth = stringSizer
        .getRotatedWidth(valueAxisLabel, gssProperties, rotationAngle);
    axisLabelHeight = stringSizer
        .getRotatedHeight(valueAxisLabel, gssProperties, rotationAngle);
  }

  public void draw() {
    bounds.copyTo(drawBounds);
    drawBounds.x += getLayerOffsetX();
    drawBounds.y += getLayerOffsetY();

    double tickPositions[] = rangeAxis.calcTickPositions();
    layer.save();

    if (!GRID_ONLY) {
      clearAxis(layer, drawBounds);
      drawLine(layer, drawBounds);
    }

    layer.setTransparency(1.0f);
    layer.setFillColor(Color.WHITE);
    layer.setStrokeColor(Color.GREEN);

    final double axisInterval = valueAxis.getExtrema().length();
    final double tickPosition0 = tickPositions[0];
    for (int i = 0; i < tickPositions.length; i++) {
      drawTick(layer, tickPositions[i], tickPosition0, axisInterval, drawBounds,
          GRID_ONLY);
    }

    if (!GRID_ONLY) {
      drawAxisLabel(layer, drawBounds, plot.getChart());
    }

    layer.restore();
  }

//  public String formatLegendLabel(double value) {
//    return rangeAxis.getFormattedLabel(value);
//  }

  public double getMaxLabelHeight() {
    return this.maxLabelHeight;
  }

  public double getMaxLabelWidth() {
    return maxLabelWidth;
  }

  public String getType() {
    return "axis";
  }

  public String getTypeClass() {
    return "a" + ((RangeAxis) valueAxis).getAxisIndex();
  }

  @Override
  public void layout() {
    computeLabelWidths(view);
    bounds.width = calcWidth();
    // height not calculated -- it must be dictated by some external entity
  }

  @Override
  protected void initHook() {
    rangeAxis = (RangeAxis) this.valueAxis;
    GssElement tickGssElem = new GssElementImpl("tick", this);
    GssElement gridGssElem = new GssElementImpl("grid", this);
    tickProperties = view.getGssProperties(tickGssElem, "");
    gridProperties = view.getGssProperties(gridGssElem, "");

    if (getParentPosition() == Position.RIGHT) {
      rotationAngle = Math.PI / 2;
    } else {
      rotationAngle = -(Math.PI / 2);
    }
  }

  private double calcWidth() {
    double w = 0.0;
    final double widthBuffer = 5;
    final double computedAxisLabelWidth = labelProperties.visible ?
        axisLabelWidth + widthBuffer : 0;

    boolean isLeft = getParentPosition() == Position.LEFT;
    if (isInnerMost(isLeft)) {
      if (getTickPosition() == RangeAxisPanel.TickPosition.INSIDE) {
        w = computedAxisLabelWidth;
      } else {
        w = maxLabelWidth + widthBuffer + computedAxisLabelWidth;
      }
    } else {
      w = maxLabelWidth + widthBuffer + computedAxisLabelWidth;
    }

    return w;
  }

  private void clearAxis(Layer layer, Bounds bounds) {
    layer.save();
    if (!boundsSet) {
      layer.setTextLayerBounds(textLayerName, bounds);
      boundsSet = true;
    }

    layer.clearTextLayer(textLayerName);

    layer.setFillColor(gssProperties.bgColor);
    layer.setShadowBlur(0);
    layer.setShadowOffsetX(0);
    layer.setShadowOffsetY(0);
    layer.translate(bounds.x - 1, bounds.y - 1);
    layer.scale(bounds.width + 1, bounds.height + 1);
    layer.fillRect(0, 0, 1, 1);
    layer.restore();
  }

  private void drawAxisLabel(Layer layer, Bounds bounds, Chart chart) {
    if (labelProperties.visible) {
      boolean isLeft = getParentPosition() == Position.LEFT;
      boolean isInnerMost = isInnerMost(isLeft);

      double dir = isLeft ? 0 : (isInnerMost ? 0 : maxLabelWidth + 1);
      double x = bounds.x + dir;
      double y = bounds.y + (bounds.height / 2) - (axisLabelHeight / 2);
      layer.setStrokeColor(labelProperties.color);
      String label = valueAxis.getLabel();

      layer
          .drawRotatedText(x, y, rotationAngle, label, gssProperties.fontFamily,
              gssProperties.fontWeight, gssProperties.fontSize, textLayerName,
              chart);
    }
  }

  private void drawLabel(Layer layer, double y, Bounds bounds, double value) {
    String label = rangeAxis.getFormattedLabel(value);

    double labelWidth = layer
        .stringWidth(label, gssProperties.fontFamily, gssProperties.fontWeight,
            gssProperties.fontSize);
    double labelHeight = layer
        .stringHeight(label, gssProperties.fontFamily, gssProperties.fontWeight,
            gssProperties.fontSize);
    boolean isLeft = getParentPosition() == Position.LEFT;
    double dir = (isLeft ? -5 - labelWidth : 5 - bounds.width);
    if ("inside".equals(gssProperties.tickPosition)) {
      dir = isLeft ? 5 + 1 : -labelWidth - 5;
    }

    layer.save();
    layer.setStrokeColor(getTickProps(labelProperties).color);
    layer.setFillColor(labelProperties.bgColor);
    double alignAdjust = -labelHeight / 2;
    if ("above".equals(labelProperties.tickAlign)) {
      alignAdjust = -labelHeight;
      dir = 1;

      if (isInnerMost(isLeft)) {
        dir = isLeft ? 1 : -bounds.width - labelWidth - 3;
      } else {
        dir = isLeft ? (-maxLabelWidth + 1)
            : (-labelWidth - axisLabelWidth - 10);
      }
    }
    if (MathUtil.isBounded(y, bounds.y, bounds.bottomY())) {
      layer.drawText(bounds.rightX() + dir, y + alignAdjust, label,
          gssProperties.fontFamily, gssProperties.fontWeight,
          gssProperties.fontSize, textLayerName, Cursor.DEFAULT);
    }
    layer.restore();
  }

  private void drawLine(Layer layer, Bounds bounds) {
    GssProperties tprop = getTickProps(tickProperties);
    layer.setFillColor(tprop.color);
    boolean isLeft = getParentPosition() == Position.LEFT;
    double dir = (isLeft ? bounds.width : 0);
    if ("inside".equals(gssProperties.tickPosition)) {
      if (isInnerMost(isLeft)) {
        dir = isLeft ? bounds.width : -1;
      } else {
        dir = isLeft ? bounds.width - maxLabelWidth - 1 : maxLabelWidth + 1;
      }
    }
    layer.fillRect(bounds.x + dir, bounds.y, tickProperties.lineThickness,
        bounds.height);
  }

  private GssProperties getTickProps(GssProperties defprop) {
    GssProperties tprop = defprop;
    if (!plot.isMultiaxis()) {
      int dIdx = plot.getFocus() != null ? plot.getFocus().getDatasetIndex()
          : -1;

      if (dIdx != -1) {
        GssProperties cprop = plot.getDatasetRenderer(dIdx).getCurveProperties()
            ;
        if (cprop != null) {
          tprop = cprop;
        }
      }
    }
    return tprop;
  }

  private void drawTick(Layer layer, double range, double rangeLow,
      double rangeInterval, Bounds bounds, boolean gridOnly) {

    // Determines the horizontal length (in pixels) of each range axis tick
    final int tickWidth = 5;

    double uy = bounds.y + (bounds.height - ((range - rangeLow) / rangeInterval
        * bounds.height));

    boolean isLeft = getParentPosition() == Position.LEFT;
    double dir = (isLeft ? (bounds.width - tickWidth) : 0);
    if ("inside".equals(gssProperties.tickPosition)) {
      if (isInnerMost(isLeft)) {
        dir = isLeft ? (bounds.width + 1) : -tickWidth;
      } else {
        dir = isLeft ? (bounds.width - maxLabelWidth - 1)
            : (maxLabelWidth - tickWidth + 1);
      }
    }

    layer.save();
    layer.setFillColor(getTickProps(tickProperties).color);
    layer.setTransparency(1);

    layer.fillRect(bounds.x + dir, uy, tickWidth, tickProperties.lineThickness);
    if (gridProperties.visible && uy != bounds.bottomY()) {
      layer.setFillColor(gridProperties.color);
      layer.setTransparency((float) gridProperties.transparency);
      layer.fillRect(bounds.rightX(), uy, plot.getInnerBounds().width,
          gridProperties.lineThickness);
    }
    layer.restore();
    if (!gridOnly) {
      drawLabel(layer, uy, bounds, range);
    }
  }

  private String getDummyLabel() {
    int maxDig = RangeAxis.MAX_DIGITS;
    return "0" + ((maxDig == 1) ? ""
        : "." + "000000000".substring(0, maxDig - 1));
  }

  private TickPosition getTickPosition() {
    return "inside".equals(gssProperties.tickPosition) ? TickPosition.INSIDE
        : TickPosition.OUTSIDE;
  }

  private boolean isInnerMost(boolean isLeftPanel) {
    CompositeAxisPanel parentContainer = (CompositeAxisPanel) this.parent;

    return parentContainer.indexOf(this) == (isLeftPanel ?
        parentContainer.getChildCount() - 1 : 0);
  }

  private Position getParentPosition() {
    return ((CompositeAxisPanel) parent).getPosition();
  }


  private static void log(Object msg) {
    System.out.println("RangeAxisPanel> " + msg);
  }
}