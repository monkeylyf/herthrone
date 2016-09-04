package com.herthrone.effect;

import com.google.common.base.Preconditions;
import com.herthrone.base.Creature;
import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;

public class HealEffect implements Effect {

  private final Creature healTarget;
  private final int healVolume;

  public HealEffect(final Creature healTarget, final int healVolume) {
    this.healTarget = healTarget;
    this.healVolume = healVolume;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.HEAL;
  }

  @Override
  public void act() {
    Preconditions.checkArgument(healVolume > 0, "heal volume must be positive");
    final int cappedHealVolume = Math.min(healVolume, healTarget.healthLoss());
    healTarget.health().increase(cappedHealVolume);
  }

  public Creature getTarget() {
    return healTarget;
  }
}
