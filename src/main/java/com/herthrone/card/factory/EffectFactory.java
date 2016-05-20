package com.herthrone.card.factory;

import com.google.common.base.Preconditions;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.card.action.AttributeEffect;
import com.herthrone.card.action.EquipWeaponEffect;
import com.herthrone.card.action.MoveCardEffect;
import com.herthrone.card.action.StatusEffect;
import com.herthrone.card.action.SummonEffect;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.constant.Constant;
import com.herthrone.game.Battlefield;
import com.herthrone.stats.IntAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by yifeng on 4/14/16.
 */
public class EffectFactory {

  private Battlefield battlefield;
  private final MinionFactory minionFactory;
  private final WeaponFactory weaponFactory;

  public EffectFactory(final MinionFactory minionFactory, final WeaponFactory weaponFactory, final Battlefield battlefield) {
    this.minionFactory = minionFactory;
    this.weaponFactory = weaponFactory;
    this.battlefield = battlefield;
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
    ConstEffectType effect = config.getEffect();
    switch (effect) {
      case ATTRIBUTE:
        return getAttributeAction(config, minion);
      case WEAPON:
        Preconditions.checkArgument(minion instanceof Hero, "Only hero can equip weapon, not " + minion.getType());
        final Hero hero = (Hero) minion;
        return getEquipWeaponAction(hero, config);
      case SUMMON:
        return getSummonAction(config);
      case DRAW:
        return getDrawCardAction(config);
      default:
        throw new IllegalArgumentException("Unknown effect: " + effect);
    }
  }

  private Action getAttributeAction(final EffectConfig effect, final Minion minion) {
    final String type = effect.getType();
    switch (type) {
      case (Constant.HEALTH):
        return getHealthAttributeAction(minion, effect);
      case (Constant.ATTACK):
        return getGeneralAttributeAction(minion.getAttackAttr(), effect);
      case (Constant.CRYSTAL):
        return getGeneralAttributeAction(minion.getCrystalManaCost(), effect);
      case (Constant.HEALTH_UPPER_BOUND):
        return getGeneralAttributeAction(minion.getHealthUpperAttr(), effect);
      case (Constant.ARMOR):
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
    final ConstWeapon weapon = ConstWeapon.valueOf(weaponName.toUpperCase());
    Weapon weaponInstance = weaponFactory.createWeaponByName(weapon);
    return new EquipWeaponEffect(hero, weaponInstance);
  }

  private Action getSummonAction(final EffectConfig effect) {
    List<String> summonChoices = new ArrayList<>(effect.getChoices());
    summonChoices = summonChoices.stream().map(name -> name.toUpperCase()).collect(Collectors.toList());
    final int size = effect.getChoices().size();
    int index = 0;
    if (size > 0) {
      final Random random = new Random();
      if (effect.isUnique()) {
        List<String> uniqueMinionsOnBoard = battlefield.mySide.board.stream().map(minion -> minion.getCardName()).collect(Collectors.toList());
        summonChoices.removeAll(uniqueMinionsOnBoard);
      } else {
        index = random.nextInt(size);
      }
    }
    final String summonTargetName = summonChoices.get(index);
    final ConstMinion summonTarget = ConstMinion.valueOf(summonTargetName);
    final Minion minion = minionFactory.createMinionByName(summonTarget);
    return new SummonEffect(battlefield.mySide.board, minion);
  }

  private Action getDrawCardAction(final EffectConfig effect) {
    // TODO: draw from own deck/opponent deck/opponent hand
    return new MoveCardEffect(battlefield.mySide.hand, battlefield.mySide.deck);
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
        return battlefield.mySide.hero;
      default:
        return battlefield.mySide.board.get(index);
    }
  }
}
