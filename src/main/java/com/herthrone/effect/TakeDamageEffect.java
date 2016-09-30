package com.herthrone.effect;

import com.google.common.base.Preconditions;
import com.herthrone.base.Creature;
import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;

public class TakeDamageEffect implements Effect {

  private final Creature creature;
  private final int damage;

  public TakeDamageEffect(final Creature creature, final int damage) {
    this.creature = creature;
    this.damage = damage;
  }

  @Override
  public ConstEffectType effectType() {
    return null;
  }

  @Override
  public void act() {
    Preconditions.checkArgument(damage > 0);
    creature.takeDamage(damage);
  }
}
