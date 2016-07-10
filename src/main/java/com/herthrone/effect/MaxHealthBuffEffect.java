package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.constant.ConstEffectType;

/**
 * Created by yifengliu on 7/9/16.
 */
public class MaxHealthBuffEffect implements Effect {

  private final Minion minion;
  private final int gain;

  public MaxHealthBuffEffect(final Minion minion, final int gain) {
    this.minion = minion;
    this.gain = gain;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.MAX_HEALTH_BUFF;
  }

  @Override
  public void act() {
    minion.maxHealth().getPermanentBuff().increase(gain);
    minion.health().increase(gain);
  }
}
