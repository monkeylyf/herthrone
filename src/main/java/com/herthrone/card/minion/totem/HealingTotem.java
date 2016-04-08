package com.herthrone.card.minion.totem;

import com.herthrone.base.Attribute;
import com.herthrone.base.BaseCreature;
import com.herthrone.base.BaseMinion;
import com.herthrone.card.minion.Constants;

/**
 * Created by yifeng on 4/5/16.
 */
public class HealingTotem implements BaseMinion {

  private static final String NAME = Constants.HEALING_TOTEM;
  private static final int HEALTH = 2;
  private static final int ATTACK = 0;
  private static final int CRYSTAL_MANA_COST = 2;

  private final Attribute healthAttr;
  private final Attribute attackAttr;
  private final Attribute crystalManaCost;

  public HealingTotem() {
    this.healthAttr = new Attribute(HealingTotem.HEALTH);
    this.attackAttr = new Attribute(HealingTotem.ATTACK);
    this.crystalManaCost = new Attribute(HealingTotem.CRYSTAL_MANA_COST);
  }

  @Override
  public String getCardName() {
    return HealingTotem.NAME;
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
