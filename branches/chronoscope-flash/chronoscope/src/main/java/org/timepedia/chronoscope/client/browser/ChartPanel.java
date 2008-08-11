package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;
import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.gss.MockGssContext;
import org.timepedia.chronoscope.client.browser.flashcanvas.FlashView;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.exporter.client.Exportable;

/**
 * ChartPanel is a GWT Widget that intercepts events and translates them to the Chart interface as well as creating a
 * View instance for the chart to render with. All client-side browser-specific classes reside in the browser subpackage
 * to ensure the portability of the rest of Chronoscope to other environments (Applet, Servlet, etc)
 * <p/>
 * ChartPanel at the moment only handles XYPlots, but in the future will be extended to handle other chart types. See
 * the project wiki for more details.
 * <p/>
 * <p/>
 * A�simple way to construct a chart looks like this:
 * <p/>
 * <pre>
 * ChartPanel chartPanel = new ChartPanel(myDatasets);
 * RootPanel.get("someid").add(chartPanel);
 * </pre>
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class ChartPanel extends Widget
        implements ViewReadyCallback, WindowResizeListener, SafariKeyboardConstants, Exportable {

    private FlashView view;
    private boolean selectionMode;
    private int selStart;
    private boolean maybeDrag;
    private int startDragX;
    private Chart chart;
    private XYPlot plot;
    private String id;
    private int chartWidth;
    private int chartHeight;
    private ViewReadyCallback readyListener;


    /**
     * Instantiates a chart widget using the given DOM element as a container
     *
     * @param container
     * @param plot
     */
    public ChartPanel(Element container, XYPlot plot, int chartWidth, int chartHeight) {
        this(container, plot, chartWidth, chartHeight, null);
    }

    /**
     * Instantiates a chart widget using the given DOM element as a container, with a ViewReadyCallback
     *
     * @param container
     * @param plot
     */
    public ChartPanel(Element container, XYPlot plot, int chartWidth, int chartHeight,
                      ViewReadyCallback readyListener) {
        this.chartWidth = chartWidth;
        this.chartHeight = chartHeight;
        this.readyListener = readyListener;
        initElement(container);
        this.plot = plot;
        chart = new Chart();
        chart.setPlot(plot);

    }


    private void initElement(Element container) {
        setElement(container);
        addStyleName("chrono");
        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.KEYEVENTS);
        sinkEvents(Event.ONCLICK);
        sinkEvents(Event.ONDBLCLICK);
        sinkEvents(Event.ONMOUSEWHEEL);

        Window.addWindowResizeListener(this);
        disableContextMenu(getElement());

        id = DOM.getElementAttribute(container, "id");
        if (id == null || "".equals(id)) {
            id = Chronoscope.generateId();
            DOM.setElementAttribute(container, "id", id);
        }
    }

    /**
     * Instantiates a chart widget using the given DOM element as a container, creating a DefaultXYPlot using the
     * given datasets, with a ViewReadyCallback.
     *
     * @param container
     * @param datasets
     */
    public ChartPanel(Element container, XYDataset[] datasets, int chartWidth, int chartHeight,
                      ViewReadyCallback readyListener) {
        this.chartWidth = chartWidth;
        this.chartHeight = chartHeight;
        this.readyListener = readyListener;
        initElement(container);
        chart = new Chart();
        plot = new DefaultXYPlot(chart, datasets, true);
        chart.setPlot(plot);
    }


    /**
     * Instantiates a chart widget using the given DOM element as a container, creating a DefaultXYPlot using the
     * given datasets.
     *
     * @param container
     * @param datasets
     */
    public ChartPanel(Element container, XYDataset[] datasets, int chartWidth, int chartHeight) {
        this(container, datasets, chartWidth, chartHeight, null);
    }


    /**
     * Create a chart using the given datasets and an automatically generated container element
     *
     * @param datasets
     */
    public ChartPanel(XYDataset[] datasets, int chartWidth, int chartHeight) {
        this(DOM.createDiv(), datasets, chartWidth, chartHeight);
    }

    /**
     * Create a chart using the given XYPlot, and an automatically generated container element
     */
    public ChartPanel(XYPlot plot, int chartWidth, int chartHeight) {
        this(DOM.createDiv(), plot, chartWidth, chartHeight);
    }

    /**
     * Invoked when a BrowserView is finished construction and ready for rendering. Chart initialization is delayed
     * until view creation, because Chart initialization depends on measuring font metrics, CSS styles, and other
     * view centric operations.
     *
     * @param view
     */
    public void onViewReady(View view) {
        super.onAttach();

        chart.init(view, plot);
        Chronoscope.putChart(id, chart);
        if (readyListener != null) {
            readyListener.onViewReady(view);
        } else {
            chart.redraw();
        }
    }


    /**
     * When this widget is attached, a BrowserView is constructed. Views are responsible for providing rendering and CSS
     * services. A BrowserView may be created asynchronously (as is the case with Flash, Silverlight, and others) and
     * therefore super.onAttach() is not called until onViewReady is invoked.
     */
    protected void onAttach() {
        Element cssgss = DOM.createDiv();
        DOM.setStyleAttribute(cssgss, "width", "0px");
        DOM.setStyleAttribute(cssgss, "height", "0px");
//        DOM.setStyleAttribute(cssgss, "visibility", "hidden");
        DOM.setElementAttribute(cssgss, "id", DOM.getElementAttribute(getElement(), "id") + "style");
        DOM.setElementAttribute(cssgss, "class", "chrono");
        appendBody(cssgss);
        view = new FlashView(getElement(), chartWidth, chartHeight, true, new MockGssContext(cssgss), this);
        view.onAttach();
    }

    private native void appendBody(Element cssgss) /*-{
        $doc.body.appendChild(cssgss);
    }-*/;


    /**
     * Take over right-mouse button menu on browsers that support it.
     *
     * @param elem
     */
    private native void disableContextMenu(Element elem) /*-{
           elem.oncontextmenu=function(a,b) {
             this.@org.timepedia.chronoscope.client.browser.ChartPanel::fireContextMenu(Lcom/google/gwt/user/client/Event;)(a);
               return false;
           };

   }-*/;

    public void fireContextMenu(Event evt) {

        int x = DOM.eventGetClientX(evt);
        int y = DOM.eventGetClientY(evt) + getScrollTop();

        view.fireContextMenuEvent(x, y);
        DOM.eventCancelBubble(evt, true);
        DOM.eventPreventDefault(evt);
    }

    /**
     * Handle main Chronoscope navigation features and forward them to the Chart class.
     *
     * @param evt
     */
    public void onBrowserEvent(Event evt) {

        if (!isAttached()) {
            return;
        }

        int x = DOM.eventGetClientX(evt) - getAbsoluteLeft(getElement());

        int absTop = getAbsoluteTop(getElement());
        int y = DOM.eventGetClientY(evt) - absTop + getScrollTop();

        switch (DOM.eventGetType(evt)) {

            case Event.ONMOUSEDOWN:
                if (selectionMode || DOM.eventGetShiftKey(evt)) {
                    selStart = x;
                    selectionMode = true;
                } else {
                    maybeDrag = true;

                    startDragX = x;
                }
                chart.setPlotFocus(x, y);
                DOM.eventCancelBubble(evt, true);
                DOM.eventPreventDefault(evt);
                break;
            case Event.ONMOUSEOUT:
                chart.setAnimating(false);
                chart.redraw();
                break;

            case Event.ONMOUSEOVER:
                chart.setPlotFocus(x, y);
                view.focus();
                maybeDrag = false;
                break;
            case Event.ONMOUSEUP:
                if (selectionMode) {
                    selectionMode = false;
                    chart.setAnimating(false);
                    selStart = -1;
                    if (DOM.eventGetShiftKey(evt)) {
                        chart.zoomToHighlight();
                    }
                } else if (maybeDrag) {
                    view.pushHistory();
                    chart.setAnimating(false);
                    chart.redraw();
                }

                maybeDrag = false;
                view.focus();
                break;
            case Event.ONMOUSEMOVE:
                if (chart.isInsidePlot(x, y)) {
                    if (selectionMode && selStart > -1) {
                        chart.setAnimating(true);
                        chart.setHighlight(selStart, x);
                    } else {
                        if (maybeDrag && Math.abs(startDragX - x) > 10) {
                            chart.setAnimating(true);
                            chart.scrollPixels(startDragX - x);
                            startDragX = x;
                            DOM.eventCancelBubble(evt, true);
                            DOM.eventPreventDefault(evt);
                        } else {
                            chart.setHover(x, y);
                        }
                    }
                }
//              else if (maybeDrag && chart.getOverviewBounds().inside(x, y)) {
//                    chart.getOverviewAxis().drag(view, startDragX, x, y);
//                }
                break;

            case Event.ONKEYPRESS:

                int keyCode = DOM.eventGetKeyCode(evt);

                if (keyCode == 9) {
                    if (DOM.eventGetShiftKey(evt)) {
                        chart.prevFocus();
                    } else {
                        chart.nextFocus();
                    }
                    DOM.eventCancelBubble(evt, true);
                    DOM.eventPreventDefault(evt);
                } else if (keyCode == KeyboardListener.KEY_LEFT ||

                        keyCode == KeyboardListener.KEY_PAGEUP || keyCode == SAFARI_LEFT || keyCode == SAFARI_LEFT ||
                        keyCode == SAFARI_PGUP) {
                    chart.pageLeft(keyCode == KeyboardListener.KEY_PAGEUP || keyCode == SAFARI_PGUP ? 1.0 : 0.5);
                    DOM.eventCancelBubble(evt, true);
                    DOM.eventPreventDefault(evt);
                } else if (keyCode == KeyboardListener.KEY_RIGHT ||

                        keyCode == KeyboardListener.KEY_PAGEDOWN || keyCode == SAFARI_RIGHT ||
                        keyCode == SAFARI_RIGHT || keyCode == SAFARI_PDWN) {
                    chart.pageRight(keyCode == KeyboardListener.KEY_PAGEDOWN || keyCode == SAFARI_PDWN ? 1.0 : 0.5);
                    DOM.eventCancelBubble(evt, true);
                    DOM.eventPreventDefault(evt);
                } else if (keyCode == KeyboardListener.KEY_ENTER) {
                    chart.maxZoomToFocus();
                    DOM.eventCancelBubble(evt, true);
                    DOM.eventPreventDefault(evt);

                } else if (keyCode == KeyboardListener.KEY_UP || keyCode == 90 + 32 || keyCode == SAFARI_UP) {
                    chart.nextZoom();
                    DOM.eventCancelBubble(evt, true);
                    DOM.eventPreventDefault(evt);
                } else if (keyCode == KeyboardListener.KEY_DOWN || keyCode == SAFARI_DOWN || keyCode == 88 + 32) {
                    chart.prevZoom();
                    DOM.eventCancelBubble(evt, true);
                    DOM.eventPreventDefault(evt);
                } else if (keyCode == 83 + 32) {
                    selectionMode = !selectionMode;
                } else if (keyCode == KeyboardListener.KEY_BACKSPACE) {
                    DOM.eventCancelBubble(evt, true);
                    DOM.eventPreventDefault(evt);
                    History.back();

                } else if (keyCode == KeyboardListener.KEY_HOME || keyCode == SAFARI_HOME) {
                    chart.maxZoomOut();
                    DOM.eventCancelBubble(evt, true);
                    DOM.eventPreventDefault(evt);
                }
                break;
            case Event.ONCLICK:
                maybeDrag = false;
                chart.setAnimating(false);
                if (DOM.eventGetButton(evt) == Event.BUTTON_RIGHT) {

                    view.fireContextMenuEvent(x, y);

                } else if (chart.setFocus(x, y)) {
                    DOM.eventCancelBubble(evt, true);
                    DOM.eventPreventDefault(evt);
                } else if (chart.click(x, y)) {

                } else {
                    super.onBrowserEvent(evt);
                }

                view.focus();
                break;
            case Event.ONDBLCLICK:
                maybeDrag = false;
                chart.setAnimating(false);
                if (chart.maxZoomTo(x, y)) {
                    DOM.eventCancelBubble(evt, true);
                    DOM.eventPreventDefault(evt);
                } else {
                    super.onBrowserEvent(evt);
                }
                break;

            case Event.ONMOUSEWHEEL:
                int dir = DOM.eventGetMouseWheelVelocityY(evt);
                if (dir <= 0) {
                    onMouseWheelUp(dir);
                } else {
                    onMouseWheelDown(dir);
                }
                DOM.eventCancelBubble(evt, true);
                DOM.eventPreventDefault(evt);
                break;
            default:
                super.onBrowserEvent(evt);
        }
    }

    public void onMouseWheelUp(int intensity) {
        maybeDrag = false;
        chart.nextZoom();
    }

    public void onMouseWheelDown(int intensity) {
        maybeDrag = false;
        chart.prevZoom();
    }

    /**
     * Note: Window/Chart/View resizing doesn't work right now.
     *
     * @param width
     * @param height
     */
    public void onWindowResized(int width, int height) {

        Element elem = view.getElement();
        view.resize(DOM.getElementPropertyInt(elem, "clientWidth"), DOM.getElementPropertyInt(elem, "clientHeight"));
    }

    public void resetDrag(int amt) {

    }

    /**
     * @return
     * @gwt.export
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * May no longer be neccessarily now that some bugs got fixed.
     *
     * @param elem
     * @return
     */
    public static native int getAbsoluteLeft(Element elem) /*-{
          var left = 0;
          var curr = elem;

          while (curr.offsetParent) {
            left -= curr.scrollLeft;
            curr = curr.parentNode;
          }
          while (elem) {
            left += elem.offsetLeft;
            elem = elem.offsetParent;
          }
          return left;
        }-*/;

    /**
     * May no longer be neccessarily now that some bugs got fixed.
     *
     * @param elem
     * @return
     */
    public static native int getAbsoluteTop(Element elem) /*-{
          var top = 0;
          var curr = elem;
          while (curr.offsetParent) {
            top -= curr.scrollTop;
            curr = curr.parentNode;
          }
          while (elem) {
            top += elem.offsetTop;
            elem = elem.offsetParent;
          }
          return top;
        }-*/;

    /**
     * Gets the window's scroll left.
     *
     * @return window's scroll left
     */
    public static native int getScrollLeft() /*-{
          // Standard mode || Quirks mode.
          return $doc.documentElement.scrollLeft || $doc.body.scrollLeft
        }-*/;

    /**
     * Get the window's scroll top.
     *
     * @return the window's scroll top
     */
    public static native int getScrollTop() /*-{
          // Standard mode || Quirks mode.
          return $doc.documentElement.scrollTop || $doc.body.scrollTop;
        }-*/;


}