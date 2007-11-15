package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.util.Util;

/**
 * Implements a XYPlotRenderer that uses multiresolution datasets to minimize the amount of XYRenderer invocations
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class ScalableXYPlotRenderer extends XYPlotRenderer {
    public ScalableXYPlotRenderer(XYPlot plot) {
        super(plot);
    }

    public void drawDataset(Layer can, XYDataset dataSet, XYRenderer renderer, int seriesNum, XYPlot plot) {

        int focusSeries = plot.getFocusSeries();
        int focusPoint = plot.getFocusPoint();
        int hoverSeries = plot.getHoverSeries();
        int hoverPoint = plot.getHoverPoint();

        boolean disabled = focusSeries != -1 && focusSeries != seriesNum;
        int domainStart, domainEnd;
        int mipLevel = -1;

        double domainOrigin = plot.getDomainOrigin();
        double currentDomain = plot.getCurrentDomain();
        do {
            mipLevel++;

            domainStart = Util.binarySearch(dataSet, domainOrigin, mipLevel);
            domainEnd = Util.binarySearch(dataSet, domainOrigin + currentDomain, mipLevel);
        } while (domainEnd - domainStart > Math.min(plot.getRenderer(seriesNum).getMaxDrawableDatapoints(plot),
                                                    plot.getMaxDrawableDataPoints()));

        plot.setCurrentDatasetLevel(seriesNum, mipLevel);


        int inc = 1;


        double rangeMin = Double.MAX_VALUE;
        double rangeMax = Double.MIN_VALUE;
        for (int i = domainStart; i <= domainEnd; i++) {
            double val = dataSet.getY(i, mipLevel);
            rangeMin = Math.min(rangeMin, val);
            rangeMax = Math.max(rangeMax, val);
        }

        plot.getRangeAxis(seriesNum).setVisibleRange(rangeMin, rangeMax);

        renderer.beginCurve(plot, can, false, disabled);

        int end = Math.min(domainEnd + 1, dataSet.getNumSamples(mipLevel));
        for (int i = Math.max(0, domainStart - 1); i < end; i += inc) {
            double x = dataSet.getX(i, mipLevel);
            double y = dataSet.getY(i, mipLevel);
            boolean focused = focusSeries == seriesNum && focusPoint == i;
            boolean hovered = hoverSeries == seriesNum && hoverPoint == i;
            renderer.drawCurvePart(plot, can, x, y, seriesNum, focused, hovered, false, disabled);
        }
        renderer.endCurve(plot, can, false, disabled, seriesNum);

        renderer.beginPoints(plot, can, false, disabled);

        for (int i = Math.max(0, domainStart - 2); i < Math.min(domainEnd + 1, dataSet.getNumSamples(mipLevel));
                i += inc) {
            double x = dataSet.getX(i, mipLevel);
            double y = dataSet.getY(i, mipLevel);
            boolean focused = focusSeries == seriesNum && focusPoint == i;
            boolean hovered = hoverSeries == seriesNum && hoverPoint == i;
            renderer.drawPoint(plot, can, x, y, seriesNum, focused, hovered, false, disabled);
        }
        renderer.endPoints(plot, can, false, disabled, seriesNum);


    }
}
