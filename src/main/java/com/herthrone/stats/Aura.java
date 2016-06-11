package com.herthrone.stats;

import com.herthrone.base.Card;
import com.herthrone.base.Minion;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yifeng on 4/22/16.
 */

public class Aura {

  private final Map<Minion, Integer> buffs;
  private int accumulatedBuffs;

  public Aura() {
    this.buffs = new HashMap<>();
    this.accumulatedBuffs = 0;
  }

  public void add(final Minion card, final int buffVal) {
    buffs.put(card, buffVal);
    accumulatedBuffs += buffVal;
  }

  public int get() {
    return accumulatedBuffs;
  }

  public void clear() {
    buffs.clear();
    accumulatedBuffs = 0;
  }

  public void remove(final Minion card) {
    Integer buff = buffs.remove(card);
    if (buff != null) {
      accumulatedBuffs -= buff.intValue();
    }
  }
}
