package com.herthrone.factory;

import com.herthrone.base.Creature;
import com.herthrone.action.PhysicalDamage;
import com.herthrone.game.Battlefield;

/**
 * Created by yifeng on 4/20/16.
 */
public class AttackFactory {

  private final Battlefield battlefield;

  public AttackFactory(final Battlefield battlefield) {
    this.battlefield = battlefield;
  }

  public Action getPhysicalDamageAction(final Creature attacker, final Creature attackee) {
    return new PhysicalDamage(attacker, attackee);
  }
}
