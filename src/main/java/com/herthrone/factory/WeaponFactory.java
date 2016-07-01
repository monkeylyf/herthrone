package com.herthrone.factory;

import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.configuration.WeaponConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.constant.Constant;
import com.herthrone.game.Binder;
import com.herthrone.object.BooleanMechanics;
import com.herthrone.object.EffectMechanics;
import com.herthrone.object.IntAttribute;

import java.util.Map;

/**
 * Created by yifeng on 4/8/16.
 */
public class WeaponFactory {

  public static Weapon create(final ConstWeapon weapon) {
    final WeaponConfig weaponConfig = ConfigLoader.getWeaponConfigByName(weapon);
    return create(weaponConfig.name, weaponConfig.displayName, weaponConfig.className,
        weaponConfig.attack, weaponConfig.durability,weaponConfig.crystal,
        weaponConfig.isCollectible, weaponConfig.mechanics);
  }

  public static Weapon create(final ConstWeapon name, final String displayName,
                              final ConstClass className, final int attack, final int durability,
                              final int crystalManaCost, final boolean isCollectible,
                              final Map<ConstMechanic, MechanicConfig> mechanics) {
    return new Weapon() {
      private final IntAttribute crystalManaCostAttr = new IntAttribute(crystalManaCost);
      private final IntAttribute attackAttr = new IntAttribute(attack);
      private final IntAttribute durabilityAttr = new IntAttribute(durability);
      private final BooleanMechanics booleanMechanics = new BooleanMechanics(mechanics);
      private final EffectMechanics effectMechanics = new EffectMechanics(mechanics);
      private final Binder binder = new Binder();

      @Override
      public Map<String, String> view() {
        return ImmutableMap.<String, String>builder()
            .put(Constant.CARD_NAME, cardName())
            .put(Constant.ATTACK, getAttackAttr().toString())
            .put(Constant.CRYSTAL, manaCost().toString())
            .put(Constant.TYPE, type().toString()).build();
      }

      @Override
      public String cardName() {
        return name.toString();
      }

      @Override
      public String displayName() {
        return displayName;
      }

      @Override
      public ConstType type() {
        return ConstType.WEAPON;
      }

      @Override
      public ConstClass className() {
        return className;
      }

      @Override
      public IntAttribute manaCost() {
        return crystalManaCostAttr;
      }

      @Override
      public boolean isCollectible() {
        return isCollectible;
      }

      @Override
      public Binder binder() {
        return binder;
      }

      @Override
      public int use() {
        durabilityAttr.decrease(1);
        return attackAttr.value();
      }

      @Override
      public IntAttribute getDurabilityAttr() {
        return durabilityAttr;
      }

      @Override
      public IntAttribute getAttackAttr() {
        return attackAttr;
      }

      @Override
      public EffectMechanics getEffectMechanics() {
        return effectMechanics;
      }

      @Override
      public BooleanMechanics getBooleanMechanics() {
        return booleanMechanics;
      }
    };
  }
}
