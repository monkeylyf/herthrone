package com.herthrone.card.action;

import com.herthrone.card.factory.Action;
import com.herthrone.base.Minion;

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
    this.attacker.causeDamage(this.attackee);
    this.attacker.takeDamage(this.attackee.getAttackAttr().getVal());
  }
}
