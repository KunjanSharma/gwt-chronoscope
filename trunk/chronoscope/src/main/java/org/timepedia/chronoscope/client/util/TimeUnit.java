package org.timepedia.chronoscope.client.util;

/**
 * Enumeration over common time units.
 * 
 * @author Chad Takahashi
 */
public enum TimeUnit {
  MS (1),
  TENTH_SEC (100),
  SEC (1000),
  MIN (SEC.ms * 60),
  HOUR (MIN.ms * 60),
  DAY (HOUR.ms * 24),
  WEEK (DAY.ms * 7),
  MONTH (DAY.ms * (365.2425 / 12.0)),
  YEAR (DAY.ms * 365.2425), // avg # days in year according to Gregorian calendar
  DECADE (YEAR.ms * 10),
  CENTURY (DECADE.ms * 10),
  MILLENIUM (CENTURY.ms * 10);
  
  private final double ms;
  
  private TimeUnit(double lengthInMilliseconds) {
    this.ms = lengthInMilliseconds;
  }
  
  /**
   * Returns this time interval in milliseconds.
   */
  public double ms() {
    return ms;
  }
  
  public static final void main(String[] args) {
    System.out.println((long)TimeUnit.YEAR.ms);
  }
}