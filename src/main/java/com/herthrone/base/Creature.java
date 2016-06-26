package com.herthrone.base;

import com.herthrone.objects.BooleanMechanics;
import com.herthrone.objects.IntAttribute;

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

  void death();

  boolean canMove();

  int getHealthLoss();
}
