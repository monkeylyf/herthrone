package com.herthrone.card.factory;

import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.WeaponConfig;
import com.herthrone.exception.WeaponNotFoundException;
import com.herthrone.stats.IntAttribute;
import com.herthrone.game.Battlefield;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.Constants;

import java.io.FileNotFoundException;

/**
 * Created by yifeng on 4/8/16.
 */
public class WeaponFactory {

  private Battlefield battlefield;

  public WeaponFactory(final Battlefield battlefield) {
    this.battlefield = battlefield;
  }

  public Weapon createWeaponByName(final String name) {
    try {
      WeaponConfig config = ConfigLoader.getWeaponConfigByName(name);
      return createWeapon(config.getCrystal(), config.getAttack(), config.getDurability(), config.getName(), config.getClassName(), config.isCollectible());
    } catch (FileNotFoundException|WeaponNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Weapon createWeapon(final int crystalManaCost, final int attack, final int durability, final String name, final String className, final boolean isCollectible) {

    return new Weapon() {
      private final IntAttribute crystalManaCostAttr = new IntAttribute(crystalManaCost);
      private final IntAttribute attackAttr = new IntAttribute(attack);
      private final IntAttribute durabilityAttr = new IntAttribute(durability);

      @Override
      public String getCardName() {
        return name;
      }

      @Override
      public String getType() {
        return Constants.WEAPON;
      }

      @Override
      public String getClassName() {
        return className;
      }

      @Override
      public IntAttribute getCrystalManaCost() {
        return this.crystalManaCostAttr;
      }

      @Override
      public boolean isCollectible() {
        return isCollectible;
      }

      @Override
      public int use() {
        this.durabilityAttr.decrease(1);
        return this.attackAttr.getVal();
      }

      @Override
      public IntAttribute getDurabilityAttr() {
        return this.durabilityAttr;
      }

      @Override
      public IntAttribute getAttackAttr() {
        return this.attackAttr;
      }
    };
  }
}
