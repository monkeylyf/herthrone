package com.herthrone.object;

import com.herthrone.base.Reset;

/**
 * Created by yifengliu on 5/5/16.
 */
public class Value implements Reset {

  private int value;

  public Value() {
    this(0);
  }

  public Value(final int value) {
    this.value = value;
  }

  @Override
  public void reset() {
    value = 0;
  }

  public void increase(final int delta) {
    value += delta;
  }

  public int value() {
    return value;
  }

  public void setTo(final int val) {
    this.value = val;
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }
}
