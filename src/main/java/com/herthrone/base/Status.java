package com.herthrone.base;

/**
 * Created by yifeng on 4/14/16.
 */
public class Status implements RoundStatusController {

  private boolean on;
  private double roundsToLast;

  public Status(final boolean on, final double roundsToLast) {
    this.on = on;
    this.roundsToLast = roundsToLast;
  }

  public Status(final boolean on) {
    this(on, Double.POSITIVE_INFINITY);
  }

  @Override
  public void nextRound() {
    roundsToLast -= 1;
    if (roundsToLast == 0) {
      reset();
    }
  }

  @Override
  public void reset() {
    this.on = false;
    this.roundsToLast = 0.0;
  }
}
