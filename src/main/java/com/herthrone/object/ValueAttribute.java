package com.herthrone.object;

import com.herthrone.base.Minion;
import com.herthrone.base.Resettable;
import com.herthrone.base.Round;

import java.util.HashMap;
import java.util.Map;

public class ValueAttribute implements Resettable, Round {

  private final Buff buff;
  private final Value rawValue;
  private final AuraBuff auraBuff;
  private Value currentValue;

  public ValueAttribute(final int value) {
    this.currentValue = new Value(value);
    this.rawValue = new Value(value);
    this.auraBuff = new AuraBuff();
    this.buff = new Buff();
  }

  public ValueAttribute(final int value, final boolean initAsIs) {
    this(value);
    if (!initAsIs) {
      buff.temporaryBuff.increase(-value);
    }
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
    return currentValue.value() + buff.value() + auraBuff.accumulatedBuffValue;
  }

  public void reset() {
    currentValue.setTo(rawValue.value());
    buff.reset();
    auraBuff.reset();
  }

  public void set(final int valueToSet) {
    currentValue.setTo(valueToSet);
    if (buff.value() > 0) {
      buff.reset();
    }
  }

  public boolean isGreaterThan(final int value) {
    return value() > value;
  }

  public boolean isLessThan(final int value) {
    return value() < value;
  }

  @Override
  public void endTurn() {
    buff.endTurn();
    currentValue.setTo(rawValue.value());
  }

  @Override
  public String toString() {
    if (buff.value() != 0) {
      return String.format("%d(%d)", value(), buff.value());
    } else {
      return Integer.toString(value());
    }
  }

  @Override
  public void startTurn() {
    buff.startTurn();
  }

  public Value getTemporaryBuff() {
    return buff.temporaryBuff;
  }

  public Value getPermanentBuff() {
    return buff.permanentBuff;
  }

  public void addAuraBuff(final Minion minion, final int gain) {
    auraBuff.add(minion, gain);
  }

  public void removeAuraBuff(final Minion minion) {
    auraBuff.remove(minion);
  }

  public void resetBuff() {
    buff.reset();
  }

  private static class Buff implements Resettable, Round {

    public final Map<Minion, Integer> minionToTemporaryBuffMapping;
    public final Map<Minion, Integer> minionToPermanentBuffMapping;
    public final Value temporaryBuff;
    public final Value permanentBuff;

    public Buff() {
      this.minionToTemporaryBuffMapping = new HashMap<>();
      this.minionToPermanentBuffMapping = new HashMap<>();
      this.temporaryBuff = new Value();
      this.permanentBuff = new Value();
    }

    public int value() {
      return temporaryBuff.value() + permanentBuff.value();
    }

    @Override
    public void reset() {
      temporaryBuff.reset();
      permanentBuff.reset();
    }

    @Override
    public void endTurn() {
      temporaryBuff.reset();
    }

    @Override
    public void startTurn() {

    }
  }

}
