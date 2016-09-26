package com.herthrone.object;

import com.google.common.base.MoreObjects;
import com.herthrone.base.Minion;
import com.herthrone.base.Resettable;

import java.util.HashMap;
import java.util.Map;

public class AuraBuff implements Resettable {

  private Map<Minion, Integer> minionToBuffMapping;
  public int accumulatedBuffValue;

  public AuraBuff() {
    this.minionToBuffMapping = new HashMap<>();
    this.accumulatedBuffValue = 0;
  }

  public void add(final Minion minion, final int buffVal) {
    final Integer existingBuff = minionToBuffMapping.get(minion);
    minionToBuffMapping.put(minion, buffVal);
    final int gain = (existingBuff == null) ? buffVal : buffVal - existingBuff;
    accumulatedBuffValue += gain;
  }

  public void remove(final Minion minion) {
    final Integer existingBuff = minionToBuffMapping.remove(minion);
    final int loss = (existingBuff == null) ? 0 : existingBuff;
    accumulatedBuffValue -= loss;
  }

  @Override
  public void reset() {
    minionToBuffMapping.clear();
    accumulatedBuffValue = 0;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("accumulated", accumulatedBuffValue)
        .add("mapping", minionToBuffMapping)
        .toString();
  }
}
