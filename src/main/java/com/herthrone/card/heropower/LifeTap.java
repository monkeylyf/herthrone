package com.herthrone.card.heropower;

import com.herthrone.action.*;
import com.herthrone.base.*;
import com.herthrone.card.Constants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/2/16.
 */

public class LifeTap implements Spell, GeneralActionFactory {

  private static final int CRYSTAL_MANA_COST = 2;
  private static final String NAME = Constants.LIFE_TAP;

  private final Attribute crystalManaCost;

  public LifeTap(int rawCrystalManaCost) {
    this.crystalManaCost = new Attribute(LifeTap.CRYSTAL_MANA_COST);
  }

  @Override
  public String getCardName() {
    return LifeTap.NAME;
  }

  @Override
  public Attribute getCrystalManaCost() {
    return this.crystalManaCost;
  }

  @Override
  public void cast(BaseCreature hero) {
    hero.takeDamage(2);
    //Hero Draw card...
  }

  @Override
  public List<Action> yieldAction(Player player) {
    return Arrays.asList(new SpellAction(this, player.toHeroList()));
  }
}
