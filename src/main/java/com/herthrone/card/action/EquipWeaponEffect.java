package com.herthrone.card.action;

import com.herthrone.card.factory.Action;
import com.herthrone.base.Hero;
import com.herthrone.base.Weapon;

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
    this.hero.arm(weapon);
  }
}
