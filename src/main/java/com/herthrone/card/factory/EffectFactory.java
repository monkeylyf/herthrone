package com.herthrone.card.factory;

import com.herthrone.action.Action;
import com.herthrone.action.ActionFactory;
import com.herthrone.base.BaseCard;
import com.herthrone.base.Minion;
import com.herthrone.base.Side;
import com.herthrone.card.action.AttributeEffect;
import com.herthrone.card.action.MoveCardEffect;
import com.herthrone.card.action.StatusEffect;
import com.herthrone.card.action.Summon;
import com.herthrone.container.Board;
import com.herthrone.container.Container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class EffectFactory {

  private final Side side;

  public EffectFactory(Side side) {
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

  public ActionFactory getDivineShieldStatusActionGenerator(final int index) {
    Minion minion = getMinionByIndex(index);
    return getDivineShieldStatusActionGenerator(minion);
  }

  private ActionFactory getDivineShieldStatusActionGenerator(final Minion minion) {
    return new ActionFactory() {
      @Override
      public List<Action> yieldActions() {
        Action action = new StatusEffect(minion.getDivineShield(), 1);
        return singleActionToList(action);
      }
    };
  }

  public ActionFactory getFrozenStatusActionGenerator(final int index) {
    Minion minion = getMinionByIndex(index);
    return getFrozenStatusActionGenerator(minion);
  }

  private ActionFactory getFrozenStatusActionGenerator(final Minion minion) {
    return new ActionFactory() {
      @Override
      public List<Action> yieldActions() {
        Action action = new StatusEffect(minion.getFrozen(), 1);
        return singleActionToList(action);
      }
    };
  }

  public ActionFactory getDamageImmunityStatusActionGenerator(final int index) {
    Minion minion = getMinionByIndex(index);
    return getDamageImmunityStatusActionGenerator(minion);
  }

  private ActionFactory getDamageImmunityStatusActionGenerator(Minion minion) {
    return new ActionFactory() {
      @Override
      public List<Action> yieldActions() {
        Action action = new StatusEffect(minion.getDamageImmunity(), 1);
        return singleActionToList(action);
      }
    };
  }

  public ActionFactory getDrawCardFromDeckActionGenerator(final int num) {
    Container<BaseCard> hand = this.side.getHand();
    Container<BaseCard> deck = this.side.getDeck();
    return getDrawCardFromDeckActionGenerator(hand, deck, num);
  }

  private ActionFactory getDrawCardFromDeckActionGenerator(final Container<BaseCard> hand, final Container<BaseCard> deck, final int num) {
    return new ActionFactory() {
      @Override
      public List<Action> yieldActions() {
        List<Action> actions = new ArrayList<>();
        for (int i = 0; i < num; i++) {
          Action action = new MoveCardEffect(hand, deck);
          actions.add(action);
        }
        return actions;
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
