package com.herthrone.base;

import com.google.common.base.Optional;
import com.herthrone.object.IntAttribute;

/**
 * Created by yifeng on 4/13/16.
 */
public interface Hero extends Creature {

  IntAttribute armor();

  IntAttribute heroPowerMovePoints();

  Optional<Weapon> getWeapon();

  void equip(final Weapon weapon);

  Spell getHeroPower();

  void setHeroPower(final Spell heroPower);

  void unequip();

  void playToEquip(final Weapon weapon);

  void useHeroPower(final Creature creature);
}
