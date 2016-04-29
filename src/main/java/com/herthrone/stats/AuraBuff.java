package com.herthrone.stats;

import com.herthrone.base.BaseCard;
import com.herthrone.base.Minion;

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
    this.buffs.put(card, buffVal);
    this.accumulatedBuffs += buffVal;
  }

  public int getBuffs() {
    return this.accumulatedBuffs;
  }

  public void clear() {
    this.buffs.clear();
    this.accumulatedBuffs = 0;
  }

  public void removeBuff(T card) {
    Integer buff = this.buffs.remove(card);
    if (buff != null) {
      this.accumulatedBuffs -= buff.intValue();
    }
  }
}
