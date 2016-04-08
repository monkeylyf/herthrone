package com.herthrone.base;

/**
 * Created by yifeng on 4/5/16.
 */
public class Attribute {

  private int val;
  private int buffDelta;
  private final int rawVal;
  private boolean resetAfter;

  public Attribute(final int val) {
    this.val = this.rawVal = val;
    this.buffDelta = 0;
    this.resetAfter = false;
  }

  public int getVal() { return this.val + this.buffDelta; }
  public int getRawVal() { return this.val; }
  public void setBuff(final int buff) { this.buffDelta += buff; }
  public void reset() {
    this.val = this.rawVal;
    this.buffDelta = 0;
  }

  public void increaseToMax(final int val) {
    this.val = Math.max(this.val + val, this.rawVal + this.buffDelta);
  }
  public void increase() { this.val += 1; }
  public void increase(final int val) { this.val += val; }
  public void decrease() {
    this.val -= 1;
  }
  public void decrease(final int val) {
    this.val -= val;
  }

  public void setResetAfterRound() {
    this.resetAfter = true;
  }
  public void resetAfterRound() {
    if (this.resetAfter) {
      reset();
      this.resetAfter = false;
    }
  }
}
