package com.herthrone.stats;

/**
 * Created by yifeng on 4/5/16.
 */
public class Attribute implements StatsPerRound {

  private int val;
  private final int rawVal;
  private int buffDelta;
  private double duration;

  public Attribute(final int val, final double duration) {
    this.val = this.rawVal = val;
    this.buffDelta = 0;
    this.duration = duration;
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

  public void setDuration(final int duration) {
    this.duration = duration;
  }

  @Override
  public void nextRound() {
    this.duration -= 1;
    if (this.duration == 0) {
      resetBuff();
    }
  }

  @Override
  public void reset() {
    resetVal();
    resetBuff();
  }
}
