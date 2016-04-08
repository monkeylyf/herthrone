package com.herthrone.card.weapon;

import com.herthrone.base.Attribute;
import com.herthrone.base.Weapon;

/**
 * Created by yifeng on 4/5/16.
 */
public class WickedKnife implements Weapon {

  private static final String NAME = Constants.WICKED_KNIFE;

  public static final int ATTACK = 1;
  public static final int DURABILITY = 2;
  public static final int CRYSTAL_MANA_COST = 1;

  private final Attribute attackAttr;
  private final Attribute durabilityAttr;
  private final Attribute crystalManaCost;

  public WickedKnife() {
    this.attackAttr = new Attribute(WickedKnife.ATTACK);
    this.durabilityAttr = new Attribute(WickedKnife.DURABILITY);
    this.crystalManaCost = new Attribute(WickedKnife.CRYSTAL_MANA_COST);
  }

  @Override
  public String getCardName() {
    return WickedKnife.NAME;
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
