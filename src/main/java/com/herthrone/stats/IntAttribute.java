package com.herthrone.stats;

/**
 * Created by yifeng on 4/5/16.
 */
public class IntAttribute implements Round {

  public final Buff buff;
  private final int rawVal;
  private Value val;

  public IntAttribute(final int val) {
    this.val = new Value(val);
    this.rawVal = val;
    this.buff = new Buff();
  }

  public int getVal() {
    return val.getVal() + buff.getBuffVal();
  }

  public void increase(final int gain) {
    val.increase(gain);
  }

  public void decrease(final int loss) {
    val.decrease(loss);
  }

  public void reset() {
    val.setTo(rawVal);
    buff.reset();
  }

  @Override
  public void nextRound() {
    buff.temp.reset();
  }

  @Override
  public String toString() {
    if (buff.getBuffVal() != 0) {
      return String.format("%d(%d)", getVal(), buff.getBuffVal());
    } else {
      return Integer.toString(getVal());
    }
  }
}
