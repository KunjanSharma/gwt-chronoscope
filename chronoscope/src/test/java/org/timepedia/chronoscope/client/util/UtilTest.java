package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;

/**
 * @author Chad Takahashi
 *
 */
public class UtilTest extends TestCase {

  public void testBinarySearch() {
    double[] domain = new double[] {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    Dataset<Tuple2D> ds = new JUnitDataset(domain);
    
    // Case 1: Verify that exact key matches are found
    for (int i = 0; i < domain.length; i++) {
      assertEquals(i, Util.binarySearch(ds, domain[i], 0));
    }
    
    // Case 2: Verify that a non-matching key returns the index to the closest
    // domain value to the right.
    for (int i = 0; i < domain.length - 1; i++) {
      // Pick a search key that's between index [i] and [i+1]
      double searchKey = (domain[i] + domain[i + 1]) / 2.0;
      int expectedIndex = i + 1; // closest matching index to the right
      assertEquals(expectedIndex, Util.binarySearch(ds, searchKey, 0));
    }
    
    // Case 3: search key that's smaller than domain[0] should return index 0
    assertEquals(0, Util.binarySearch(ds, domain[0] - 1.0, 0));

    // Case 4: search key that's greater than domain[lastIndex] should
    // return lastIndex.
    final int lastIndex = domain.length - 1;
    assertEquals(lastIndex, Util.binarySearch(ds, domain[lastIndex] + 1.0, 0));
  }
  
  public void testIsEqual() {
    Integer x = new Integer(42);
    
    assertTrue(Util.isEqual(null, null));
    assertTrue(Util.isEqual(x, x)); // same reference
    assertTrue(Util.isEqual(new Integer(10), new Integer(10))); // same value
  
    assertFalse(Util.isEqual(new Integer(10), new Integer(12)));
    assertFalse(Util.isEqual(null, x));
    assertFalse(Util.isEqual(x, null));
  }
  
  
  private static final class JUnitDataset implements Dataset<Tuple2D> {
    private double[] domain;
    
    public JUnitDataset(double[] domain) {
      this.domain = domain;
    }
    
    public double getApproximateMinimumInterval() {
      throw new UnsupportedOperationException();
    }

    public String getAxisId() {
      throw new UnsupportedOperationException();
    }

    public double getDomainBegin() {
      throw new UnsupportedOperationException();
    }

    public double getDomainEnd() {
      throw new UnsupportedOperationException();
    }

    public String getIdentifier() {
      throw new UnsupportedOperationException();
    }

    public int getNumSamples() {
      return domain.length;
    }

    public int getNumSamples(int level) {
      return getNumSamples();
    }

    public String getRangeLabel() {
      throw new UnsupportedOperationException();
    }

    public double getX(int index) {
      return domain[index];
    }

    public double getX(int index, int level) {
      return getX(index);
    }

    public Tuple2D getFlyweightTuple(int index) {
      throw new UnsupportedOperationException();
    }

    public Tuple2D getFlyweightTuple(int index, int mipLevel) {
      throw new UnsupportedOperationException();
    }

    public double getMaxValue(int coordinate) {
      throw new UnsupportedOperationException();
    }

    public double getMinValue(int coordinate) {
      throw new UnsupportedOperationException();
    }
  }
}
