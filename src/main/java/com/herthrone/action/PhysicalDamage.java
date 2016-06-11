package com.herthrone.action;

import com.herthrone.base.Creature;
import com.herthrone.base.Effect;

/**
 * Created by yifeng on 4/4/16.
 */
public class PhysicalDamage implements Effect {

  private final Creature attacker;
  private final Creature attackee;

  public PhysicalDamage(final Creature attacker, final Creature attackee) {
    this.attacker = attacker;
    this.attackee = attackee;
  }

  @Override
  public void act() {
    attacker.causeDamage(attackee);
    attacker.takeDamage(attackee.getAttackAttr().getVal());
  }
}