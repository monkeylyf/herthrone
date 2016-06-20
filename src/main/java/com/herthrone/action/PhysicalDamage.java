package com.herthrone.action;

import com.herthrone.base.Creature;
import com.herthrone.base.Effect;
import com.herthrone.base.Minion;

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
    // TODO: refactor attacker/attackee design. This is very bad tight coupling.
    if (attacker instanceof Minion && attackee instanceof Minion) {
      attacker.causeDamage(attackee);
      attackee.causeDamage(attacker);
    } else {
      attacker.causeDamage(attackee);
      attacker.takeDamage(attackee.getAttackAttr().getVal());
    }
  }
}