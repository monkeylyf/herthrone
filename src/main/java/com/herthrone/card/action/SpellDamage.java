package com.herthrone.card.action;

import com.herthrone.action.Action;
import com.herthrone.base.Minion;

/**
 * Created by yifeng on 4/13/16.
 */
public class SpellDamage implements Action {

  private final int damage;
  private final Minion attackee;

  public SpellDamage(final int damage, final Minion attackee) {
    this.damage = damage;
    this.attackee = attackee;
  }

  @Override
  public void act() {
    this.attackee.takeDamage(this.damage);
  }
}
