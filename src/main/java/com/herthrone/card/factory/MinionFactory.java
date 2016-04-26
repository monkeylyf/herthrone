package com.herthrone.card.factory;

import com.herthrone.stats.Attribute;
import com.herthrone.game.Battlefield;
import com.herthrone.base.Minion;
import com.herthrone.stats.Status;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.Constants;
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

  public Minion createMinionByName(final String name) {
    try {
      MinionConfig config = ConfigLoader.getMinionConfigByName(name);
      return createMinion(config.getHealth(), config.getAttack(), config.getCrystal(), config.getClassName(), config.getName());
    } catch (FileNotFoundException|MinionNotFoundException e) {
      e.printStackTrace();
      return null;
    }
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
      private final Status damageImmunity = new Status(false);
      private final Status divineShield = new Status(false);
      private final Status frozen = new Status(false);
      private final Battlefield battlefield = field;

      @Override
      public String getCardName() {
        return name;
      }

      @Override
      public String getType() {
        return Constants.MINION;
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
      public void causeDamage(Minion minion) {
        minion.takeDamage(this.attackAttr.getVal());
      }

      @Override
      public void takeDamage(final int damage) {
        if (this.getDivineShield().isOn()) {
          this.getDivineShield().reset();
        } else {
          this.healthAttr.decrease(damage);
        }
      }

      @Override
      public boolean canDamage() {
        return this.attackAttr.getVal() > 0;
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
