package com.herthrone.stats;

import com.herthrone.base.BaseCard;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yifeng on 4/22/16.
 */

public class AuraBuff<T extends BaseCard> {

  private final Map<T, Integer> buffs;
  private int accumulatedBuffs;

  public AuraBuff() {
    this.buffs = new HashMap<>();
    this.accumulatedBuffs = 0;
  }

  public void addBuff(final T card, final int buffVal) {
    buffs.put(card, buffVal);
    accumulatedBuffs += buffVal;
  }

  public int getBuffs() {
    return accumulatedBuffs;
  }

  public void clear() {
    buffs.clear();
    accumulatedBuffs = 0;
  }

  public void removeBuff(T card) {
    Integer buff = buffs.remove(card);
    if (buff != null) {
      accumulatedBuffs -= buff.intValue();
    }
  }
}
