package com.herthrone.card.factory;

import com.herthrone.base.Battlefield;
import com.herthrone.card.action.PhysicalDamage;
import com.herthrone.base.Attribute;
import com.herthrone.base.Minion;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.exception.MinionNotFoundException;

import java.io.FileNotFoundException;

/**
 * Created by yifeng on 4/13/16.
 */
public class MinionFactory {

  private final Battlefield battlefield;

  public MinionFactory(final Battlefield battlefield) {
    this.battlefield = battlefield;
  }

  public Minion createMinionByName(final String name) throws FileNotFoundException, MinionNotFoundException {
    MinionConfig config = ConfigLoader.getMinionConfigByName(name);
    return createMinion(config.getHealth(), config.getAttack(), config.getCrystal(), config.getClassName(), config.getName());
  }

  public Minion createMinion(final int health, final int attack, final int crystalManaCost, final String className, final String name) {
    return createMinion(health, attack, crystalManaCost, className, name, this.battlefield);
  }

  public Minion createMinion(final int health, final int attack, final int crystalManaCost, final String className, final String name, final Battlefield field) {
    return new Minion() {

      private final Attribute healthAttr = new Attribute(health);
      private final Attribute healthUpperAttr = new Attribute(health);
      private final Attribute attackAttr = new Attribute(attack);
      private final Attribute crystalManaCostAttr = new Attribute(crystalManaCost);
      private final String minionName = name;
      private final String heroClass = className;
      private final Battlefield battlefield = field;

      @Override
      public String getCardName() {
        return this.minionName;
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
      public Attribute getHealthUpperAttr() {
        return this.healthUpperAttr;
      }

      @Override
      public Attribute getAttackAttr() {
        return this.attackAttr;
      }

      @Override
      public String getHeroClass() {
        return this.heroClass;
      }

      @Override
      public void causeDamage(Minion creature) {
        creature.takeDamage(this.attackAttr.getVal());
      }

      @Override
      public void takeDamage(final int damage) {
        this.healthAttr.decrease(damage);
      }

      @Override
      public boolean canDamage() {
        return this.attackAttr.getVal() > 0;
      }
    };
  }
}
