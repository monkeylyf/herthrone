package com.herthrone.base;

import com.herthrone.object.BooleanMechanics;
import com.herthrone.object.EffectMechanics;
import com.herthrone.object.ValueAttribute;

/**
 * Created by yifeng on 4/2/16.
 */
public interface Weapon extends Card, Destroyable {

  int use();

  ValueAttribute getDurabilityAttr();

  ValueAttribute getAttackAttr();

  EffectMechanics getEffectMechanics();

  BooleanMechanics getBooleanMechanics();
}
