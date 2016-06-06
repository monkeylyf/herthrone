package com.herthrone.stats;

import com.herthrone.base.Reset;
import com.herthrone.base.Round;

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
    this.on = true;
    this.roundUntilExpire = roundUntilExpire;
  }

  public boolean isOn() {
    return on;
  }

  @Override
  public void endTurn() {
    roundUntilExpire -= 1;
    if (roundUntilExpire == 0) {
      //reset();
    }
  }

  @Override
  public void startTurn() {

  }

  @Override
  public void reset() {
    on = false;
    roundUntilExpire = 0.0;
  }
}
