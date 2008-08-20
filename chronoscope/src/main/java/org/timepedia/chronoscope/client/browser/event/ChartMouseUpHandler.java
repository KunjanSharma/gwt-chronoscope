package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.libideas.event.client.MouseEvent;
import com.google.gwt.libideas.event.client.MouseUpEvent;
import com.google.gwt.libideas.event.client.MouseUpHandler;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.browser.DOMView;

/**
 * Handles the event where the user releases the mouse button.
 *
 * @author Chad Takahashi
 */
public final class ChartMouseUpHandler
    extends AbstractEventHandler<MouseUpHandler> implements MouseUpHandler {

  public void onMouseUp(MouseUpEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;
    int x = getLocalX(event);
    int y = getLocalY(event);
    
    CompoundUIAction uiAction = chartInfo.getCompoundUIAction();
    if (uiAction.isSelecting()) {
      chart.setAnimating(false);
      chart.zoomToHighlight();
    } 
    else if (uiAction.isDragging() && x != uiAction.getStartX()) {
      ((DOMView) chart.getView()).pushHistory();
      chart.setAnimating(false);
      chart.redraw();
    }

    chartInfo.getCompoundUIAction().cancel();
    chart.setCursor(Cursor.DEFAULT);

    ((DOMView) chart.getView()).focus();
    
    if (event.getButton() == MouseEvent.Button.RIGHT) {
      chart.getView().fireContextMenuEvent(x, y);
    }
    
    chartInfo.setHandled(true);
  }
}