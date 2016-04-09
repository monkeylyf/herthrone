package com.herthrone.card.heropower;


import com.herthrone.action.Action;
import com.herthrone.action.GeneralActionFactory;
import com.herthrone.action.SpellAction;
import com.herthrone.base.Attribute;
import com.herthrone.base.BaseCreature;
import com.herthrone.base.Player;
import com.herthrone.base.Spell;
import com.herthrone.card.Constants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/2/16.
 */
public class SteadyShot implements Spell, GeneralActionFactory {

  private static final int CRYSTAL_MANA_COST = 2;
  private static final String NAME = Constants.STEADY_SHOT;

  private final Attribute crystalManaCost;

  public SteadyShot() {
    this.crystalManaCost = new Attribute(SteadyShot.CRYSTAL_MANA_COST);
  }

  @Override
  public String getCardName() {
    return SteadyShot.NAME;
  }

  @Override
  public Attribute getCrystalManaCost() {
    return this.crystalManaCost;
  }

  @Override
  public void cast(final BaseCreature hero) {
    hero.takeDamage(2);
  }

  @Override
  public List<Action> yieldAction(Player player) {
    return Arrays.asList(new SpellAction(this, player.toHeroList()));
  }
}
