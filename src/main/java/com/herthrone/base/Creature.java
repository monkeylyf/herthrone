package com.herthrone.base;

import com.herthrone.object.ValueAttribute;

/**
 * Created by yifengliu on 6/4/16.
 */
public interface Creature extends Card, Round, Mechanic.BooleanMechanic {

  ValueAttribute health();

  ValueAttribute maxHealth();

  ValueAttribute attack();

  ValueAttribute attackMovePoints();

  void dealDamage(final Creature creature);

  boolean takeDamage(final int damage);

  boolean canDamage();

  boolean isDead();

  void death();

  boolean canMove();

  int healthLoss();
}
