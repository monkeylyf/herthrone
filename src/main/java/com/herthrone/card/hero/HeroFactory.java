package com.herthrone.card.hero;

import com.herthrone.action.AttackAction;
import com.herthrone.base.Attribute;
import com.herthrone.base.BaseCreature;
import com.herthrone.base.Weapon;

import java.util.Optional;

/**
 * Created by yifeng on 4/8/16.
 */
public class HeroFactory {

  public static final int HEALTH = 30;
  public static final int ATTACK = 0;
  public static final int ARMOR = 0;
  public static final int CRYSTAL_MANA_COST = 0;

  public static BaseCreature createHeroByName(final String name) {
    return HeroFactory.createHero(HeroFactory.HEALTH, HeroFactory.ATTACK, HeroFactory.ARMOR, HeroFactory.CRYSTAL_MANA_COST, name);
  }

  public static BaseCreature createHero(final int health, final int attack, final int armor, final int crystalManaCost, final String name) {

    return new BaseCreature() {
      private final Attribute healthAttr = new Attribute(health);
      private final Attribute armorAttr = new Attribute(armor);
      private final Attribute attackAttr = new Attribute(attack);
      private final Attribute crystalManaCostAttr = new Attribute(crystalManaCost);
      private final String heroName = name;

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
      public AttackAction yieldAttackAction(BaseCreature creature) {
        return new AttackAction(this, creature);
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
      public void causeDamage(BaseCreature attackee) {
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
