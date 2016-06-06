package com.herthrone.action;

import com.herthrone.base.Effect;
import com.herthrone.base.Hero;
import com.herthrone.base.Weapon;

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
  public void act() {
    hero.arm(weapon);
  }
}
