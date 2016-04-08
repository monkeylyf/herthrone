package com.herthrone.card.minion.totem;

import com.herthrone.base.Attribute;
import com.herthrone.base.BaseCreature;
import com.herthrone.base.BaseMinion;
import com.herthrone.card.minion.Constants;

/**
 * Created by yifeng on 4/5/16.
 */
  public class WrathOfAirTotem implements BaseMinion {

    private static final String NAME = Constants.WRATH_OF_AIR_TOTEM;

    public static final int HEALTH = 2;
    public static final int ATTACK = 0;
    public static final int CRYSTAL_MANA_COST = 1;

    private final Attribute healthAttr;
    private final Attribute attackAttr;
    private final Attribute crystalManaCost;

    public WrathOfAirTotem() {
      this.healthAttr = new Attribute(WrathOfAirTotem.HEALTH);
      this.attackAttr = new Attribute(WrathOfAirTotem.ATTACK);
      this.crystalManaCost = new Attribute(WrathOfAirTotem.CRYSTAL_MANA_COST);
    }

    @Override
    public String getCardName() {
      return WrathOfAirTotem.NAME;
    }

    @Override
    public Attribute getCrystalManaCost() {
      return this.crystalManaCost;
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
    public void causeDamage(BaseCreature creature) {

    }

    @Override
    public void takeDamage(int damage) {

    }
  }
