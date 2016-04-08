package com.herthrone.card.heropower;

import com.herthrone.action.Action;
import com.herthrone.action.GeneralActionFactory;
import com.herthrone.base.Attribute;
import com.herthrone.base.Summon;
import com.herthrone.action.SummonAction;
import com.herthrone.base.Player;
import com.herthrone.card.Constants;
import com.herthrone.container.Board;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/2/16.
 */
public class TotemicCall implements Summon, GeneralActionFactory {

  private static final int CRYSTAL_MANA_COST = 2;
  private static final String NAME = Constants.TOTEMIC_CALL;

  private Attribute crystalManaCost;

  public TotemicCall(int rawCrystalManaCost) {
    this.crystalManaCost = new Attribute(TotemicCall.CRYSTAL_MANA_COST);
  }

  @Override
  public String getCardName() {
    return TotemicCall.NAME;
  }

  @Override
  public Attribute getCrystalManaCost() {
    return this.crystalManaCost;
  }

  @Override
  public List<Action> yieldAction(Player player) {
    return Arrays.asList(new SummonAction(this, player));
  }

  @Override
  public void summon(Board board) {
    //board.addMinion();
  }

  public boolean canSummon(final Board board) {
    return !board.isFull();
  }

}
