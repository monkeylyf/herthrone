package com.herthrone.card.weapon;

import com.herthrone.base.Attribute;
import com.herthrone.base.Weapon;

/**
 * Created by yifeng on 4/5/16.
 */
public class FieryWarAxe implements Weapon {

  private static final String NAME = Constants.FIERY_WAR_AEX;

  public static final int ATTACK = 0;
  public static final int DURABILITY = 2;
  public static final int CRYSTAL_MANA_COST = 1;

  private final Attribute attackAttr;
  private final Attribute durabilityAttr;
  private final Attribute crystalManaCost;

  public FieryWarAxe() {
    this.attackAttr = new Attribute(FieryWarAxe.ATTACK);
    this.durabilityAttr = new Attribute(FieryWarAxe.DURABILITY);
    this.crystalManaCost = new Attribute(FieryWarAxe.CRYSTAL_MANA_COST);
  }

  @Override
  public String getCardName() {
    return FieryWarAxe.NAME;
  }

  @Override
  public Attribute getCrystalManaCost() {
    return this.crystalManaCost;
  }

  @Override
  public int use() {
    this.durabilityAttr.decrease();
    return this.attackAttr.getVal();
  }

  @Override
  public Attribute getDurability() {
    return this.durabilityAttr;
  }

  @Override
  public Attribute getAttack() {
    return this.attackAttr;
  }

}
