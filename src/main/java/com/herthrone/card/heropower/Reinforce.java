package com.herthrone.card.heropower;

import com.herthrone.action.Action;
import com.herthrone.action.GeneralActionFactory;
import com.herthrone.action.SummonAction;
import com.herthrone.base.Attribute;
import com.herthrone.base.BaseCreature;
import com.herthrone.base.Player;
import com.herthrone.base.Summon;
import com.herthrone.card.Constants;
import com.herthrone.container.Board;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/2/16.
 */
public class Reinforce implements Summon, GeneralActionFactory {

  private static final int CRYSTAL_MANA_COST = 2;
  private static final String NAME = Constants.REINFORCE;

  private final Attribute crystalManaCost;

  public Reinforce() {
    this.crystalManaCost = new Attribute(Reinforce.CRYSTAL_MANA_COST);
  }

  @Override
  public String getCardName() {
    return Reinforce.NAME;
  }

  @Override
  public Attribute getCrystalManaCost() {
    return this.crystalManaCost;
  }

  @Override
  public void summon(Board board) {
    BaseCreature silverHandRecruit = null;
    board.addMinion(silverHandRecruit);
  }

  public boolean canSummon(Board board) {
    return !board.isFull();
  }

  @Override
  public List<Action> yieldAction(Player player) {
    return Arrays.asList(new SummonAction(this, player));
  }

}
