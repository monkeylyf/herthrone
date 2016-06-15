package com.herthrone.factory;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Creature;
import com.herthrone.base.Hero;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.HeroConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;
import com.herthrone.stats.BooleanMechanics;
import com.herthrone.stats.IntAttribute;

import java.util.Map;

/**
 * Created by yifeng on 4/8/16.
 */
public class HeroFactory {

  public static final int HEALTH = 30;
  public static final int ATTACK = 0;
  public static final int ARMOR = 0;
  public static final int CRYSTAL_MANA_COST = 0;
  private static final int HERO_INIT_MOVE_POINTS = 1;

  public static Hero createHeroByName(final ConstHero hero) {
    HeroConfig heroConfig = ConfigLoader.getHeroConfigByName(hero);
    return HeroFactory.createHero(HeroFactory.HEALTH, HeroFactory.ATTACK, HeroFactory.ARMOR,
        HeroFactory.CRYSTAL_MANA_COST, heroConfig.getName(),
        heroConfig.getClassName());
  }

  public static Hero createHero(final int health, final int attack, final int armor,
                                final int crystalManaCost, final ConstHero name,
                                final ConstClass className) {
    return new Hero() {
      private final IntAttribute healthAttr = new IntAttribute(health);
      private final IntAttribute healthUpperAttr = new IntAttribute(health);
      private final IntAttribute armorAttr = new IntAttribute(armor);
      private final IntAttribute attackAttr = new IntAttribute(attack);
      private final IntAttribute crystalManaCostAttr = new IntAttribute(crystalManaCost);
      private final IntAttribute attackMovePoints = new IntAttribute(HERO_INIT_MOVE_POINTS);
      private final IntAttribute heroPowerMovePoints = new IntAttribute(HERO_INIT_MOVE_POINTS);
      private final BooleanMechanics booleanMechanics = new BooleanMechanics();
      private Optional<Weapon> weapon = Optional.absent();

      @Override
      public Map<String, String> view() {
        return ImmutableMap.<String, String>builder()
            .put(Constant.CARD_NAME, getCardName())
            .put(Constant.HEALTH, getHealthAttr().toString() + "/" + getHealthUpperAttr().toString())
            .put(Constant.ARMOR, getArmorAttr().toString())
            .put(Constant.WEAPON, (getWeapon().isPresent()) ? getWeapon().toString() : "unarmed")
            .put(Constant.ATTACK, getAttackAttr().toString())
            .put(Constant.CRYSTAL, getCrystalManaCost().toString())
            //.put(Constant.DESCRIPTION, "TODO")
            .put(Constant.MOVE_POINTS, getAttackMovePoints().toString())
            .build();
      }

      @Override
      public void endTurn() {
        this.attackMovePoints.endTurn();
      }

      @Override
      public void startTurn() {

      }

      @Override
      public String getCardName() {
        return name.toString();
      }

      @Override
      public ConstType getType() {
        return ConstType.HERO;
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
        return false;
      }

      @Override
      public IntAttribute getHealthAttr() {
        return healthAttr;
      }

      @Override
      public IntAttribute getHealthUpperAttr() {
        return healthUpperAttr;
      }

      @Override
      public IntAttribute getAttackAttr() {
        return attackAttr;
      }

      @Override
      public IntAttribute getAttackMovePoints() {
        return attackMovePoints;
      }

      @Override
      public BooleanMechanics getBooleanMechanics() {
        return booleanMechanics;
      }

      @Override
      public IntAttribute getArmorAttr() {
        return armorAttr;
      }

      @Override
      public IntAttribute getHeroPowerMovePoints() {
        return heroPowerMovePoints;
      }

      @Override
      public Optional<Weapon> getWeapon() {
        return weapon;
      }

      @Override
      public void causeDamage(final Creature attackee) {
        attackee.takeDamage(weapon.get().use());
        if (weapon.get().getDurabilityAttr().getVal() == 0) {
          disarm();
        }
      }

      @Override
      public void takeDamage(int damage) {
        if (armorAttr.getVal() >= damage) {
          armorAttr.decrease(damage);
        } else {
          healthAttr.decrease(damage - armorAttr.getVal());
          armorAttr.reset();
        }
      }

      @Override
      public void arm(Weapon newWeapon) {
        if (weapon.isPresent()) {
          disarm();
        }
        weapon = Optional.of(newWeapon);
      }

      @Override
      public void disarm() {
        weapon = Optional.absent();
      }

      @Override
      public boolean canDamage() {
        return weapon.isPresent();
      }

      @Override
      public boolean isDead() {
        return healthAttr.getVal() <= 0;
      }

      @Override
      public int getHealthLoss() {
        return getHealthUpperAttr().getVal() - getHealthAttr().getVal();
      }

      @Override
      public String toString() {
        return Objects.toStringHelper(this)
            .add("hero", getCardName())
            .add("health", getHealthAttr().getVal())
            .add("health_upper", getHealthUpperAttr().getVal())
            .add("attack", (weapon.isPresent()) ? weapon.get().getAttackAttr().getVal() : 0)
            .toString();
      }
    };
  }
}
