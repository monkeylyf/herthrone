package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.base.Hero;
import com.herthrone.base.Weapon;
import com.herthrone.constant.ConstEffectType;

public class EquipWeaponEffect implements Effect {

  private final Hero hero;
  private final Weapon weapon;

  public EquipWeaponEffect(final Hero hero, final Weapon weapon) {
    this.hero = hero;
    this.weapon = weapon;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.EQUIP_WEAPON;
  }

  @Override
  public void act() {
    hero.equip(weapon);
  }
}
