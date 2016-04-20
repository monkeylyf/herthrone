package com.herthrone.card.factory;

import com.herthrone.base.BaseCard;
import com.herthrone.base.Battlefield;
import com.herthrone.base.Minion;
import com.herthrone.base.Side;
import com.herthrone.card.action.AttributeEffect;
import com.herthrone.card.action.MoveCardEffect;
import com.herthrone.card.action.StatusEffect;
import com.herthrone.card.action.SummonEffect;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.base.Container;
import com.herthrone.exception.MinionNotFoundException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class EffectFactory {

  private final Side mySide;
  private final Side opponentSide;
  private final MinionFactory minionFactory;

  public EffectFactory(final MinionFactory minionFactory, final Battlefield battlefield) {
    this.minionFactory = minionFactory;
    this.mySide = battlefield.getMySide();
    this.opponentSide = battlefield.getOpponentSide();
  }

  public ActionFactory getActionFactoryByConfig(EffectConfig config) {
    final String effect = config.getEffect();
    //switch (effect) {
    //  case Constants.ATTRIBUTE:
    //}
    // TODO:
    return getArmorActionGenerator(2);
  }

  public ActionFactory getArmorActionGenerator(final int gain) {
    return getArmorActionGenerator(this.mySide, gain);
  }

  private ActionFactory getArmorActionGenerator(final Side side, final int gain) {
    return new ActionFactory() {
      @Override
      public List<Action> yieldActions() {
        Action action = new AttributeEffect(side.getHero().getArmorAttr(), gain);
        return Factory.singleActionToList(action);
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
        return Factory.singleActionToList(action);
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
        return Factory.singleActionToList(action);
      }
    };
  }
  public ActionFactory getSummonActionGenerator(final List<String> minionNames) throws FileNotFoundException, MinionNotFoundException {
    final List<Minion> minions = new ArrayList<>();
    for (String minionName : minionNames) {
      minions.add(this.minionFactory.createMinionByName(minionName));
    }
    return  getSummonActionGenerator(this.mySide.getMinions(), minions);
  }

  private ActionFactory getSummonActionGenerator(final Container<Minion> board, final List<Minion> minions) {
    return new ActionFactory() {
      @Override
      public List<Action> yieldActions() {
        Action action = new SummonEffect(board, minions);
        return Factory.singleActionToList(action);
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
        return Factory.singleActionToList(action);
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
        return Factory.singleActionToList(action);
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
        return Factory.singleActionToList(action);
      }
    };
  }

  public ActionFactory getDrawCardFromDeckActionGenerator(final int num) {
    Container<BaseCard> hand = this.mySide.getHand();
    Container<BaseCard> deck = this.mySide.getDeck();
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

  /**
   * Return Minion/Hero by given index.
   * -1 indicates hero; otherwise it's the index of board.
   *
   * @param index
   * @return
   */
  private Minion getMinionByIndex(final int index) {
    switch (index) {
      case -1: return this.mySide.getHero();
      default: return this.mySide.getMinions().get(index);
    }
  }
}
