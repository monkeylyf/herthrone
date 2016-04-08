package com.herthrone.card.heropower;

import com.herthrone.action.Action;
import com.herthrone.action.ActionFactory;
import com.herthrone.action.CastSpell;
import com.herthrone.action.SpellAction;
import com.herthrone.base.Attribute;
import com.herthrone.base.BaseCreature;
import com.herthrone.base.Spell;
import com.herthrone.card.Constants;

import java.util.List;

/**
 * Created by yifeng on 4/2/16.
 */
public class FireBlast implements Spell, ActionFactory {

  private static final int CRYSTAL_MANA_COST = 2;
  private static final String NAME = Constants.FIRE_BLAST;

  private final Attribute crystalManaCost;

  @Override
  public String getCardName() {
    return FireBlast.NAME;
  }

  @Override
  public Attribute getCrystalManaCost() {
    return this.crystalManaCost;
  }

  public FireBlast() {
    this.crystalManaCost = new Attribute(FireBlast.CRYSTAL_MANA_COST);
  }

  @Override
  public Action yieldAction(List<BaseCreature> baseCreature) {
    return new SpellAction(this, baseCreature);
  }

  @Override
  public void cast(BaseCreature baseCreature) {
    baseCreature.takeDamage(1);
  }

}
