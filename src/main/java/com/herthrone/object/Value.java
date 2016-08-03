package com.herthrone.object;

import com.herthrone.base.Resettable;

public class Value implements Resettable {

  private int value;

  Value() {
    this(0);
  }

  Value(final int value) {
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
