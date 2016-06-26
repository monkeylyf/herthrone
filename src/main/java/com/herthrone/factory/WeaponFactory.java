package com.herthrone.factory;

import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.WeaponConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.constant.Constant;
import com.herthrone.game.Binder;
import com.herthrone.objects.IntAttribute;

import java.util.Map;

/**
 * Created by yifeng on 4/8/16.
 */
public class WeaponFactory {

  public static Weapon create(final ConstWeapon weapon) {
    WeaponConfig config = ConfigLoader.getWeaponConfigByName(weapon);
    return create(config.getCrystal(), config.getAttack(), config.getDurability(), config.getName(), config.getClassName(), config.isCollectible());
  }

  public static Weapon create(final int crystalManaCost, final int attack,
                              final int durability, final ConstWeapon name,
                              final ConstClass className, final boolean isCollectible) {

    return new Weapon() {
      private final IntAttribute crystalManaCostAttr = new IntAttribute(crystalManaCost);
      private final IntAttribute attackAttr = new IntAttribute(attack);
      private final IntAttribute durabilityAttr = new IntAttribute(durability);
      private final Binder binder = new Binder();

      @Override
      public Map<String, String> view() {
        return ImmutableMap.<String, String>builder()
            .put(Constant.CARD_NAME, getCardName())
            .put(Constant.ATTACK, getAttackAttr().toString())
            .put(Constant.CRYSTAL, getCrystalManaCost().toString())
            //.put(Constant.DESCRIPTION, "TODO")
            .put(Constant.TYPE, getType().toString())
            .build();
      }

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
        return crystalManaCostAttr;
      }

      @Override
      public boolean isCollectible() {
        return isCollectible;
      }

      @Override
      public Binder getBinder() {
        return binder;
      }

      @Override
      public int use() {
        durabilityAttr.decrease(1);
        return attackAttr.getVal();
      }

      @Override
      public IntAttribute getDurabilityAttr() {
        return durabilityAttr;
      }

      @Override
      public IntAttribute getAttackAttr() {
        return attackAttr;
      }
    };
  }
}
