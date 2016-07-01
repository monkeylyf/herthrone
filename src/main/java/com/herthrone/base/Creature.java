package com.herthrone.base;

import com.herthrone.object.BooleanMechanics;
import com.herthrone.object.IntAttribute;

/**
 * Created by yifengliu on 6/4/16.
 */
public interface Creature extends Card, Round {

  IntAttribute health();

  IntAttribute maxHealth();

  IntAttribute attack();

  IntAttribute attackMovePoints();

  BooleanMechanics booleanMechanics();

  void dealDamage(final Creature creature);

  boolean takeDamage(final int damage);

  boolean canDamage();

  boolean isDead();

  void death();

  boolean canMove();

  int healthLoss();
}
