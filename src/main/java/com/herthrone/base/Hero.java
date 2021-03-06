package com.herthrone.base;

import com.google.common.base.Optional;
import com.herthrone.object.ManaCrystal;
import com.herthrone.object.ValueAttribute;

/**
 * Created by yifeng on 4/13/16.
 */
public interface Hero extends Creature {

  ValueAttribute armor();

  ValueAttribute heroPowerMovePoints();

  ManaCrystal manaCrystal();

  Optional<Weapon> getWeapon();

  void equip(final Weapon weapon);

  Spell getHeroPower();

  void setHeroPower(final Spell heroPower);

  void unequip();

  void playToEquip(final Weapon weapon);

  void playToEquip(final Weapon weapon, final Creature target);

  void useHeroPower(final Creature creature);
}
