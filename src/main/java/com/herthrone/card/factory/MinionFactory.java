package com.herthrone.card.factory;

import com.herthrone.base.Minion;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstType;
import com.herthrone.game.Battlefield;
import com.herthrone.stats.BooleanAttribute;
import com.herthrone.stats.IntAttribute;

import java.io.FileNotFoundException;

/**
 * Created by yifeng on 4/13/16.
 */
public class MinionFactory {

  private final Battlefield battlefield;

  public MinionFactory(final Battlefield battlefield) {
    this.battlefield = battlefield;
  }

  public Minion createMinionByName(final ConstMinion minion) {
    try {
      MinionConfig config = ConfigLoader.getMinionConfigByName(minion);
      return createMinion(config.getHealth(), config.getAttack(), config.getCrystal(), config.getClassName(), config.getName(), config.isCollectible());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Minion createMinion(final int health, final int attack, final int crystalManaCost, final ConstClass className, final ConstMinion name, final boolean isCollectible) {
    return createMinion(health, attack, crystalManaCost, className, name, isCollectible, this.battlefield);
  }

  public Minion createMinion(final int health, final int attack, final int crystalManaCost, final ConstClass className, final ConstMinion name, final boolean isCollectible, final Battlefield field) {
    return new Minion() {

      private final IntAttribute healthAttr = new IntAttribute(health);
      private final IntAttribute healthUpperAttr = new IntAttribute(health);
      private final IntAttribute attackAttr = new IntAttribute(attack);
      private final IntAttribute crystalManaCostAttr = new IntAttribute(crystalManaCost);
      private final IntAttribute movePoints = new IntAttribute(1);
      private final BooleanAttribute damageImmunity = new BooleanAttribute(false);
      private final BooleanAttribute divineShield = new BooleanAttribute(false);
      private final BooleanAttribute frozen = new BooleanAttribute(false);
      private final BooleanAttribute stealth = new BooleanAttribute(false);
      private final Battlefield battlefield = field;

      @Override
      public String getCardName() {
        return name.toString();
      }

      @Override
      public ConstType getType() {
        return ConstType.MINION;
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
      public IntAttribute getMovePoints() {
        return null;
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
      public BooleanAttribute getStealth() {
        return this.stealth;
      }

      @Override
      public void causeDamage(Minion minion) {
        minion.takeDamage(this.attackAttr.getVal());
        if (this.stealth.isOn()) {
          // After attack, minions reveal themselves from stealth.
          // TODO: but this is not the only way to reveal a minion in stealth.
          // http://hearthstone.gamepedia.com/Stealth
          this.stealth.reset();
        }
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
