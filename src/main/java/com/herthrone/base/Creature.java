package com.herthrone.base;

import com.herthrone.object.ValueAttribute;

public interface Creature extends Card, Round, Targetable, Mechanic.StaticMechanic {

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
