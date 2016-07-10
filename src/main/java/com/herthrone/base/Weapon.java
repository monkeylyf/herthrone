package com.herthrone.base;

import com.herthrone.object.ValueAttribute;

/**
 * Created by yifeng on 4/2/16.
 */
public interface Weapon extends Card, Destroyable, Mechanic.TriggeringMechanic {

  void use();

  ValueAttribute getDurabilityAttr();

  ValueAttribute getAttackAttr();

}
