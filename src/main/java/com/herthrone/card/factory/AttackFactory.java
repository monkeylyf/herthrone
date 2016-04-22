package com.herthrone.card.factory;

import com.herthrone.game.Battlefield;
import com.herthrone.base.Minion;
import com.herthrone.card.action.PhysicalDamage;

/**
 * Created by yifeng on 4/20/16.
 */
public class AttackFactory {

  private final Battlefield battlefield;

  public AttackFactory(final Battlefield battlefield) {
    this.battlefield = battlefield;
  }

  public Action getPhysicalDamageAction(final Minion attacker, final Minion attackee) {
    return new PhysicalDamage(attacker, attackee);
  }
}
