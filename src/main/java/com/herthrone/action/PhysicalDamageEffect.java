package com.herthrone.action;

import com.herthrone.base.Creature;
import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.constant.ConstEffectType;

/**
 * Created by yifeng on 4/4/16.
 */
public class PhysicalDamageEffect implements Effect {

  private final Creature attacker;
  private final Creature attackee;

  public PhysicalDamageEffect(final Creature attacker, final Creature attackee) {
    this.attacker = attacker;
    this.attackee = attackee;
  }

  @Override
  public ConstEffectType getEffectType() {
    return ConstEffectType.ATTACK;
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