package com.herthrone.card.heropower;

import com.herthrone.action.*;
import com.herthrone.base.Attribute;
import com.herthrone.base.BaseCreature;
import com.herthrone.base.Hero;
import com.herthrone.base.Spell;
import com.herthrone.card.Constants;

import java.util.List;

/**
 * Created by yifeng on 4/2/16.
 */
public class ArmorUp implements Spell, ActionFactory {

  private static final int CRYSTAL_MANA_COST = 2;
  private static final String NAME = Constants.ARMOR_UP;

  private final Attribute crystalManaCost;

  @Override
  public String getCardName() {
    return ArmorUp.NAME;
  }

  @Override
  public Attribute getCrystalManaCost() {
    return this.crystalManaCost;
  }

  public ArmorUp() {
    this.crystalManaCost = new Attribute(ArmorUp.CRYSTAL_MANA_COST);
  }

  @Override
  public void cast(BaseCreature hero) {
    //hero.armorAttr.increase(2);
  }

  @Override
  public Action yieldAction(List<BaseCreature> creature) {
    return new SpellAction(this, creature);
  }
}
