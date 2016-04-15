package com.herthrone.card.factory;

import com.herthrone.action.Action;
import com.herthrone.action.ActionFactory;
import com.herthrone.base.Minion;
import com.herthrone.base.Side;
import com.herthrone.card.action.AttributeEffect;
import com.herthrone.card.action.Summon;
import com.herthrone.container.Board;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class HeroPowerFactory {

  private final Side side;

  public HeroPowerFactory(Side side) {
    this.side = side;
  }

  public ActionFactory getArmorActionGenerator(final int gain) {
    return getArmorActionGenerator(this.side, gain);
  }

  private ActionFactory getArmorActionGenerator(final Side side, final int gain) {
    return new ActionFactory() {
      @Override
      public List<Action> yieldActions() {
        Action action = new AttributeEffect(side.getHero().getArmorAttr(), gain);
        return singleActionToList(action);
      }
    };
  }

  public ActionFactory getHealthActionGenerator(final int index, final int gain) {
    Minion minion = getMinionByIndex(index);
    return getHealthActionGenerator(minion, gain);
  }

  private ActionFactory getHealthActionGenerator(final Minion minion, final int gain) {
    return new ActionFactory() {
      @Override
      public List<Action> yieldActions() {
        Action action = new AttributeEffect(minion.getHealthAttr(), gain);
        return singleActionToList(action);
      }
    };
  }

  public ActionFactory getAttackActionGenerator(final int index, final int gain) {
    Minion minion = getMinionByIndex(index);
    return getAttackActionGenerator(minion, gain);
  }

  private ActionFactory getAttackActionGenerator(final Minion minion, final int gain) {
    return new ActionFactory() {
      @Override
      public List<Action> yieldActions() {
        Action action = new AttributeEffect(minion.getAttackAttr(), gain);
        return singleActionToList(action);
      }
    };
  }
  public ActionFactory getSummonActionGenerator(final List<String> minionNames) {
    return  getSummonActionGenerator(this.side.getBoard(), minionNames);
  }

  private ActionFactory getSummonActionGenerator(final Board board, final List<String> minionNames) {
    return new ActionFactory() {
      @Override
      public List<Action> yieldActions() {
        Action action = new Summon(board, minionNames);
        return singleActionToList(action);
      }
    };
  }

  private static List<Action> singleActionToList(Action action) {
    return Arrays.asList(action);
  }

  /**
   * Return Minion/Hero by given index.
   * -1 indicates hero; otherwise it's the index of board.
   *
   * @param index
   * @return
   */
  private Minion getMinionByIndex(final int index) {
    switch (index) {
      case -1: return this.side.getHero();
      default: return this.side.getBoard().getMinion(index);
    }
  }
}
