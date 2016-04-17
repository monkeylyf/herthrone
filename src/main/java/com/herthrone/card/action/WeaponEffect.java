package com.herthrone.card.action;

import com.herthrone.action.Action;
import com.herthrone.base.Hero;
import com.herthrone.card.factory.WeaponFactory;

/**
 * Created by yifeng on 4/14/16.
 */
public class WeaponEffect implements Action {

  private final Hero hero;
  private final String weaponName;

  public WeaponEffect(final Hero hero, final String weaponName) {
    this.hero = hero;
    this.weaponName = weaponName;
  }

  @Override
  public void act() {
    this.hero.equipWeapon(WeaponFactory.createWeaponByName(this.weaponName));
  }

}
