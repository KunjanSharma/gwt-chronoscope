package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.EventHandler;

/**
 *
 */
public interface ChartDragEndHandler extends EventHandler {

  void onDragEnd(ChartDragEndEvent event);
}