package com.herthrone.object;

import com.herthrone.base.Minion;
import com.herthrone.base.Reset;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yifeng on 4/22/16.
 */

public class AuraBuff implements Reset {

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
