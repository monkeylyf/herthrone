package com.herthrone.card.factory;

import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.WeaponConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.game.Battlefield;
import com.herthrone.stats.IntAttribute;

import java.io.FileNotFoundException;

/**
 * Created by yifeng on 4/8/16.
 */
public class WeaponFactory {

  private Battlefield battlefield;

  public WeaponFactory(final Battlefield battlefield) {
    this.battlefield = battlefield;
  }

  public Weapon createWeaponByName(final ConstWeapon weapon) {
    try {
      WeaponConfig config = ConfigLoader.getWeaponConfigByName(weapon);
      return createWeapon(config.getCrystal(), config.getAttack(), config.getDurability(), config.getName(), config.getClassName(), config.isCollectible());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Weapon createWeapon(final int crystalManaCost, final int attack, final int durability, final ConstWeapon name, final ConstClass className, final boolean isCollectible) {

    return new Weapon() {
      private final IntAttribute crystalManaCostAttr = new IntAttribute(crystalManaCost);
      private final IntAttribute attackAttr = new IntAttribute(attack);
      private final IntAttribute durabilityAttr = new IntAttribute(durability);

      @Override
      public String getCardName() {
        return name.toString();
      }

      @Override
      public ConstType getType() {
        return ConstType.WEAPON;
      }

      @Override
      public ConstClass getClassName() {
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
