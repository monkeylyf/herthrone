package com.herthrone.base;

import com.herthrone.object.BooleanMechanics;
import com.herthrone.object.EffectMechanics;
import com.herthrone.object.IntAttribute;

/**
 * Created by yifeng on 4/2/16.
 */
public interface Weapon extends Card {

  int use();

  IntAttribute getDurabilityAttr();

  IntAttribute getAttackAttr();

  EffectMechanics getEffectMechanics();

  BooleanMechanics getBooleanMechanics();
}
