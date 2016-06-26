package com.herthrone.base;

import com.google.common.base.Optional;
import com.herthrone.objects.IntAttribute;

/**
 * Created by yifeng on 4/13/16.
 */
public interface Hero extends Creature {

  IntAttribute getArmorAttr();

  IntAttribute getHeroPowerMovePoints();

  Optional<Weapon> getWeapon();

  void arm(Weapon weapon);

  Spell getHeroPower();

  void UpdateHeroPower(final Spell heroPower);

  void disarm();

}
