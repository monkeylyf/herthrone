package com.herthrone.base;

import com.herthrone.object.ValueAttribute;

public interface Weapon extends Card, Destroyable, Mechanic.ActiveMechanic {

  void use();

  ValueAttribute getDurabilityAttr();

  ValueAttribute getAttackAttr();

  ValueAttribute attackMovePoints();
}
