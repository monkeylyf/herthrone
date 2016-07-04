package com.herthrone.factory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.herthrone.base.Creature;
import com.herthrone.base.Effect;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConditionConfig;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.constant.Constant;
import com.herthrone.effect.AttributeEffect;
import com.herthrone.effect.EquipWeaponEffect;
import com.herthrone.effect.GenerateEffect;
import com.herthrone.effect.MoveCardEffect;
import com.herthrone.effect.OverloadEffect;
import com.herthrone.effect.PhysicalDamageEffect;
import com.herthrone.effect.ReturnToHandEffect;
import com.herthrone.effect.SummonEffect;
import com.herthrone.effect.TakeControlEffect;
import com.herthrone.game.Side;
import com.herthrone.helper.RandomMinionGenerator;
import com.herthrone.object.ValueAttribute;
import com.herthrone.object.ManaCrystal;
import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yifeng on 4/14/16.
 */
public class EffectFactory {

  static Logger logger = Logger.getLogger(EffectFactory.class.getName());
  static final Comparator<Minion> compareBySequenceId = (m1, m2) -> Integer.compare(
      m1.getSequenceId(), m2.getSequenceId());

  public static void addAuraEffect(final EffectConfig effectConfig, final Minion minion,
                                   final Minion target) {
    switch (effectConfig.type) {
      case Constant.ATTACK:
        target.attack().addAuraBuff(minion, effectConfig.value);
        break;
      case Constant.HEALTH:
        target.health().addAuraBuff(minion, effectConfig.value);
        break;
      case Constant.MAX_HEALTH:
        target.maxHealth().addAuraBuff(minion, effectConfig.value);
        break;
      default:
        throw new RuntimeException(effectConfig.type + " not supported for aura");
    }
  }

  public static void removeAuraEffect(final EffectConfig effectConfig, final Minion minion,
                                      final Minion target) {
    switch (effectConfig.type) {
      case Constant.ATTACK:
        target.attack().removeAuraBuff(minion);
        break;
      case Constant.HEALTH:
        target.health().removeAuraBuff(minion);
        break;
      case Constant.MAX_HEALTH:
        target.maxHealth().removeAuraBuff(minion);
        break;
      default:
        throw new RuntimeException(effectConfig.type + " not supported for aura");
    }
  }

  public static boolean isTriggerConditionMet(final Optional<MechanicConfig> mechanicConfigOptional,
                                              final Side side, final Creature target) {
    if (!mechanicConfigOptional.isPresent()) {
      logger.debug("Mechanic configuration is absent");
      return false;
    } else if (!isConditionTriggered(mechanicConfigOptional.get().effect, target)) {
      logger.debug("Condition not met and mechanic effect not triggered");
      return false;
    } else {
      return true;
    }
  }

  public static void pipeMechanicEffectIfPresentAndMeetCondition(
      final Optional<MechanicConfig> mechanicConfigOptional, final Side side, final Creature target) {
    if (isTriggerConditionMet(mechanicConfigOptional, side, target)) {
      final MechanicConfig mechanicConfig = mechanicConfigOptional.get();
      logger.debug("Triggering " + mechanicConfig.mechanic.toString());
      Effect effect = pipeMechanicEffect(mechanicConfig, target);
      target.binder().getSide().getEffectQueue().enqueue(effect);
    }
  }

  private static boolean isConditionTriggered(final Optional<EffectConfig> effectConfigOptional,
                                              final Creature target) {
    if (effectConfigOptional.isPresent() &&
        effectConfigOptional.get().conditionConfigOptional.isPresent()) {
      // Check if there is condition config. If there is, return whether condition is met.
      final ConditionConfig conditionConfig = effectConfigOptional.get()
          .conditionConfigOptional.get();
      switch (conditionConfig.conditionType) {
        case BOARD_SIZE:
          return conditionConfig.inRange(target.binder().getOpponentSide().board.size());
        case COMBO:
          return target.binder().getSide().replay.size() > 1;
        default:
          throw new RuntimeException("Unknown condition: " + conditionConfig.conditionType);
      }
    } else {
      // If no condition configured, return true and the effect should be triggered any way.
      return true;
    }
  }

  public static Effect pipeMechanicEffect(final MechanicConfig mechanic, final Creature target) {
    final Optional<EffectConfig> config = mechanic.effect;
    Preconditions.checkArgument(config.isPresent(), "Mechanic " + mechanic + " has no effect");
    final EffectConfig effectConfig = config.get();
    final Creature realTarget = effectConfig.isRandom ?
        RandomMinionGenerator.randomCreature(effectConfig.target, target.binder().getSide()) :
        target;

    return getActionsByConfig(effectConfig,  realTarget);
  }

  public static Effect getActionsByConfig(final EffectConfig config, final Creature creature) {
    ConstEffectType effect = config.name;
    switch (effect) {
      case ATTRIBUTE:
        return getAttributeAction(config, creature);
      case WEAPON:
        Preconditions.checkArgument(
            creature instanceof Hero, creature.type() + " can not equip weapon");
        return getEquipWeaponAction((Hero) creature, config);
      case SUMMON:
        return getSummonAction(config, creature.binder().getSide());
      case DRAW:
        return getDrawCardAction(config, creature.binder().getSide());
      case CRYSTAL:
        return getCrystalEffect(config, creature);
      case TAKE_CONTROL:
        return getTakeControlAction(config, creature);
      case GENERATE:
        return getGenerateAction(config, creature);
      case RETURN_TO_HAND:
        Preconditions.checkArgument(
            creature instanceof Minion, creature.type() + " can not be returned to player's hand");
        return getReturnToHandAction((Minion) creature);
      default:
        throw new IllegalArgumentException("Unknown effect: " + effect);
    }
  }

  private static Effect getReturnToHandAction(final Minion target) {
    return new ReturnToHandEffect(target);
  }

  private static Effect getGenerateAction(EffectConfig config, Creature creature) {
    return new GenerateEffect(
        config.choices, config.type, config.target, creature.binder().getSide());
  }

  private static Effect getTakeControlAction(final EffectConfig effect, final Creature creature) {
    final Creature traitorMinion = RandomMinionGenerator.randomCreature(
        effect.target, creature.binder().getSide());
    Preconditions.checkArgument(traitorMinion instanceof Minion);
    return new TakeControlEffect((Minion) traitorMinion);
  }

  private static Effect getAttributeAction(final EffectConfig effect, final Creature creature) {
    final String type = effect.type;
    switch (type) {
      case (Constant.HEALTH):
        return getHealthAttributeAction(creature, effect);
      case (Constant.ATTACK):
        return getGeneralAttributeAction(creature.attack(), effect);
      case (Constant.CRYSTAL):
        return getGeneralAttributeAction(creature.manaCost(), effect);
      case (Constant.MAX_HEALTH):
        return getGeneralAttributeAction(creature.maxHealth(), effect);
      case (Constant.ARMOR):
        Preconditions.checkArgument(
            creature instanceof Hero, "Armor Attribute does not applies to " + creature.type());
        return getGeneralAttributeAction(((Hero) creature).armor(), effect);
      default:
        throw new IllegalArgumentException("Unknown effect type: " + type);
    }
  }

  private static Effect getEquipWeaponAction(final Hero hero, final EffectConfig effect) {
    final String weaponName = effect.type;
    final ConstWeapon weapon = ConstWeapon.valueOf(weaponName.toUpperCase());
    Weapon weaponInstance = WeaponFactory.create(weapon);
    return new EquipWeaponEffect(hero, weaponInstance);
  }

  private static Effect getSummonAction(final EffectConfig effect, final Side side) {
    List<String> summonChoices = effect.choices.stream()
        .map(name -> name.toUpperCase()).collect(Collectors.toList());
    String summonTargetName;
    if (effect.isUnique) {
      // Summon candidates must be non-existing on the board to avoid dups.
      final List<Creature> existingCreatures = side.board.stream()
          .map(m -> (Creature) m).collect(Collectors.toList());
      summonTargetName = RandomMinionGenerator.randomUnique(summonChoices, existingCreatures);
    } else {
      summonTargetName = RandomMinionGenerator.randomOne(summonChoices);
    }
    final ConstMinion summonTarget = ConstMinion.valueOf(summonTargetName);
    final Minion minion = MinionFactory.create(summonTarget);
    return new SummonEffect(side.board, minion);
  }

  private static Effect getDrawCardAction(final EffectConfig effect, final Side side) {
    // TODO: draw from own deck/opponent deck/opponent hand
    final TargetConfig target = effect.target;
    switch (target.type) {

    }
    return new MoveCardEffect(side.hand, side.deck, side);
  }

  private static Effect getCrystalEffect(final EffectConfig config, final Creature creature) {
    final String type = config.type;
    final ManaCrystal manaCrystal = creature.binder().getSide().manaCrystal;
    switch (type) {
      case (Constant.CRYSTAL_LOCK):
        return new OverloadEffect(manaCrystal, config.value);
      default:
        throw new IllegalArgumentException("Unknown type: " + type);
    }
  }

  private static Effect getHealthAttributeAction(final Creature creature, final EffectConfig effect) {
    final int value = effect.value;
    Preconditions.checkArgument(value != 0, "Health change must be non-zero");
    final int adjustChange = (value > 0) ? Math.min(value, creature.healthLoss()) : value;
    return new AttributeEffect(creature.health(), adjustChange, effect.isPermanent);
  }

  private static Effect getGeneralAttributeAction(final ValueAttribute attr, final EffectConfig effect) {
    Preconditions.checkArgument(effect.value != 0, "Attribute change must be non-zero");
    return new AttributeEffect(attr, effect.value, effect.isPermanent);
  }

  public static List<Effect> getActionsByConfig(final Spell spell, final Creature creature) {
    return spell.getEffects().stream()
        .map(effect -> getActionsByConfig(effect, creature)).collect(Collectors.toList());
  }

  public static List<Effect> getActionsByConfig(final SpellConfig config, final Creature creature) {
    return config.effects.stream()
        .map(effect -> getActionsByConfig(effect, creature)).collect(Collectors.toList());
  }


  public static class AttackFactory {

    public static void getPhysicalDamageAction(final Creature attacker, final Creature attackee) {
      final Effect effect = attacker.booleanMechanics().has(ConstMechanic.FORGETFUL) ?
          getForgetfulPhysicalDamageAction(attacker, attackee) : new PhysicalDamageEffect(attacker, attackee);
      attacker.binder().getSide().getEffectQueue().enqueue(effect);
    }

    private static Effect getForgetfulPhysicalDamageAction(final Creature attacker, final Creature attackee) {
      final boolean isForgetfulToPickNewTarget = RandomMinionGenerator.getBool();
      if (isForgetfulToPickNewTarget) {
        logger.debug("Forgetful triggered");
        final Creature substituteAttackee = RandomMinionGenerator.randomExcept(attackee.binder().getSide().allCreatures(), attackee);
        logger.debug(String.format("Change attackee from %s to %s", attackee.toString(), substituteAttackee.toString()));
        Preconditions.checkArgument(substituteAttackee != attackee);
        return new PhysicalDamageEffect(attacker, substituteAttackee);
      } else {
        logger.debug("Forgetful not triggered");
        return new PhysicalDamageEffect(attacker, attackee);
      }
    }
  }
}
