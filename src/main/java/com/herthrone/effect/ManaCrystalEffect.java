package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.object.ManaCrystal;

/**
 * Created by yifengliu on 7/18/16.
 */
public class ManaCrystalEffect implements Effect {

  private final ManaCrystal manaCrystal;
  private final int gain;

  public ManaCrystalEffect(final ManaCrystal manaCrystal, final int gain) {
    this.manaCrystal = manaCrystal;
    this.gain = gain;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.CRYSTAL;
  }

  @Override
  public void act() {
    manaCrystal.increase(gain);
  }
}
