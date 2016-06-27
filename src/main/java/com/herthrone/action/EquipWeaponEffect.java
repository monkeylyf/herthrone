package com.herthrone.action;

import com.herthrone.base.Effect;
import com.herthrone.base.Hero;
import com.herthrone.base.Weapon;
import com.herthrone.constant.ConstEffectType;

/**
 * Created by yifeng on 4/14/16.
 */
public class EquipWeaponEffect implements Effect {

  private final Hero hero;
  private final Weapon weapon;

  public EquipWeaponEffect(final Hero hero, final Weapon weapon) {
    this.hero = hero;
    this.weapon = weapon;
  }

  @Override
  public ConstEffectType getEffectType() {
    return ConstEffectType.EQUIP_WEAPON;
  }

  @Override
  public void act() {
    hero.equip(weapon);
  }
}
