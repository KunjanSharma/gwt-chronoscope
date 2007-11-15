package org.timepedia.chronoscope.client.canvas;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.render.LinearGradient;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Helper class for implementors getting started with Custom Canvas/Layer implementations
 * <p/>
 * not really intended for real use, just a placeholder with no performance benefit (indeed, a deficit).
 * DisplayLists will be most useful when dealing with
 * Flash/Silverlight/etc Canvases, where the DisplayList will be compactly encoded in a wire format
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class DefaultDisplayListImpl implements DisplayList {
    private String id;
    private Layer layer;

    public DisplayList createDisplayList(String id) {
        return layer.createDisplayList(id);
    }

    public LinearGradient createLinearGradient(double startx, double starty, double endx, double endy) {
        return layer.createLinearGradient(startx, starty, endx, endy);
    }

    public PaintStyle createPattern(String imageUri) {
        return layer.createPattern(imageUri);
    }

    public void setFillColor(final PaintStyle p) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setFillColor(p);
            }
        });

    }

    public void setCanvasPattern(final CanvasPattern canvasPattern) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setCanvasPattern(canvasPattern);
            }
        });

    }

    public void setRadialGradient(final RadialGradient radialGradient) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setRadialGradient(radialGradient);
            }
        });

    }

    public void setShadowColor(final Color shadowColor) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setShadowColor(shadowColor);
            }
        });

    }

    public void setStrokeColor(final PaintStyle p) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setStrokeColor(p);
            }
        });

    }

    public RadialGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1) {
        return layer.createRadialGradient(x0, y0, r0, x1, y1, r1);
    }

    public Bounds getBounds() {
        return layer.getBounds();
    }

    public void setLayerOrder(final int zorder) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setLayerOrder(zorder);
            }
        });

    }

    public Canvas getCanvas() {
        return layer.getCanvas();
    }

    public void fillRect(double startx, double starty, double width, double height) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.fillRect();
            }
        });

    }

    public double getHeight() {
        return layer.getHeight();
    }

    public float getLayerAlpha() {
        return layer.getLayerAlpha();
    }

    public String getLayerId() {
        return layer.getLayerId();
    }

    public int getLayerOrder() {
        return layer.getLayerOrder();
    }

    public int getScrollLeft() {
        return layer.getScrollLeft();
    }

    public void setScrollLeft(final int i) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setScrollLeft(i);
            }
        });

    }

    public void setLayerAlpha(final float alpha) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setLayerAlpha(alpha);
            }
        });

    }

    public String getStrokeColor() {
        return layer.getStrokeColor();
    }

    public String getTransparency() {
        return layer.getTransparency();
    }

    public void drawImage(final Layer layer2, final double sx, final double sy, final double swidth,
                          final double sheight, final double dx, final double dy, final double dwidth,
                          final double dheight) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.drawImage(layer2, sx, sy, swidth, sheight, dx, dy, dwidth, dheight);
            }
        });

    }

    public void drawRotatedText(final double x, final double y, final double v, final String label,
                                final String fontFamily, final String fontWeight, final String fontSize,
                                final String layerName, final Chart chart) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.drawRotatedText(x, y, v, label, fontFamily, fontWeight, fontSize, layerName, chart);
            }
        });

    }

    public double getWidth() {
        return layer.getWidth();
    }

    public void setFillColor(final String color) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setFillColor(color);
            }
        });

    }

    public void setLinearGradient(final LinearGradient lingrad) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setLinearGradient(lingrad);
            }
        });

    }

    public void fillRect() {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.fillRect();
            }
        });

    }

    public boolean isVisible() {
        return layer.isVisible();
    }

    public int rotatedStringHeight(String str, double rotationAngle, String fontFamily, String fontWeight,
                                   String fontSize) {
        return layer.rotatedStringHeight(str, rotationAngle, fontFamily, fontWeight, fontSize);
    }

    public int rotatedStringWidth(String str, double rotationAngle, String fontFamily, String fontWeight,
                                  String fontSize) {
        return layer.rotatedStringWidth(str, rotationAngle, fontFamily, fontWeight, fontSize);
    }

    private ArrayList cmdBuffer = new ArrayList();

    public DefaultDisplayListImpl(String id, Layer layer) {
        this.id = id;
        this.layer = layer;
    }

    public void execute() {
        Iterator i = cmdBuffer.iterator();
        while (i.hasNext()) {
            ( (Cmd) i.next() ).exec();
        }
    }

    public void beginPath() {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.beginPath();
            }
        });
    }

    public void setStrokeColor(final String color) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setStrokeColor(color);
            }
        });

    }

    public void setLineWidth(final double width) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setLineWidth(width);
            }
        });

    }

    public void save() {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.save();
            }
        });

    }

    public void moveTo(final double x, final double y) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.moveTo(x, y);
            }
        });

    }

    public void lineTo(final double x, final double y) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.lineTo(x, y);
            }
        });

    }

    public void stroke() {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.stroke();
            }
        });

    }

    public void restore() {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.restore();
            }
        });

    }

    public void setVisibility(final boolean visibility) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setVisibility(visibility);
            }
        });

    }

    public void closePath() {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.closePath();
            }
        });

    }

    public void fill() {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.fill();
            }
        });

    }

    public void setTransparency(final float value) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setTransparency(value);
            }
        });

    }

    public void setComposite(final int mode) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setComposite(mode);
            }
        });

    }

    public void translate(final double x, final double y) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.translate(x, y);
            }
        });

    }

    public void arc(final double x, final double y, final double radius, final double startAngle, final double endAngle,
                    final int clockwise) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.arc(x, y, radius, startAngle, endAngle, clockwise);
            }
        });

    }


    public void clearRect(final double x, final double y, final double width, final double height) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.clearRect(x, y, width, height);
            }
        });

    }

    public void rect(final double x, final double y, final double width, final double height) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.rect(x, y, width, height);
            }
        });

    }

    public void setShadowBlur(final double width) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setShadowBlur(width);
            }
        });

    }

    public void setShadowColor(final String color) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setShadowColor(color);
            }
        });

    }

    public void setShadowOffsetX(final double x) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setShadowOffsetX(x);
            }
        });

    }

    public void setShadowOffsetY(final double y) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setShadowOffsetY(y);
            }
        });

    }

    public void clip(final double x, final double y, final double width, final double height) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.clip(x, y, width, height);
            }
        });

    }

    public void drawImage(final Layer layer2, final double x, final double y, final double width, final double height) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.drawImage(layer2, x, y, width, height);
            }
        });

    }

    public void scale(final double sx, final double sy) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.scale(sx, sy);
            }
        });

    }

    public void clearTextLayer(final String textLayer) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.clearTextLayer(textLayer);
            }
        });

    }

    public void drawText(final double x, final double y, final String label, final String fontFamily,
                         final String fontWeight, final String fontSize, final String textLayer) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.drawText(x, y, label, fontFamily, fontWeight, fontSize, textLayer);
            }
        });

    }

    public void setTextLayerBounds(final String textLayer, final Bounds textLayerBounds) {
        cmdBuffer.add(new Cmd() {
            public void exec() {
                layer.setTextLayerBounds(textLayer, textLayerBounds);
            }
        });

    }

    public int stringHeight(String string, String font, String bold, String size) {
        return layer.stringHeight(string, font, bold, size);
    }

    public int stringWidth(String string, String font, String bold, String size) {
        return layer.stringWidth(string, font, bold, size);
    }

    interface Cmd {
        void exec();
    }
}
