package com.herthrone.stats;

/**
 * Created by yifeng on 4/5/16.
 */
public class IntAttribute implements Round, Reset {

  private final int rawVal;
  private int val;
  private int tempBuff;
  private int permBuff;

  public IntAttribute(final int val) {
    this.val = this.rawVal = val;
    this.tempBuff = 0;
    this.permBuff = 0;
  }

  public int getVal() { return this.val + this.tempBuff + this.permBuff; }

  public void permIncrease(final int gain) { this.tempBuff += gain; }
  public void tempDecrease(final int loss) { this.tempBuff -= loss; }

  public void increase(final int gain) { this.permBuff += gain; }
  public void decrease(final int loss) { this.permBuff -= loss; }

  public void resetVal() { this.val = this.rawVal; }
  public void resetBuff() {
    this.tempBuff = 0;
    this.permBuff = 0;
  }

  @Override
  public void nextRound() {
    this.tempBuff = 0;
  }

  @Override
  public void reset() {
    resetVal();
    resetBuff();
  }
}
