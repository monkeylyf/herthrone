package com.herthrone.card.action;

import com.herthrone.base.Minion;
import com.herthrone.card.factory.Action;

/**
 * Created by yifeng on 4/4/16.
 */
public class PhysicalDamage implements Action {

  private final Minion attacker;
  private final Minion attackee;

  public PhysicalDamage(Minion attacker, Minion attackee) {
    this.attacker = attacker;
    this.attackee = attackee;
  }

  @Override
  public void act() {
    attacker.causeDamage(attackee);
    attacker.takeDamage(attackee.getAttackAttr().getVal());
  }
}