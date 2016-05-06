package com.herthrone.stats;

/**
 * Created by yifeng on 4/5/16.
 */
public class IntAttribute implements Round {

  private final int rawVal;
  private Value val;

  public final Buff buff;

  public IntAttribute(final int val) {
    this.val = new Value(val);
    this.rawVal = val;
    this.buff = new Buff();
  }

  public int getVal() { return this.val.getVal() + this.buff.getBuffVal(); }
  public void increase(final int gain) { this.val.increase(gain); }
  public void decrease(final int loss) { this.val.decrease(loss); }
  public void reset() {
    this.val.setTo(this.rawVal);
    this.buff.reset();
  }

  @Override
  public void nextRound() {
    this.buff.temp.reset();
  }

}
