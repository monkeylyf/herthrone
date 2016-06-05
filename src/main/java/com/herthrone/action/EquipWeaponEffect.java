package com.herthrone.action;

import com.herthrone.base.Hero;
import com.herthrone.base.Weapon;
import com.herthrone.factory.Action;

/**
 * Created by yifeng on 4/14/16.
 */
public class EquipWeaponEffect implements Action {

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
