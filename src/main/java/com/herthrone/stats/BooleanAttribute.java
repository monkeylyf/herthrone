package com.herthrone.stats;

/**
 * Created by yifeng on 4/14/16.
 */
public class BooleanAttribute implements Round, Reset {

  private boolean on;
  private double roundUntilExpire;

  public BooleanAttribute(final boolean on, final double roundUntilExpire) {
    this.on = on;
    this.roundUntilExpire = roundUntilExpire;
  }

  public BooleanAttribute(final boolean on) {
    this(on, Double.POSITIVE_INFINITY);
  }

  public void on(final double roundUntilExpire) {
    this.on = on;
    this.roundUntilExpire = roundUntilExpire;
  }

  public boolean isOn() {
    return this.on;
  }

  @Override
  public void nextRound() {
    this.roundUntilExpire -= 1;
    if (this.roundUntilExpire == 0) {
      //reset();
    }
  }

  @Override
  public void reset() {
    this.on = false;
    this.roundUntilExpire = 0.0;
  }
}
