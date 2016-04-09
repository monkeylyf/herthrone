package com.herthrone.action;

import com.herthrone.base.BaseCreature;

/**
 * Created by yifeng on 4/4/16.
 */
public class AttackAction implements Action {

  private final BaseCreature attacker;
  private final BaseCreature attackee;

  public AttackAction(BaseCreature attacker, BaseCreature attackee) {
    this.attacker = attacker;
    this.attackee = attackee;
  }

  @Override
  public void act() {
    this.attacker.causeDamage(this.attackee);
    this.attacker.takeDamage(this.attackee.getAttackAttr().getVal());
  }
}
