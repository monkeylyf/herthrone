package com.herthrone.base;

/**
 * Created by yifeng on 4/5/16.
 */
public class Attribute implements RoundStatusController {

  private int val;
  private final int rawVal;
  private int buffDelta;
  private double roundsToLast;

  public Attribute(final int val, final double roundsToLast) {
    this.val = this.rawVal = val;
    this.buffDelta = 0;
    this.roundsToLast = roundsToLast;
  }

  public Attribute(final int val) {
    this(val, Double.POSITIVE_INFINITY);
  }

  public int getVal() { return this.val + this.buffDelta; }

  public void buff(final int val) { this.buffDelta += val; }
  public void increase(final int gain) { this.val += gain; }
  public void decrease(final int loss) { this.val -= loss; }

  public void resetVal() { this.val = this.rawVal; }
  public void resetBuff() { this.buffDelta = 0; }

  @Override
  public void nextRound() {
    this.roundsToLast -= 1;
    if (this.roundsToLast == 0) {
      resetBuff();
    }
  }

  @Override
  public void reset() {
    resetVal();
    resetBuff();
  }
}
