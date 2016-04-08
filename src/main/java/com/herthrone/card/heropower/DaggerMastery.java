package com.herthrone.card.heropower;

import com.herthrone.action.Action;
import com.herthrone.action.ActionFactory;
import com.herthrone.action.CastSpell;
import com.herthrone.action.SpellAction;
import com.herthrone.base.Attribute;
import com.herthrone.base.BaseCreature;
import com.herthrone.base.Hero;
import com.herthrone.base.Spell;
import com.herthrone.card.Constants;
import com.herthrone.card.weapon.WickedKnife;

import java.util.List;

/**
 * Created by yifeng on 4/2/16.
 */
public class DaggerMastery implements Spell, ActionFactory {

  private static final int CRYSTAL_MANA_COST = 2;
  private static final String NAME = Constants.DAGGER_MASTERY;

  private final Attribute crystalManaCost;

  @Override
  public String getCardName() {
    return DaggerMastery.NAME;
  }

  @Override
  public Attribute getCrystalManaCost() {
    return this.crystalManaCost;
  }

  public DaggerMastery() {
    this.crystalManaCost = new Attribute(DaggerMastery.CRYSTAL_MANA_COST);
  }

  @Override
  public Action yieldAction(List<BaseCreature> heroes) {
    return new SpellAction(this, heroes);
  }

  @Override
  public void cast(BaseCreature hero) {
    //hero.equip(new WickedKnife(2));
  }

}
