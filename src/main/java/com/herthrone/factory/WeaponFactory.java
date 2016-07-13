package com.herthrone.factory;

import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.configuration.WeaponConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.constant.Constant;
import com.herthrone.game.Binder;
import com.herthrone.object.BooleanMechanics;
import com.herthrone.object.TriggeringMechanics;
import com.herthrone.object.ValueAttribute;

import java.util.List;
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
                              final Map<ConstTrigger, List<MechanicConfig>> mechanics) {
    return new Weapon() {

      private final ValueAttribute crystalManaCostAttr = new ValueAttribute(crystalManaCost);
      private final ValueAttribute attackAttr = new ValueAttribute(attack);
      private final ValueAttribute durabilityAttr = new ValueAttribute(durability);
      private final BooleanMechanics booleanMechanics = new BooleanMechanics(mechanics);
      private final ValueAttribute attackMovePoints = new ValueAttribute(
          booleanMechanics.has(ConstMechanic.WINDFURY) ?
              Constant.WINDFURY_INIT_MOVE_POINTS : Constant.INIT_MOVE_POINTS);
      private final TriggeringMechanics triggeringMechanics = new TriggeringMechanics(mechanics);
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
      public void destroy() {
        durabilityAttr.decrease(durabilityAttr.value());
        binder().getSide().hero.unequip();
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
      public ValueAttribute manaCost() {
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
      public void use() {
        durabilityAttr.decrease(1);
        if (!durabilityAttr.isPositive()) {
          binder().getSide().hero.unequip();
        }
      }

      @Override
      public ValueAttribute getDurabilityAttr() {
        return durabilityAttr;
      }

      @Override
      public ValueAttribute getAttackAttr() {
        return attackAttr;
      }

      @Override
      public ValueAttribute attackMovePoints() {
        return attackMovePoints;
      }

      public TriggeringMechanics getTriggeringMechanics() {
        return triggeringMechanics;
      }
    };
  }
}
