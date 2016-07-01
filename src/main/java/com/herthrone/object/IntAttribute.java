package com.herthrone.object;

import com.herthrone.base.Reset;
import com.herthrone.base.Round;

/**
 * Created by yifeng on 4/5/16.
 */
public class IntAttribute implements Reset, Round {

  public final Buff buff;
  private final int rawValue;
  private Value currentValue;

  public IntAttribute(final int value) {
    this.currentValue = new Value(value);
    this.rawValue = value;
    this.buff = new Buff();
  }

  public void increase(final int gain) {
    currentValue.increase(gain);
  }

  public void decrease(final int loss) {
    currentValue.increase(-loss);
  }

  public boolean isPositive() {
    return value() > 0;
  }

  public int value() {
    return currentValue.value() + buff.value();
  }

  public void reset() {
    currentValue.setTo(rawValue);
    buff.reset();
  }

  public boolean isNoGreaterThan(final int value) {
    return value() <= value;
  }

  @Override
  public void endTurn() {
    buff.endTurn();
  }

  @Override
  public void startTurn() {
    buff.startTurn();
  }

  @Override
  public String toString() {
    if (buff.value() != 0) {
      return String.format("%d(%d)", value(), buff.value());
    } else {
      return Integer.toString(value());
    }
  }
}
