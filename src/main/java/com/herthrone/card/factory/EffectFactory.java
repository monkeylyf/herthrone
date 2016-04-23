package com.herthrone.card.factory;

import com.google.common.base.Preconditions;
import com.herthrone.card.action.*;
import com.herthrone.game.Constants;
import com.herthrone.base.*;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.exception.MinionNotFoundException;
import com.herthrone.game.Battlefield;
import com.herthrone.game.Container;
import com.herthrone.game.Side;
import com.herthrone.stats.Attribute;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yifeng on 4/14/16.
 */
public class EffectFactory {

  private final Side mySide;
  private final Side opponentSide;
  private final MinionFactory minionFactory;
  private final WeaponFactory weaponFactory;

  public EffectFactory(final MinionFactory minionFactory, final WeaponFactory weaponFactory, final Battlefield battlefield) {
    this.minionFactory = minionFactory;
    this.weaponFactory = weaponFactory;
    this.mySide = battlefield.getMySide();
    this.opponentSide = battlefield.getOpponentSide();
  }

  public List<Action> getActionsByConfig(final Spell spell, final Minion minion) {
    return spell.getEffects().stream()
            .map(effect -> getActionsByConfig(effect, minion))
            .collect(Collectors.toList());
  }

  public List<Action> getActionsByConfig(final SpellConfig config, final Minion minion) {
    return config.getEffects().stream()
            .map(effect -> getActionsByConfig(effect, minion))
            .collect(Collectors.toList());
  }

  public Action getActionsByConfig(final EffectConfig config, final Minion minion) {
    final String effect = config.getEffect();
    switch (effect) {
      case Constants.Type.ATTRIBUTE:
        return getAttributeAction(config, minion);
      case Constants.Type.WEAPON:
        Preconditions.checkArgument(minion instanceof Hero, "Only hero can equip weapon, not " + minion.getType());
        final Hero hero = (Hero) minion;
        return getEquipWeaponAction(hero, config);
      default:
        return getAttributeAction(config, minion);
    }
  }

  private Action getAttributeAction(final EffectConfig effect, final Minion minion) {
    final String type = effect.getType();
    switch (type) {
      case (Constants.Type.HEALTH):
        return getHealthAttributeAction(minion, effect);
      case (Constants.Type.ATTACK):
        return getGeneralAttributeAction(minion.getAttackAttr(), effect);
      case (Constants.Type.CRYSTAL):
        return getGeneralAttributeAction(minion.getCrystalManaCost(), effect);
      case (Constants.Type.HEALTH_UPPER_BOUND):
        return getGeneralAttributeAction(minion.getHealthUpperAttr(), effect);
      case (Constants.Type.ARMOR):
        Preconditions.checkArgument(minion instanceof Hero, "Armor Attribute applies to Hero only, not " + minion.getType());
        final Hero hero = (Hero) minion;
        return getGeneralAttributeAction(hero.getArmorAttr(), effect);
      default:
        throw new IllegalArgumentException("Unknown effect type " + effect.getType());
    }
  }

  private Action getGeneralAttributeAction(final Attribute attr, final EffectConfig effect) {
    Preconditions.checkArgument(effect.getValue() != 0, "Attribute change must be non-zero");
    return new AttributeEffect(attr, effect.getValue(), effect.getDuration());
  }

  private Action getHealthAttributeAction(final Minion minion, final EffectConfig effect) {
    final int value = effect.getValue();
    Preconditions.checkArgument(value != 0, "Health change must be non-zero");
    final int adjustChange = (value > 0) ? Math.min(value, minion.getHealthLoss()) : value;
    return new AttributeEffect(minion.getHealthAttr(), adjustChange, effect.getDuration());
  }

  private Action getEquipWeaponAction(final Hero hero, final EffectConfig effect) {
    final String weaponName = effect.getType();
    Weapon weapon = this.weaponFactory.createWeaponByName(weaponName);
    return new EquipWeaponEffect(hero, weapon);
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
