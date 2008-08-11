package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LoadListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.ClippedImageImpl;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.AbstractLayer;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.CanvasFontMetrics;
import org.timepedia.chronoscope.client.canvas.FontRendererService;
import org.timepedia.chronoscope.client.canvas.FontRendererServiceAsync;
import org.timepedia.chronoscope.client.canvas.RenderedFontMetrics;

import java.util.HashMap;

/**
 * A class that implements text rendering by positioning DIVs with text or
 * images over the canvas.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public abstract class DomTextLayer extends AbstractLayer {

  public static class TextLayer {

    public Element layerElem;

    public Bounds bounds;
  }

  private static final HashMap fontMetricsCache = new HashMap();

  public Element metricDiv;

  protected HashMap metricMap = new HashMap();

  protected final HashMap layers = new HashMap();

  private boolean printOnce = false;

  protected DomTextLayer(Canvas canvas) {
    super(canvas);
  }

  public void clearTextLayer(String layerName) {
    TextLayer layer = (TextLayer) layers.get(layerName);
    if (layer != null) {
      DOM.setInnerHTML(layer.layerElem, "");
    }
  }

  public Element createTextDiv() {
    Element textDiv = DOM.createElement("div");
    DOM.setStyleAttribute(textDiv, "position", "absolute");

    DOM.setStyleAttribute(textDiv, "backgroundColor", "transparent");
    return textDiv;
  }

  // overall, this routine needs to be more robust
  // and there probably should be a TextLayout class that can layout multiline text with wrapping in the presence
  // of rotations within an enclosed area
  public void drawRotatedText(final double x, final double y,
      final double angle, final String label, String fontFamily,
      String fontWeight, String fontSize, final String layerName,
      final Chart chart) {
    String metricsKey = getStrokeColor() + fontFamily + fontWeight + fontSize
        + angle;
    CanvasFontMetrics rmt = (CanvasFontMetrics) fontMetricsCache
        .get(metricsKey);

    if (rmt == null) {
      rmt = new CanvasFontMetrics();
      rmt.maxHeight = stringHeight("X", fontFamily, fontWeight, fontSize);
      rmt.maxWidth = stringWidth("X", fontFamily, fontWeight, fontSize);
      fontMetricsCache.put(metricsKey, rmt);
    }
    if (rmt.rfm == null) {
      if (!Chronoscope.isFontBookRenderingEnabled()) {

        double tx = x, ty = y;
        // needs better layout
        for (int i = 0; i < label.length(); i++) {
          String letter = String.valueOf(label.charAt(i));
          drawText(tx, ty, letter, fontFamily, fontWeight, fontSize, layerName,
              Cursor.DEFAULT);
          ty += rmt.maxHeight - 3;
        }
        return;
      }

      FontRendererServiceAsync fontRendererServiceAsync = FontRendererService
          .FontRenderServiceUtil.getInstance();
      final CanvasFontMetrics rmt1 = rmt;

      String fs = fontSize;
      if (fs.endsWith("pt")) {
        fs = fs.substring(0, fs.length() - 2);
      }
      int ifs = (int) Double.parseDouble(fs);
      ifs = ifs * 12 / 9;

      fontRendererServiceAsync.getRenderedFontMetrics(fontFamily, fontWeight,
          ifs + "pt", getStrokeColor(), (float) angle, new AsyncCallback() {
        public void onFailure(Throwable caught) {
          if (Chronoscope.isErrorReportingEnabled()) {
            Window.alert("Failed to load fontbook " + caught);
          }
          Chronoscope.setFontBookRendering(false);
        }

        public void onSuccess(Object result) {
          rmt1.rfm = (RenderedFontMetrics) result;
          if (result == null) {
            if (Chronoscope.isErrorReportingEnabled()) {
              Window.alert("No rendered fontmetrics returned!");
              Chronoscope.setFontBookRendering(false);
            }
            // TODO: handle this error
          }

          chart.reloadStyles();

          final Image img = new Image();

          img.setVisible(false);

          img.addLoadListener(new LoadListener() {
            public void onError(Widget sender) {
              if (Chronoscope.isErrorReportingEnabled()) {
                Window.alert("Couldn't load image " + rmt1.rfm
                    .url);
                Chronoscope.setFontBookRendering(false);
              }
            }

            public void onLoad(Widget sender) {
              rmt1.fontBook = (Image) sender;
              RootPanel.get().remove(img);
            }
          });
          img.setUrl(rmt1.rfm.url);
          RootPanel.get().add(img);
          /*drawTextImage(
                  x, y, label, layerName, angle, rmt1.rfm
          );*/
        }
      });
    } else {
      if (rmt.rfm != null) {
        drawTextImage(x, y, label, layerName, angle, rmt.rfm);
      }
    }
  }

  public void drawText(double x, double y, String label, String fontFamily,
      String fontWeight, String fontSize, String layerName, Cursor cursorStyle) {
    TextLayer layer = getTextLayer(layerName);
    Element layerElem = layer.layerElem;
    Element textDiv = createTextDiv();
    DOM.setStyleAttribute(textDiv, "left", (x - layer.bounds.x) + "px");
    DOM.setStyleAttribute(textDiv, "top", (y - layer.bounds.y) + "px");
    DOM.setStyleAttribute(textDiv, "fontFamily", fontFamily);
    DOM.setStyleAttribute(textDiv, "fontSize", fontSize);
    DOM.setStyleAttribute(textDiv, "fontWeight", fontWeight);
    DOM.setStyleAttribute(textDiv, "color", getStrokeColor());
    DOM.setStyleAttribute(textDiv, "opacity", getTransparency());
    if(cursorStyle == Cursor.CLICKABLE) {
      DOM.setStyleAttribute(textDiv, "textDecoration", "underline");
      DOM.setStyleAttribute(textDiv, "cursor", "pointer");
    }
    DOM.setInnerHTML(textDiv, label);
    DOM.appendChild(layerElem, textDiv);
  }

  public abstract Element getElement();

  public TextLayer getTextLayer(String layerName) {
    TextLayer layer = (TextLayer) layers.get(layerName);
    if (layer == null) {
      Element layerElem;
      layerElem = DOM.createElement("div");
      DOM.setStyleAttribute(layerElem, "position", "absolute");
      DOM.setIntStyleAttribute(layerElem, "left", 0);
      DOM.setIntStyleAttribute(layerElem, "top", 0);
      DOM.setIntStyleAttribute(layerElem, "width", (int) getWidth());
      DOM.setIntStyleAttribute(layerElem, "height", (int) getHeight());
      DOM.setStyleAttribute(layerElem, "backgroundColor", "transparent");
      DOM.setStyleAttribute(layerElem, "zIndex", "3");
      DOM.setStyleAttribute(layerElem, "overflow", "visible");
      DOM.appendChild(DOM.getParent(getElement()), layerElem);
      layer = new TextLayer();
      layer.layerElem = layerElem;
      layer.bounds = new Bounds(0, 0, getWidth(), getHeight());
      layers.put(layerName, layer);
    }
    return layer;
  }

  public int rotatedStringHeight(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    String metricsKey = getStrokeColor() + fontFamily + fontWeight + fontSize
        + rotationAngle;
    CanvasFontMetrics rmt = (CanvasFontMetrics) fontMetricsCache
        .get(metricsKey);
    if (rmt != null && rmt.rfm != null) {
      return rmt.rfm.stringHeight(str.toCharArray());
    } else {
      return super.rotatedStringHeight(str, rotationAngle, fontFamily,
          fontWeight, fontSize);
    }
  }

  public int rotatedStringWidth(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    String metricsKey = getStrokeColor() + fontFamily + fontWeight + fontSize
        + rotationAngle;
    CanvasFontMetrics rmt = (CanvasFontMetrics) fontMetricsCache
        .get(metricsKey);
    if (rmt != null && rmt.rfm != null) {
      return rmt.rfm.stringWidth(str.toCharArray());
    } else {
      return super.rotatedStringWidth(str, rotationAngle, fontFamily,
          fontWeight, fontSize);
    }
  }

  public void setTextLayerBounds(String layerName, Bounds bounds) {
    TextLayer layer = getTextLayer(layerName);
    Element layerElem = layer.layerElem;
    layer.bounds = new Bounds(bounds);

    DOM.setStyleAttribute(layerElem, "width", bounds.width + "px");
    DOM.setStyleAttribute(layerElem, "height", bounds.height + "px");
    DOM.setStyleAttribute(layerElem, "left", bounds.x + "px");
    DOM.setStyleAttribute(layerElem, "top", bounds.y + "px");
    DOM.setStyleAttribute(layerElem, "overflow", "visible");
    DOM.setStyleAttribute(layerElem, "zIndex", "5");
    DOM.setStyleAttribute(layerElem, "backgroundColor", "transparent");
    DOM.setStyleAttribute(layerElem, "visibility", "visible");
  }

  public int stringHeight(String string, String font, String bold,
      String size) {
    Element div = getMetricDiv();

    DOM.setStyleAttribute(div, "fontFamily", font);
    DOM.setStyleAttribute(div, "fontWeight", bold);
    DOM.setStyleAttribute(div, "fontSize", size);
    DOM.setInnerHTML(div, string);

    return DOM.getElementPropertyInt(div, "clientHeight");
  }

  public int stringWidth(String string, String font, String bold, String size) {
    Element div = getMetricDiv();
    DOM.setStyleAttribute(div, "fontFamily", font);
    DOM.setStyleAttribute(div, "fontWeight", bold);
    DOM.setStyleAttribute(div, "fontSize", size);
    DOM.setInnerHTML(div, string);

    return DOM.getElementPropertyInt(div, "clientWidth");
  }

  private void drawTextImage(double x, double y, String label, String layerName,
      double angle, RenderedFontMetrics rfm) {
    TextLayer layer = getTextLayer(layerName);
    Element layerElem = layer.layerElem;
    double x1 = x, y1 = y;
    int sign = 1;
    if (angle < 0) {
      int madv = rfm.stringWidth(label.toCharArray());
      x1 += madv * Math.cos(Math.abs(angle));
      y1 += madv * Math.sin(Math.abs(angle));
      x1 -= rfm.stringWidth(String.valueOf(label.charAt(0)).toCharArray())
          * Math.cos(Math.abs(angle));
      y1 -=
          rfm.stringWidth(String.valueOf(label.charAt(0)).toCharArray()) * Math
              .sin(Math.abs(angle)) + 5;
    }
    Bounds b = new Bounds();
    ClippedImageImpl ci = (ClippedImageImpl) GWT.create(ClippedImageImpl.class);

    for (int i = 0; i < label.length(); i++) {
      char c = label.charAt(i);
      rfm.getBounds(b, c);
      Element elem = (Element)ci.createStructure(rfm.url, (int) b.x, (int) (b.y + 1),
          (int) b.width, (int) b.height);

      DOM.setStyleAttribute(elem, "position", "absolute");
      DOM.setStyleAttribute(elem, "left", (x1 - layer.bounds.x) + "px");
      DOM.setStyleAttribute(elem, "top", (y1 - layer.bounds.y) + "px");
      DOM.setElementProperty(elem, "letter", "" + c);
      DOM.appendChild(layerElem, elem);
      int adv = rfm.getAdvance(c);
      x1 += sign * adv * Math.cos(angle);
      y1 += sign * adv * Math.sin(angle);
    }
  }

  private native Element getDocumentElement() /*-{
         return $doc.body;
    }-*/;

  private Element getMetricDiv() {
    if (metricDiv == null) {
      Element div = DOM.createDiv();
      DOM.setStyleAttribute(div, "position", "absolute");
      DOM.setStyleAttribute(div, "padding", "0px");
      DOM.setStyleAttribute(div, "margin", "0px");
      DOM.setStyleAttribute(div, "border", "0px");
      DOM.setStyleAttribute(div, "visibility", "hidden");
      DOM.appendChild(
          ((CssGssViewSupport) getCanvas().getView()).getGssCssElement(), div);
      metricDiv = div;
    }
    return metricDiv;
  }
}