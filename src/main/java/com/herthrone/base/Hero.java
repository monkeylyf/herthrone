package com.herthrone.base;

import com.google.common.base.Optional;
import com.herthrone.stats.IntAttribute;

/**
 * Created by yifeng on 4/13/16.
 */
public interface Hero extends Creature {

  IntAttribute getArmorAttr();

  Optional<Weapon> getWeapon();

  void arm(Weapon weapon);

  void disarm();

}
