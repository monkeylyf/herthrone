package com.herthrone.card.factory;

import com.google.common.base.Preconditions;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.card.action.*;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.game.Battlefield;
import com.herthrone.game.shit;
import com.herthrone.game.Side;
import com.herthrone.stats.IntAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
      case shit.Type.ATTRIBUTE:
        return getAttributeAction(config, minion);
      case shit.Type.WEAPON:
        Preconditions.checkArgument(minion instanceof Hero, "Only hero can equip weapon, not " + minion.getType());
        final Hero hero = (Hero) minion;
        return getEquipWeaponAction(hero, config);
      case shit.Type.SUMMON:
        return getSummonAction(config);
      case shit.Type.DRAW:
        return getDrawCardAction(config);
      default:
        throw new IllegalArgumentException("Unknown effect: " + effect);
    }
  }

  private Action getAttributeAction(final EffectConfig effect, final Minion minion) {
    final String type = effect.getType();
    switch (type) {
      case (shit.Type.HEALTH):
        return getHealthAttributeAction(minion, effect);
      case (shit.Type.ATTACK):
        return getGeneralAttributeAction(minion.getAttackAttr(), effect);
      case (shit.Type.CRYSTAL):
        return getGeneralAttributeAction(minion.getCrystalManaCost(), effect);
      case (shit.Type.HEALTH_UPPER_BOUND):
        return getGeneralAttributeAction(minion.getHealthUpperAttr(), effect);
      case (shit.Type.ARMOR):
        Preconditions.checkArgument(minion instanceof Hero, "Armor Attribute applies to Hero only, not " + minion.getType());
        final Hero hero = (Hero) minion;
        return getGeneralAttributeAction(hero.getArmorAttr(), effect);
      default:
        throw new IllegalArgumentException("Unknown effect type: " + type);
    }
  }

  private Action getGeneralAttributeAction(final IntAttribute attr, final EffectConfig effect) {
    Preconditions.checkArgument(effect.getValue() != 0, "Attribute change must be non-zero");
    return new AttributeEffect(attr, effect.getValue(), effect.isPermanent());
  }

  private Action getHealthAttributeAction(final Minion minion, final EffectConfig effect) {
    final int value = effect.getValue();
    Preconditions.checkArgument(value != 0, "Health change must be non-zero");
    final int adjustChange = (value > 0) ? Math.min(value, minion.getHealthLoss()) : value;
    return new AttributeEffect(minion.getHealthAttr(), adjustChange, effect.isPermanent());
  }

  private Action getEquipWeaponAction(final Hero hero, final EffectConfig effect) {
    final String weaponName = effect.getType();
    Weapon weapon = this.weaponFactory.createWeaponByName(weaponName);
    return new EquipWeaponEffect(hero, weapon);
  }

  private Action getSummonAction(final EffectConfig effect) {
    List<String> summonTargets = new ArrayList<>(effect.getTarget());
    final int size = effect.getTarget().size();
    int index = 0;
    if (size > 0) {
      final Random random = new Random();
      if (effect.isUnique()) {
        List<String> uniqueMinionsOnBoard = this.mySide.getBoard().stream().map(minion -> minion.getCardName()).collect(Collectors.toList());
        summonTargets.removeAll(uniqueMinionsOnBoard);
      } else {
        index = random.nextInt(size);
      }
    }
    final String summonTargetName = summonTargets.get(index);
    final Minion minion = this.minionFactory.createMinionByName(summonTargetName);
    return new SummonEffect(this.mySide.getBoard(), minion);
  }

  private Action getDrawCardAction(final EffectConfig effect) {
    // TODO: draw from own deck/opponent deck/opponent hand
    return new MoveCardEffect(this.mySide.getHand(), this.mySide.getDeck());
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

  /**
   * Return Minion/Hero by given index.
   * -1 indicates hero; otherwise it's the index of board.
   *
   * @param index
   * @return
   */
  private Minion getMinionByIndex(final int index) {
    switch (index) {
      case -1:
        return this.mySide.getHero();
      default:
        return this.mySide.getBoard().get(index);
    }
  }
}
