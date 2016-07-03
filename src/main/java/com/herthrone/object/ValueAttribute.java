package com.herthrone.object;

import com.herthrone.base.Minion;
import com.herthrone.base.Reset;
import com.herthrone.base.Round;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yifeng on 4/5/16.
 */
public class ValueAttribute implements Reset, Round {

  private final Buff buff;
  private final int rawValue;
  private Value currentValue;

  public ValueAttribute(final int value) {
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

  public Value getTemporaryBuff() {
    return buff.temporaryBuff;
  }

  public Value getPermanentBuff() {
    return buff.permanentBuff;
  }

  public void resetBuff() {
    buff.reset();
  }

  private static class Buff implements Reset, Round {

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

  private static class AuraBuff implements Reset {

    private final Map<Minion, Integer> buffs;
    private int accumulatedBuffValue;

    public AuraBuff() {
      this.buffs = new HashMap<>();
      this.accumulatedBuffValue = 0;
    }

    public void add(final Minion card, final int buffVal) {
      buffs.put(card, buffVal);
      accumulatedBuffValue += buffVal;
    }

    public int value() {
      return accumulatedBuffValue;
    }

    public void remove(final Minion card) {
      final Integer buff = buffs.remove(card);
      if (buff != null) {
        accumulatedBuffValue -= buff.intValue();
      }
    }

    @Override
    public void reset() {
      buffs.clear();
      accumulatedBuffValue = 0;
    }
  }
}
