package com.herthrone.factory;

import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Creature;
import com.herthrone.base.Minion;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;
import com.herthrone.game.Battlefield;
import com.herthrone.stats.BooleanAttribute;
import com.herthrone.stats.IntAttribute;

import java.util.Map;


/**
 * Created by yifeng on 4/13/16.
 */
public class MinionFactory {

  private static final int MINION_INIT_MOVE_POINTS = 1;
  private static final int WINDFURY_INIT_MOVE_POINTS = 2;
  private final Battlefield battlefield;

  public MinionFactory(final Battlefield battlefield) {
    this.battlefield = battlefield;
  }

  public Minion createMinionByName(final ConstMinion minion) {
    MinionConfig config = ConfigLoader.getMinionConfigByName(minion);
    return createMinion(config.getHealth(), config.getAttack(), config.getCrystal(), config.getClassName(), config.getName(), config.isCollectible());
  }

  public Minion createMinion(final int health, final int attack, final int crystalManaCost, final ConstClass className, final ConstMinion name, final boolean isCollectible) {
    return createMinion(health, attack, crystalManaCost, className, name, isCollectible, battlefield);
  }

  public Minion createMinion(final int health, final int attack, final int crystalManaCost, final ConstClass className, final ConstMinion name, final boolean isCollectible, final Battlefield field) {
    final Minion minion = new Minion() {

      @Override
      public void BattleCry() {

      }

      private final IntAttribute healthAttr = new IntAttribute(health);
      private final IntAttribute healthUpperAttr = new IntAttribute(health);
      private final IntAttribute attackAttr = new IntAttribute(attack);
      private final IntAttribute crystalManaCostAttr = new IntAttribute(crystalManaCost);
      private final IntAttribute movePoints = new IntAttribute(MINION_INIT_MOVE_POINTS);
      private final BooleanAttribute damageImmunity = new BooleanAttribute(false);
      private final BooleanAttribute divineShield = new BooleanAttribute(false);
      private final BooleanAttribute frozen = new BooleanAttribute(false);
      private final BooleanAttribute stealth = new BooleanAttribute(false);
      private final BooleanAttribute taunt = new BooleanAttribute(false);
      private final Battlefield battlefield = field;

      @Override
      public Map<String, String> view() {
        return ImmutableMap.<String, String>builder()
                .put(Constant.CARD_NAME, getCardName())
                .put(Constant.HEALTH, getHealthAttr().toString() + "/" + getHealthUpperAttr().toString())
                .put(Constant.ATTACK, getAttackAttr().toString())
                .put(Constant.CRYSTAL, getCrystalManaCost().toString())
                //.put(Constant.DESCRIPTION, "TODO")
                .put(Constant.TYPE, getClassName().toString())
                .put(Constant.MOVE_POINTS, getMovePoints().toString())
                .build();
      }

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
        return crystalManaCostAttr;
      }

      @Override
      public boolean isCollectible() {
        return isCollectible;
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
      public IntAttribute getMovePoints() {
        return this.movePoints;
      }

      @Override
      public BooleanAttribute getDamageImmunity() {
        return damageImmunity;
      }

      @Override
      public BooleanAttribute getFrozen() {
        return frozen;
      }

      @Override
      public BooleanAttribute getDivineShield() {
        return divineShield;
      }

      @Override
      public BooleanAttribute getTaunt() {
        return taunt;
      }

      @Override
      public BooleanAttribute getStealth() {
        return stealth;
      }

      @Override
      public void causeDamage(final Creature creature) {
        creature.takeDamage(attackAttr.getVal());
        if (stealth.isOn()) {
          // After attack, minion reveal themselves from stealth.
          // TODO: but this is not the only way to reveal a minion in stealth.
          // http://hearthstone.gamepedia.com/Stealth
          stealth.reset();
        }
      }

      @Override
      public void takeDamage(final int damage) {
        if (getDivineShield().isOn()) {
          getDivineShield().reset();
        } else {
          healthAttr.decrease(damage);
        }
      }

      @Override
      public boolean canDamage() {
        return attackAttr.getVal() > 0;
      }

      @Override
      public void nextRound() {
        this.movePoints.nextRound();
      }

      @Override
      public boolean isDead() {
        return healthAttr.getVal() <= 0;
      }

      @Override
      public int getHealthLoss() {
        return getHealthUpperAttr().getVal() - getHealthAttr().getVal();
      }
    };

    // Minion with no charge ability waits until next turn to move.
    final IntAttribute movePoints = minion.getMovePoints();
    movePoints.buff.temp.setTo(-MINION_INIT_MOVE_POINTS);

    return minion;
  }
}
