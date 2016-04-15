package com.herthrone.card.action;

import com.herthrone.action.Action;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Weapon;

/**
 * Created by yifeng on 4/14/16.
 */
public class EquipWeapon implements Action {

  private final Hero hero;
  private final Weapon weapon;

  public EquipWeapon(final Hero hero, final Weapon weapon) {
    this.hero = hero;
    this.weapon = weapon;
  }

  @Override
  public void act() {
    this.hero.equipWeapon(weapon);
  }
}
