package com.herthrone.card.factory;

import com.herthrone.base.Battlefield;
import com.herthrone.base.Minion;
import com.herthrone.card.action.PhysicalDamage;

import java.util.List;

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
