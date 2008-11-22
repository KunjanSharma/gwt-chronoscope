package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * Represents one of the logical auxiliary axis panels located to the 
 * North/South/East/West of the main plot panel. Each concrete class 
 * encapsulates all of the resources and logic needed for rendering 
 * and UI interaction.
 * 
 * @author chad takahashi
 */
abstract class AuxiliaryPanel {
  protected DefaultXYPlot plot;
  protected View view;
  protected Layer layer;
  protected boolean enabled = true;
  
  protected abstract void drawHook();
  
  protected abstract void initHook();
  
  protected abstract void setEnabledHook(boolean enabled);
  
  /**
   * Calling this method will cause the next invocation of {@link #draw()}
   * to force a fresh rendering onto the layer in cases where subclasses
   * are caching previously drawn information.
   */
  public void clearDrawCaches() {
    // to be overridden by subclasses
  }
  
  /**
   * Draws this panel
   */
  public final void draw() {
    if (this.enabled) {
      drawHook();
    }
  }
  
  /**
   * Recalculates the positions of subpanels contained within this panel.
   */
  public abstract void layout();
  
  /**
   * Initializes the layer(s) onto which this panel is drawn.
   */
  public abstract void initLayer();
  
  public final void init() {
    ArgChecker.isNotNull(plot, "plot");
    ArgChecker.isNotNull(view, "view");
    initHook();
  }
  
  public boolean isEnabled() {
    return this.enabled;
  }
  
  public void setEnabled(boolean enabled) {
    if (this.enabled == enabled) {
      return;
    }
    
    this.enabled = enabled;
    setEnabledHook(enabled);
  }
  
  public void setPlot(DefaultXYPlot plot) {
    this.plot = plot;
  }
  
  public void setView(View view) {
    this.view = view;
  }
  
}
