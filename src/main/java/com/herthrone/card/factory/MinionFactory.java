package com.herthrone.card.factory;

import com.herthrone.stats.IntAttribute;
import com.herthrone.game.Battlefield;
import com.herthrone.base.Minion;
import com.herthrone.stats.BooleanAttribute;
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
      return createMinion(config.getHealth(), config.getAttack(), config.getCrystal(), config.getClassName(), config.getName(), config.isCollectible());
    } catch (FileNotFoundException|MinionNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Minion createMinion(final int health, final int attack, final int crystalManaCost, final String className, final String name, final boolean isCollectible) {
    return createMinion(health, attack, crystalManaCost, className, name, isCollectible, this.battlefield);
  }

  public Minion createMinion(final int health, final int attack, final int crystalManaCost, final String className, final String name, final boolean isCollectible, final Battlefield field) {
    return new Minion() {

      private final IntAttribute healthAttr = new IntAttribute(health);
      private final IntAttribute healthUpperAttr = new IntAttribute(health);
      private final IntAttribute attackAttr = new IntAttribute(attack);
      private final IntAttribute crystalManaCostAttr = new IntAttribute(crystalManaCost);
      private final BooleanAttribute damageImmunity = new BooleanAttribute(false);
      private final BooleanAttribute divineShield = new BooleanAttribute(false);
      private final BooleanAttribute frozen = new BooleanAttribute(false);
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
      public IntAttribute getCrystalManaCost() {
        return this.crystalManaCostAttr;
      }

      @Override
      public boolean isCollectible() {
        return isCollectible;
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
