package com.herthrone.base;

import com.herthrone.stats.BooleanAttribute;
import com.herthrone.stats.IntAttribute;

/**
 * Created by yifeng on 4/2/16.
 */


public interface Minion extends BaseCard {

  IntAttribute getHealthAttr();

  IntAttribute getHealthUpperAttr();

  IntAttribute getAttackAttr();

  IntAttribute getMovePoints();

  BooleanAttribute getDamageImmunity();

  BooleanAttribute getFrozen();

  BooleanAttribute getDivineShield();

  BooleanAttribute getTaunt();

  BooleanAttribute getStealth();

  void causeDamage(Minion creature);

  void takeDamage(final int damage);

  boolean canDamage();

  boolean isDead();

  int getHealthLoss();
}
