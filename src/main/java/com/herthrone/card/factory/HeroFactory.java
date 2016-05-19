package com.herthrone.card.factory;

import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.HeroConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstType;
import com.herthrone.stats.BooleanAttribute;
import com.herthrone.stats.IntAttribute;

import java.util.Optional;

/**
 * Created by yifeng on 4/8/16.
 */
public class HeroFactory {

  public static final int HEALTH = 30;
  public static final int ATTACK = 0;
  public static final int ARMOR = 0;
  public static final int CRYSTAL_MANA_COST = 0;

  public static Hero createHeroByName(final ConstHero hero) {
    HeroConfig heroConfig = ConfigLoader.getHeroConfigByName(hero);
    return HeroFactory.createHero(HeroFactory.HEALTH, HeroFactory.ATTACK, HeroFactory.ARMOR, HeroFactory.CRYSTAL_MANA_COST, heroConfig.getName(), heroConfig.getClassName());
  }

  public static Hero createHero(final int health, final int attack, final int armor, final int crystalManaCost, final ConstHero name, final ConstClass className) {

    return new Hero() {
      private final IntAttribute healthAttr = new IntAttribute(health);
      private final IntAttribute healthUpperAttr = new IntAttribute(health);
      private final IntAttribute armorAttr = new IntAttribute(armor);
      private final IntAttribute attackAttr = new IntAttribute(attack);
      private final IntAttribute crystalManaCostAttr = new IntAttribute(crystalManaCost);
      private final IntAttribute movePoints = new IntAttribute(1);
      private final BooleanAttribute damageImmunity = new BooleanAttribute(false);
      private final BooleanAttribute divineShield = new BooleanAttribute(false);
      private final BooleanAttribute frozen = new BooleanAttribute(false);
      private final BooleanAttribute stealth = new BooleanAttribute(false);
      private final BooleanAttribute taunt = new BooleanAttribute(false);

      private Optional<Weapon> weapon = Optional.empty();

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
        return this.crystalManaCostAttr;
      }

      @Override
      public boolean isCollectible() {
        return false;
      }

      @Override
      public IntAttribute getHealthAttr() {
        return this.healthAttr;
      }

      @Override
      public IntAttribute getHealthUpperAttr() {
        return this.healthUpperAttr;
      }

      @Override
      public IntAttribute getAttackAttr() {
        return this.attackAttr;
      }

      @Override
      public IntAttribute getMovePoints() {
        return this.movePoints;
      }

      @Override
      public BooleanAttribute getDamageImmunity() {
        return this.damageImmunity;
      }

      @Override
      public BooleanAttribute getFrozen() {
        return this.frozen;
      }

      @Override
      public BooleanAttribute getDivineShield() {
        return this.divineShield;
      }

      @Override
      public BooleanAttribute getTaunt() {
        return this.taunt;
      }

      @Override
      public BooleanAttribute getStealth() {
        return this.stealth;
      }

      @Override
      public IntAttribute getArmorAttr() {
        return this.armorAttr;
      }

      @Override
      public void causeDamage(Minion attackee) {
        attackee.takeDamage(this.weapon.get().use());
        if (this.weapon.get().getDurabilityAttr().getVal() == 0) {
          disarm();
        }
      }

      @Override
      public void takeDamage(int damage) {
        if (this.armorAttr.getVal() >= damage) {
          this.armorAttr.decrease(damage);
        } else {
          this.healthAttr.decrease(damage - this.armorAttr.getVal());
          this.armorAttr.reset();
        }
      }

      @Override
      public void arm(Weapon weapon) {
        if (this.weapon.isPresent()) {
          disarm();
        }
        this.weapon = Optional.of(weapon);
      }

      @Override
      public void disarm() {
        this.weapon = Optional.empty();
      }

      @Override
      public boolean canDamage() {
        return this.weapon.isPresent();
      }

      @Override
      public boolean isDead() {
        return this.healthAttr.getVal() <= 0;
      }

      @Override
      public int getHealthLoss() {
        return this.getHealthUpperAttr().getVal() - this.getHealthAttr().getVal();
      }
    };
  }
}
