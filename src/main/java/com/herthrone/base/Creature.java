package com.herthrone.base;

import com.google.common.base.Optional;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.stats.BooleanAttribute;
import com.herthrone.stats.IntAttribute;

/**
 * Created by yifengliu on 6/4/16.
 */
public interface Creature extends Card, Round {

  IntAttribute getHealthAttr();

  IntAttribute getHealthUpperAttr();

  IntAttribute getAttackAttr();

  IntAttribute getAttackMovePoints();

  Optional<BooleanAttribute> getBooleanAttribute(final ConstMechanic mechanic);

  void causeDamage(final Creature creature);

  void takeDamage(final int damage);

  boolean canDamage();

  boolean isDead();

  int getHealthLoss();
}
