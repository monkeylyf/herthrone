package com.herthrone.base;

import com.herthrone.object.BooleanMechanics;
import com.herthrone.object.ValueAttribute;

/**
 * Created by yifengliu on 6/4/16.
 */
public interface Creature extends Card, Round {

  ValueAttribute health();

  ValueAttribute maxHealth();

  ValueAttribute attack();

  ValueAttribute attackMovePoints();

  BooleanMechanics booleanMechanics();

  void dealDamage(final Creature creature);

  boolean takeDamage(final int damage);

  boolean canDamage();

  boolean isDead();

  void death();

  boolean canMove();

  int healthLoss();
}
