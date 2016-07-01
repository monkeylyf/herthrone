package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.object.ManaCrystal;

/**
 * Created by yifengliu on 6/27/16.
 */
public class OverloadEffect implements Effect {

  private final ManaCrystal manaCrystal;
  private final int lockValue;

  public OverloadEffect(final ManaCrystal manaCrystal, final int lockValue) {
    this.manaCrystal = manaCrystal;
    this.lockValue = lockValue;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.CRYSTAL;
  }

  @Override
  public void act() {
    manaCrystal.overload(lockValue);
  }
}
