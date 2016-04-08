package com.herthrone.card.heropower;

import com.herthrone.action.Action;
import com.herthrone.action.CastSpell;
import com.herthrone.action.GeneralActionFactory;
import com.herthrone.action.SpellAction;
import com.herthrone.base.*;
import com.herthrone.card.Constants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/2/16.
 */
public class Shapeshift implements Spell, GeneralActionFactory {

  private static final int CRYSTAL_MANA_COST = 2;
  private static final String NAME = Constants.SHAPE_SHIFT;

  private final Attribute crystalManaCost;

  public Shapeshift() {
    this.crystalManaCost = new Attribute(Shapeshift.CRYSTAL_MANA_COST);
  }

  @Override
  public String getCardName() {
    return Shapeshift.NAME;
  }

  @Override
  public Attribute getCrystalManaCost() {
    return this.crystalManaCost;
  }

  @Override
  public void cast(BaseCreature hero) {
    //hero.armorAttr.increase();
    //hero.attackAttr.increase();
    //hero.attackAttr.resetAfterRound();
  }

  @Override
  public List<Action> yieldAction(Player player) {
    return Arrays.asList(new SpellAction(this, player.toHeroList()));
  }

}
