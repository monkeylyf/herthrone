package com.herthrone.factory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.herthrone.action.AttributeEffect;
import com.herthrone.action.EquipWeaponEffect;
import com.herthrone.action.MoveCardEffect;
import com.herthrone.action.SummonEffect;
import com.herthrone.base.Creature;
import com.herthrone.base.Effect;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.configuration.TargetConfig;
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

  private final MinionFactory minionFactory;
  private final WeaponFactory weaponFactory;
  private Battlefield battlefield;

  public EffectFactory(final MinionFactory minionFactory, final WeaponFactory weaponFactory,
                       final Battlefield battlefield) {
    this.minionFactory = minionFactory;
    this.weaponFactory = weaponFactory;
    this.battlefield = battlefield;
  }

  public Effect getEffectByMechanic(final MechanicConfig mechanic, Optional<Creature>
      target) {
    final Optional<EffectConfig> config = mechanic.getEffect();
    Preconditions.checkArgument(config.isPresent(), "Mechanic " + mechanic + " has no effect");
    return getActionsByConfig(config.get(), null);
  }

  public List<Effect> getActionsByConfig(final Spell spell, final Creature creature) {
    return spell.getEffects().stream()
        .map(effect -> getActionsByConfig(effect, creature))
        .collect(Collectors.toList());
  }

  public List<Effect> getActionsByConfig(final SpellConfig config, final Creature creature) {
    return config.getEffects().stream()
        .map(effect -> getActionsByConfig(effect, creature))
        .collect(Collectors.toList());
  }

  public Effect getActionsByConfig(final EffectConfig config, final Creature creature) {
    ConstEffectType effect = config.getEffect();
    switch (effect) {
      case ATTRIBUTE:
        return getAttributeAction(config, creature);
      case WEAPON:
        Preconditions.checkArgument(
            creature instanceof Hero, creature.getType() + " can not equip weapon");
        final Hero hero = (Hero) creature;
        return getEquipWeaponAction(hero, config);
      case SUMMON:
        return getSummonAction(config);
      case DRAW:
        return getDrawCardAction(config);
      default:
        throw new IllegalArgumentException("Unknown effect: " + effect);
    }
  }

  private Effect getAttributeAction(final EffectConfig effect, final Creature creature) {
    final String type = effect.getType();
    switch (type) {
      case (Constant.HEALTH):
        return getHealthAttributeAction(creature, effect);
      case (Constant.ATTACK):
        return getGeneralAttributeAction(creature.getAttackAttr(), effect);
      case (Constant.CRYSTAL):
        return getGeneralAttributeAction(creature.getCrystalManaCost(), effect);
      case (Constant.HEALTH_UPPER_BOUND):
        return getGeneralAttributeAction(creature.getHealthUpperAttr(), effect);
      case (Constant.ARMOR):
        Preconditions.checkArgument(
            creature instanceof Hero, "Armor Attribute does not applies to " + creature.getType());
        final Hero hero = (Hero) creature;
        return getGeneralAttributeAction(hero.getArmorAttr(), effect);
      default:
        throw new IllegalArgumentException("Unknown effect type: " + type);
    }
  }

  private Effect getGeneralAttributeAction(final IntAttribute attr, final EffectConfig effect) {
    Preconditions.checkArgument(effect.getValue() != 0, "Attribute change must be non-zero");
    return new AttributeEffect(attr, effect.getValue(), effect.isPermanent());
  }

  private Effect getHealthAttributeAction(final Creature creature, final EffectConfig effect) {
    final int value = effect.getValue();
    Preconditions.checkArgument(value != 0, "Health change must be non-zero");
    final int adjustChange = (value > 0) ? Math.min(value, creature.getHealthLoss()) : value;
    return new AttributeEffect(creature.getHealthAttr(), adjustChange, effect.isPermanent());
  }

  private Effect getEquipWeaponAction(final Hero hero, final EffectConfig effect) {
    final String weaponName = effect.getType();
    final ConstWeapon weapon = ConstWeapon.valueOf(weaponName.toUpperCase());
    Weapon weaponInstance = weaponFactory.createWeaponByName(weapon);
    return new EquipWeaponEffect(hero, weaponInstance);
  }

  private Effect getSummonAction(final EffectConfig effect) {
    List<String> summonChoices = new ArrayList<>(effect.getChoices());
    summonChoices = summonChoices.stream()
        .map(name -> name.toUpperCase()).collect(Collectors.toList());
    final int size = effect.getChoices().size();
    int index = 0;
    if (size > 0) {
      final Random random = new Random();
      if (effect.isUnique()) {
        List<String> uniqueMinionsOnBoard = battlefield.mySide.board.stream()
            .map(minion -> minion.getCardName()).collect(Collectors.toList());
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

  private Effect getDrawCardAction(final EffectConfig effect) {
    // TODO: draw from own deck/opponent deck/opponent hand
    final TargetConfig target = effect.getTarget();
    switch (target.type) {

    }
    return new MoveCardEffect(battlefield.mySide.hand, battlefield.mySide.deck, battlefield
        .mySide);
  }

  /**
   * Return Minion/Hero by given index.
   * -1 indicates hero; otherwise it's the index of board.
   *
   * @param index
   * @return
   */
  private Creature getMinionByIndex(final int index) {
    switch (index) {
      case -1:
        return battlefield.mySide.hero;
      default:
        return battlefield.mySide.board.get(index);
    }
  }
}
