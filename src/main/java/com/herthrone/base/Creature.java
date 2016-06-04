package com.herthrone.base;

import com.herthrone.stats.BooleanAttribute;
import com.herthrone.stats.IntAttribute;
import com.herthrone.stats.Round;

/**
 * Created by yifengliu on 6/4/16.
 */
public interface Creature extends BaseCard, Round {

  IntAttribute getHealthAttr();

  IntAttribute getHealthUpperAttr();

  IntAttribute getAttackAttr();

  IntAttribute getMovePoints();

  BooleanAttribute getDamageImmunity();

  BooleanAttribute getFrozen();

  BooleanAttribute getDivineShield();

  BooleanAttribute getTaunt();

  BooleanAttribute getStealth();

  void causeDamage(final Creature creature);

  void takeDamage(final int damage);

  boolean canDamage();

  boolean isDead();

  int getHealthLoss();
}
