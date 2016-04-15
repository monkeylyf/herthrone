package com.herthrone.card.factory;

import com.herthrone.base.Hero;
import com.herthrone.card.action.PhysicalDamage;
import com.herthrone.base.Attribute;
import com.herthrone.base.Minion;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.HeroConfig;
import com.herthrone.exception.HeroNotFoundException;

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

  public static Hero createHeroByName(final String name) {
    try {
      HeroConfig heroConfig = ConfigLoader.getHeroConfigByName(name);
      return HeroFactory.createHero(HeroFactory.HEALTH, HeroFactory.ATTACK, HeroFactory.ARMOR, HeroFactory.CRYSTAL_MANA_COST, name, heroConfig.getClassName());
    } catch (FileNotFoundException|HeroNotFoundException e) {
      return HeroFactory.createHero(HeroFactory.HEALTH, HeroFactory.ATTACK, HeroFactory.ARMOR, HeroFactory.CRYSTAL_MANA_COST, name, "UNKNOWN");
    }
  }

  public static Hero createHero(final int health, final int attack, final int armor, final int crystalManaCost, final String name, final String className) {

    return new Hero() {
      private final Attribute healthAttr = new Attribute(health);
      private final Attribute armorAttr = new Attribute(armor);
      private final Attribute attackAttr = new Attribute(attack);
      private final Attribute crystalManaCostAttr = new Attribute(crystalManaCost);
      private final String heroName = name;
      private final String heroClass = className;

      private Optional<Weapon> weapon = Optional.empty();

      @Override
      public String getCardName() {
        return this.heroName;
      }

      @Override
      public Attribute getCrystalManaCost() {
        return this.crystalManaCostAttr;
      }

      @Override
      public PhysicalDamage yieldAttackAction(Minion creature) {
        return new PhysicalDamage(this, creature);
      }

      @Override
      public Attribute getHealthAttr() {
        return this.healthAttr;
      }

      @Override
      public Attribute getAttackAttr() {
        return this.attackAttr;
      }

      @Override
      public Attribute getArmorAttr() {
        return this.armorAttr;
      }

      @Override
      public String getHeroClass() {
        return this.heroClass;
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
    };
  }
}