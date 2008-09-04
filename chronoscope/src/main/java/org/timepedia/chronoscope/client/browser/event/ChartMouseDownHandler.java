package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.libideas.event.client.MouseDownHandler;
import com.google.gwt.libideas.event.client.MouseDownEvent;
import com.google.gwt.libideas.event.client.MouseEvent;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;

/**
 * Handles the event where the user depresses a mouse button.
 * 
 * @author Chad Takahashi
 */
public final class ChartMouseDownHandler extends AbstractEventHandler<MouseDownHandler> implements MouseDownHandler {

  public void onMouseDown(MouseDownEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;
    int x = getLocalX(event);
    int y = getLocalY(event);

    boolean handled;
    
    if (event.getButton() == MouseEvent.Button.RIGHT) {
      // Ignore mouse right-click -- let browser handle event.
      handled = false;
    }
    else {
      // Set the UI component that initiated the drag or select
      CompoundUIAction uiAction = chartInfo.getCompoundUIAction();
      uiAction.setSource(getComponent(x, y, chart.getPlot()));
      if (event.isShiftKeyDown()) {
        chart.setCursor(Cursor.SELECTING);
        uiAction.setSelectAction(true);
      } else {
        chart.setCursor(Cursor.DRAGGING);
        uiAction.setSelectAction(false);
      }
      
      uiAction.setStartX(x);
      chart.setPlotFocus(x, y);
      handled = true;
    }

    chartInfo.setHandled(handled);
  }
}
