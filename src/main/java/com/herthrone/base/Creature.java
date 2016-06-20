package com.herthrone.base;

import com.herthrone.stats.BooleanMechanics;
import com.herthrone.stats.IntAttribute;

/**
 * Created by yifengliu on 6/4/16.
 */
public interface Creature extends Card, Round {

  IntAttribute getHealthAttr();

  IntAttribute getHealthUpperAttr();

  IntAttribute getAttackAttr();

  IntAttribute getAttackMovePoints();

  BooleanMechanics getBooleanMechanics();

  void causeDamage(final Creature creature);

  boolean takeDamage(final int damage);

  boolean canDamage();

  boolean isDead();

  boolean canMove();

  int getHealthLoss();
}
