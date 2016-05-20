package com.herthrone.stats;

/**
 * Created by yifengliu on 5/5/16.
 */
public class Value {

  private int val;

  public Value(final int val) {
    this.val = val;
  }

  public Value() {
    this(0);
  }

  public void reset() {
    val = 0;
  }

  public void increase(final int gain) {
    val += gain;
  }

  public void decrease(final int loss) {
    val -= loss;
  }

  public int getVal() {
    return val;
  }

  public void setTo(final int val) {
    this.val = val;
  }
}
