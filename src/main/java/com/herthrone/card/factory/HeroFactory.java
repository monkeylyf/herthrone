package com.herthrone.card.factory;

import com.herthrone.base.*;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.Constants;
import com.herthrone.configuration.HeroConfig;
import com.herthrone.exception.HeroNotFoundException;
import com.herthrone.stats.Attribute;
import com.herthrone.stats.Status;

import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * Created by yifeng on 4/8/16.
 */
public class HeroFactory {

  public static final int HEALTH = 30;
  public static final int ATTACK = 0;
  public static final int ARMOR = 0;
  public static final int CRYSTAL_MANA_COST = 0;

  public static Hero createHeroByName(final String name) throws FileNotFoundException, HeroNotFoundException {
      HeroConfig heroConfig = ConfigLoader.getHeroConfigByName(name);
      return HeroFactory.createHero(HeroFactory.HEALTH, HeroFactory.ATTACK, HeroFactory.ARMOR, HeroFactory.CRYSTAL_MANA_COST, name, heroConfig.getClassName());
  }

  public static Hero createHero(final int health, final int attack, final int armor, final int crystalManaCost, final String name, final String className) {

    return new Hero() {
      private final Attribute healthAttr = new Attribute(health);
      private final Attribute healthUpperAttr = new Attribute(health);
      private final Attribute armorAttr = new Attribute(armor);
      private final Attribute attackAttr = new Attribute(attack);
      private final Attribute crystalManaCostAttr = new Attribute(crystalManaCost);
      private final Status damageImmunity = new Status(false);
      private final Status divineShield = new Status(false);
      private final Status frozen = new Status(false);

      private Optional<Weapon> weapon = Optional.empty();

      @Override
      public String getCardName() {
        return name;
      }

      @Override
      public String getType() {
        return Constants.HERO;
      }

      @Override
      public String getClassName() {
        return className;
      }

      @Override
      public Attribute getCrystalManaCost() {
        return this.crystalManaCostAttr;
      }

      @Override
      public Attribute getHealthAttr() {
        return this.healthAttr;
      }

      @Override
      public Attribute getHealthUpperAttr() {
        return this.healthUpperAttr;
      }

      @Override
      public Attribute getAttackAttr() {
        return this.attackAttr;
      }

      @Override
      public Status getDamageImmunity() {
        return this.damageImmunity;
      }

      @Override
      public Status getFrozen() {
        return this.frozen;
      }

      @Override
      public Status getDivineShield() {
        return this.divineShield;
      }

      @Override
      public Attribute getArmorAttr() {
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
      public void equipWeapon(Weapon weapon) {
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
