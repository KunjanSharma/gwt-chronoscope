package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Interval;

/**
 * Provides most of the implementation necessary for an N-tuple dataset backed
 * by {@link Array2D} objects.  Subclasses only need to provide implementation
 * for {@link #loadTupleData(DatasetRequest, Array2D[])}.
 * 
 * @author Chad Takahashi
 */
public abstract class AbstractArrayDataset<T extends Tuple2D> extends AbstractDataset<T> {

  /**
   * Stores the multiresolution (mipmapped) values for each element ..
   */
  protected Array2D[] dimensions;

  protected double minRange, maxRange;

  /**
   * Concrete subclass provides implementation for loading the specified 
   * tuple data into the target list of Array2D objects.
   */
  protected abstract void loadTupleData(DatasetRequest tupleData);
  
  /**
   * Constructs an {@link Dataset} from the specified request object.
   */
  public AbstractArrayDataset(DatasetRequest request) {
    ArgChecker.isNotNull(request, "request");
    request.validate();
    axisId = (String) ArgChecker.isNotNull(request.getAxisId(), "axisId");
    rangeLabel = (String) ArgChecker.isNotNull(request.getLabel(), "label");
    identifier = request.getIdentifier();
    
    loadTupleData(request);
    validate(dimensions);

    if (Double.isNaN(request.getApproximateMinimumInterval())) {
      approximateMinimumInterval = calcMinInterval(dimensions[0]);
    } else {
      approximateMinimumInterval = request.getApproximateMinimumInterval();
    }

    // Assign rangeBottom and rangeTop
    final int numLevels = dimensions[0].numRows();
    minRange = request.getRangeBottom();
    maxRange = request.getRangeTop();
    if (Double.isNaN(maxRange) || Double.isNaN(minRange)) {
      // Question: Will the max range at mip level 1 or greater ever be greater
      // than the max range at mip level 0? If not, then can we just find
      // min/max values at level 0?
      Interval rangeInterval = calcRangeInterval(dimensions[1], numLevels);
      minRange = rangeInterval.getStart();
      maxRange = rangeInterval.getEnd();
    }
  }

  public double getApproximateMinimumInterval() {
    return approximateMinimumInterval;
  }

  public T getFlyweightTuple(int index) {
    return getFlyweightTuple(index, 0);
  }

  public abstract T getFlyweightTuple(int index, int mipLevel);

  public double getMaxValue(int coordinate) {
    switch (coordinate) {
      case 0:
        return dimensions[0].get(0, getNumSamples() - 1);
      case 1:
        return this.maxRange;
      default:
        throw new IllegalArgumentException("coordinate out of range: " + coordinate);
    }
  }
  
  public double getMinValue(int coordinate) {
    switch (coordinate) {
      case 0:
        return dimensions[0].get(0, 0);
      case 1:
        return this.minRange;
      default:
        throw new IllegalArgumentException("coordinate out of range: " + coordinate);
    }
  }

  public int getNumSamples(int mipLevel) {
    return dimensions[0].numColumns(mipLevel);
  }

  public double getX(int index, int mipLevel) {
    return dimensions[0].get(mipLevel, index);
  }

  /**
   * Calculates the bottom and top of the range values in the specified dataset.
   */
  private Interval calcRangeInterval(Array2D multiRange, int numLevels) {
    // Calculate min and max range values across all resolutions
    double lo = Double.POSITIVE_INFINITY;
    double hi = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < numLevels; i++) {
      for (int j = 0; j < multiRange.numColumns(i); j++) {
        double value = multiRange.get(i, j);
        lo = Math.min(lo, value);
        hi = Math.max(hi, value);
      }
    }

    return new Interval(lo, hi);
  }

  /**
   * Validates multiDomain and multiRange objects.
   */
  private static void validate(Array2D[] mipmappedTupleData) {
    ArgChecker.isNotNull(mipmappedTupleData, "mipmappedTupleData");
    
    Array2D[] a = mipmappedTupleData;
    for (int i = 1; i < mipmappedTupleData.length; i++) {
      if (!a[i].isSameSize(a[i - 1])) {
        throw new IllegalArgumentException("Dimensions of mipmapped data at index " + i + 
            " differ from index " + (i - 1));
      }
    }
  }

  /**
   * Returns the smallest domain interval at row 0 in the specified Array2D object.
   * If only 1 column exists at row 0, then 0 is returned as the minimum interval.
   */
  private static double calcMinInterval(Array2D a) {
    double min = Double.MAX_VALUE;
    final int numColumns = a.numColumns(0);
    
    if (numColumns < 2) {
      // An interval requires at least 2 points, so in this case, just return 0.
      min = 0.0;
      //throw new RuntimeException("Array2D must have at least 2 columns at MIP level 0: " + numColumns);
    }
    else {
      double prevValue = a.get(0, 0);
      for (int i = 1; i < numColumns; i++) {
        double currValue = a.get(0, i);
        min = Math.min(min, currValue - prevValue);
        prevValue = currValue;
      }
    }
    
    return min;
  }

}