package com.herthrone.card.minion.totem;

import com.herthrone.base.Attribute;
import com.herthrone.base.BaseCreature;
import com.herthrone.base.BaseMinion;
import com.herthrone.card.minion.Constants;

/**
 * Created by yifeng on 4/5/16.
 */
public class SearingTotem implements BaseMinion {

  private static final String NAME = Constants.SEARING_TOTEM;

  public static final int HEALTH = 1;
  public static final int ATTACK = 1;
  public static final int CRYSTAL_MANA_COST = 1;

  private final Attribute healthAttr;
  private final Attribute attackAttr;
  private final Attribute crystalManaCost;

  public SearingTotem() {
    this.healthAttr = new Attribute(SearingTotem.HEALTH);
    this.attackAttr = new Attribute(SearingTotem.ATTACK);
    this.crystalManaCost = new Attribute(SearingTotem.CRYSTAL_MANA_COST);
  }

  @Override
  public String getCardName() {
    return SearingTotem.NAME;
  }

  @Override
  public Attribute getCrystalManaCost() {
    return null;
  }

  @Override
  public Attribute getHealthAttr() {
    return null;
  }

  @Override
  public Attribute getAttackAttr() {
    return null;
  }

  @Override
  public void causeDamage(BaseCreature creature) {

  }

  @Override
  public void takeDamage(int damage) {

  }
}
