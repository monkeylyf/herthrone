package com.herthrone.card.factory;

import com.herthrone.base.Attribute;
import com.herthrone.base.Battlefield;
import com.herthrone.base.Weapon;

/**
 * Created by yifeng on 4/8/16.
 */
public class WeaponFactory {

  private Battlefield battlefield;

  public WeaponFactory(final Battlefield battlefield) {
    this.battlefield = battlefield;
  }

  public static Weapon createWeaponByName(final String name) {
    // TODO: need a json.
    return createWeapon(3, 2, 2, name);
  }

  public static Weapon createWeapon(final int crystalManaCost, final int attack, final int durability, final String name) {

    return new Weapon() {
      private final Attribute crystalManaCostAttr = new Attribute(crystalManaCost);
      private final Attribute attackAttr = new Attribute(attack);
      private final Attribute durabilityAttr = new Attribute(durability);
      private final String weaponName = name;

      @Override
      public String getCardName() {
        return this.weaponName;
      }

      @Override
      public Attribute getCrystalManaCost() {
        return this.crystalManaCostAttr;
      }

      @Override
      public int use() {
        this.durabilityAttr.decrease();
        return this.attackAttr.getVal();
      }

      @Override
      public Attribute getDurabilityAttr() {
        return this.durabilityAttr;
      }

      @Override
      public Attribute getAttackAttr() {
        return this.attackAttr;
      }
    };
  }
}
