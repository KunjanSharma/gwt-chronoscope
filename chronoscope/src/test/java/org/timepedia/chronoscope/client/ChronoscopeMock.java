package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.ScalableXYPlotRenderer;
import org.timepedia.chronoscope.client.render.XYPlotRenderer;

/**
 *
 */
public class ChronoscopeMock {

  public static MockChartPanel createTimeseriesChart(XYDataset[] ds, int width,
      int height) {

    Datasets<XYDataset> datasets = new Datasets<XYDataset>(ds);
    XYPlotRenderer plotRenderer = new ScalableXYPlotRenderer();

    DefaultXYPlot plot = new DefaultXYPlot();
    plot.setDatasets(datasets);
    plot.setPlotRenderer(plotRenderer);

    return new MockChartPanel(plot, width, height);
  }
}
