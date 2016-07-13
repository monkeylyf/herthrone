package com.herthrone.factory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.herthrone.base.Creature;
import com.herthrone.base.Destroyable;
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
import com.herthrone.constant.ConstDependency;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.constant.Constant;
import com.herthrone.effect.AttributeEffect;
import com.herthrone.effect.BuffEffect;
import com.herthrone.effect.DestroyEffect;
import com.herthrone.effect.EquipWeaponEffect;
import com.herthrone.effect.GenerateEffect;
import com.herthrone.effect.MaxHealthBuffEffect;
import com.herthrone.effect.MoveCardEffect;
import com.herthrone.effect.OverloadEffect;
import com.herthrone.effect.PhysicalDamageEffect;
import com.herthrone.effect.ReturnToHandEffect;
import com.herthrone.effect.SummonEffect;
import com.herthrone.effect.TakeControlEffect;
import com.herthrone.game.Side;
import com.herthrone.helper.RandomMinionGenerator;
import com.herthrone.object.ManaCrystal;
import com.herthrone.object.ValueAttribute;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yifeng on 4/14/16.
 */
public class EffectFactory {

  private static Logger logger = Logger.getLogger(EffectFactory.class.getName());
  static final Comparator<Minion> compareBySequenceId = (m1, m2) -> Integer.compare(
      m1.getSequenceId(), m2.getSequenceId());

  static void addAuraEffect(final EffectConfig effectConfig, final Minion minion,
                                   final Minion target) {
    switch (effectConfig.type) {
      case Constant.ATTACK:
        target.attack().addAuraBuff(minion, effectConfig.value);
        break;
      case Constant.MAX_HEALTH:
        target.maxHealth().addAuraBuff(minion, effectConfig.value);
        target.health().addAuraBuff(minion, effectConfig.value);
        break;
      default:
        throw new RuntimeException(effectConfig.type + " not supported for aura");
    }
  }

  static void removeAuraEffect(final EffectConfig effectConfig, final Minion minion,
                               final Minion target) {
    switch (effectConfig.type) {
      case Constant.ATTACK:
        target.attack().removeAuraBuff(minion);
        break;
      case Constant.MAX_HEALTH:
        // http://us.battle.net/hearthstone/en/forum/topic/13423772774
        final int healthBeforeAuraRemoval = target.health().value();
        final boolean isDamage = target.healthLoss() > 0;
        target.health().removeAuraBuff(minion);
        target.maxHealth().removeAuraBuff(minion);
        if (isDamage) {
          target.health().increase(healthBeforeAuraRemoval - target.health().value());
        }
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
      final Optional<MechanicConfig> mechanicConfigOptional,
      final Side side, final Creature target) {
    if (isTriggerConditionMet(mechanicConfigOptional, side, target)) {
      final MechanicConfig mechanicConfig = mechanicConfigOptional.get();
      logger.debug("Triggering " + mechanicConfig.mechanic.toString());
      final List<Effect> effects = pipeMechanicEffect(mechanicConfig, target);
      target.binder().getSide().getEffectQueue().enqueue(effects);
    }
  }

  public static void triggerEndTurnMechanics(final Side side) {
    final List<Minion> minions = side.board.stream()
        .sorted(compareBySequenceId)
        .filter(minion -> minion.getTriggeringMechanics().has(ConstTrigger.ON_END_TURN))
        .collect(Collectors.toList());

    for (final Minion minion : minions) {
      for (MechanicConfig mechanic : minion.getTriggeringMechanics().get(ConstTrigger.ON_END_TURN)) {
        final List<Creature> targets = TargetFactory.getProperTargets(mechanic.effect.get().target, side);
        final List<Effect> effects = targets.stream()
            .flatMap(target -> pipeMechanicEffect(mechanic, target).stream())
            .collect(Collectors.toList());
        side.getEffectQueue().enqueue(effects);
      }
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
          return conditionConfig.inRange(target.binder().getSide().board.size());
        case COMBO:
          return target.binder().getSide().replay.size() > 1;
        case WEAPON_EQUIPED:
          final List<Destroyable> destroyables = TargetFactory.getDestroyables(
              effectConfigOptional.get().target, target.binder().getSide());
          if (destroyables.size() == 0) {
            return false;
          } else {
            Preconditions.checkArgument(destroyables.size() == 1, "More than one destroyable object");
            Preconditions.checkArgument(destroyables.get(0) instanceof Weapon, "Only support weapon");
            return true;
          }
        default:
          throw new RuntimeException("Unknown condition: " + conditionConfig.conditionType);
      }
    } else {
      // If no condition configured, return true and the effect should be triggered any way.
      return true;
    }
  }

  public static List<Effect> pipeMechanicEffect(final MechanicConfig mechanic,
                                                final Creature target) {
    final Optional<EffectConfig> config = mechanic.effect;
    Preconditions.checkArgument(config.isPresent(), "Mechanic %s has no effect", mechanic);
    final EffectConfig effectConfig = config.get();
    final Creature realTarget = effectConfig.isRandom ?
        RandomMinionGenerator.randomCreature(effectConfig.target, target.binder().getSide()) :
        target;

    return pipeEffectsByConfig(effectConfig,  realTarget);
  }

  public static List<Effect> pipeEffectsByConfig(final EffectConfig config,
                                                 final Creature creature) {
    ConstEffectType effect = config.name;
    switch (effect) {
      case ATTRIBUTE:
        return getAttributeEffect(config, creature);
      case BUFF:
        return getBuffEffect(config, creature);
      case CRYSTAL:
        return getCrystalEffect(config, creature);
      case DRAW:
        return getDrawCardEffect(config, creature.binder().getSide());
      case GENERATE:
        return getGenerateEffect(config, creature);
      case DESTROY:
        return getDestroyEffect(config, creature);
      case RETURN_TO_HAND:
        Preconditions.checkArgument(
            creature instanceof Minion, "%s can not be returned to player's hand", creature.type());
        return getReturnToHandEffect((Minion) creature);
      case SUMMON:
        return getSummonEffect(config, creature.binder().getSide());
      case TAKE_CONTROL:
        return getTakeControlEffect(config, creature);
      case WEAPON:
        Preconditions.checkArgument(
            creature instanceof Hero, "%s can not equip weapon", creature.type());
        return getEquipWeaponEffect((Hero) creature, config);
      default:
        throw new IllegalArgumentException("Unknown effect: " + effect);
    }
  }

  private static List<Effect> getBuffEffect(final EffectConfig effect, final Creature creature) {
    final Side side = creature.binder().getSide();
    final String type = effect.type;
    switch (type) {
      case (Constant.ATTACK):
        return Collections.singletonList(getGeneralBuffEffect(side, creature.attack(), effect));
      case (Constant.CRYSTAL):
        return Collections.singletonList(getGeneralBuffEffect(side, creature.manaCost(), effect));
      case (Constant.MAX_HEALTH):
        Preconditions.checkArgument(
            creature instanceof Minion, "max health buff does not support %s", creature.type());
        return Collections.singletonList(getMaxHealthBuffEffect((Minion) creature, effect));
      default:
        throw new IllegalArgumentException("Unknown effect type for buff: " + type);
    }
  }

  private static Effect getMaxHealthBuffEffect(final Minion minion, final EffectConfig effect) {
    final int gain = getGain(minion.binder().getSide(), effect);
    return new MaxHealthBuffEffect(minion, gain);
  }

  private static int getGain(final Side side, final EffectConfig effectConfig) {
    if (effectConfig.valueDependency.isPresent()) {
      return getValueByDependency(effectConfig.valueDependency.get(), side);
    } else {
      Preconditions.checkArgument(effectConfig.value != 0, "Gain value must be non-zero");
      return effectConfig.value;
    }
  }

  private static Effect getGeneralBuffEffect(final Side side, final ValueAttribute attribute,
                                             final EffectConfig effect) {
    final int gain = getGain(side, effect);
    return new BuffEffect(attribute, gain, effect.isPermanent);
  }

  private static List<Effect> getDestroyEffect(final EffectConfig config, final Creature creature) {
    return TargetFactory.getDestroyables(config.target, creature.binder().getSide()).stream()
        .map(DestroyEffect::new)
        .collect(Collectors.toList());
  }

  private static List<Effect> getReturnToHandEffect(final Minion target) {
    return Collections.singletonList(new ReturnToHandEffect(target));
  }

  private static List<Effect> getGenerateEffect(EffectConfig config, Creature creature) {
    final Effect generateEffect = new GenerateEffect(
        config.choices, config.type, config.target, creature.binder().getSide());
    return Collections.singletonList(generateEffect);
  }

  private static List<Effect> getTakeControlEffect(final EffectConfig effect,
                                                   final Creature creature) {
    final Creature traitorMinion = RandomMinionGenerator.randomCreature(
        effect.target, creature.binder().getSide());
    Preconditions.checkArgument(traitorMinion instanceof Minion);
    return Collections.singletonList(new TakeControlEffect((Minion) traitorMinion));
  }

  private static List<Effect> getAttributeEffect(final EffectConfig effect,
                                                 final Creature creature) {
    final Side side = creature.binder().getSide();
    final String type = effect.type;
    switch (type) {
      case (Constant.HEALTH):
        return Collections.singletonList(getHealthAttributeEffect(creature, effect));
      case (Constant.ATTACK):
        return Collections.singletonList(
            getGeneralAttributeEffect(side, creature.attack(), effect));
      case (Constant.CRYSTAL):
        return Collections.singletonList(
            getGeneralAttributeEffect(side, creature.manaCost(), effect));
      case (Constant.MAX_HEALTH):
        return Arrays.asList(
            getGeneralAttributeEffect(side, creature.maxHealth(), effect),
            getHealthAttributeEffect(creature, effect));
      case (Constant.ARMOR):
        Preconditions.checkArgument(
            creature instanceof Hero, "Armor Attribute does not applies to %s", creature.type());
        return Collections.singletonList(
            getGeneralAttributeEffect(side, ((Hero) creature).armor(), effect));
      default:
        throw new IllegalArgumentException("Unknown effect type: " + type);
    }
  }

  private static List<Effect> getEquipWeaponEffect(final Hero hero, final EffectConfig effect) {
    final String weaponName = effect.type;
    final ConstWeapon weapon = ConstWeapon.valueOf(weaponName.toUpperCase());
    final Weapon weaponInstance = WeaponFactory.create(weapon);
    return Collections.singletonList(new EquipWeaponEffect(hero, weaponInstance));
  }

  private static List<Effect> getSummonEffect(final EffectConfig effect, final Side side) {
    final List<String> summonChoices = effect.choices.stream()
        .map(String::toUpperCase)
        .collect(Collectors.toList());
    // Summon candidates must be non-existing on the board to avoid dups.
    final String summonTargetName = effect.isUnique ?
        RandomMinionGenerator.randomUnique(summonChoices, new ArrayList<>(side.board.asList())) :
        RandomMinionGenerator.randomOne(summonChoices);
    final ConstMinion summonTarget = ConstMinion.valueOf(summonTargetName);
    final Minion minion = MinionFactory.create(summonTarget);
    return Collections.singletonList(new SummonEffect(side.board, minion));
  }

  private static List<Effect> getDrawCardEffect(final EffectConfig effect, final Side side) {
    // TODO: draw from own deck/opponent deck/opponent hand
    final TargetConfig target = effect.target;
    switch (target.type) {

    }
    return Collections.singletonList(new MoveCardEffect(side.hand, side.deck, side));
  }

  private static List<Effect> getCrystalEffect(final EffectConfig config, final Creature creature) {
    final String type = config.type;
    final ManaCrystal manaCrystal = creature.binder().getSide().manaCrystal;
    switch (type) {
      case (Constant.CRYSTAL_LOCK):
        return Collections.singletonList(new OverloadEffect(manaCrystal, config.value));
      default:
        throw new IllegalArgumentException("Unknown type: " + type);
    }
  }

  private static Effect getHealthAttributeEffect(final Creature creature, final EffectConfig effect) {
    final int value;
    if (effect.valueDependency.isPresent()) {
      value = getValueByDependency(effect.valueDependency.get(), creature.binder().getSide());
    } else {
      value = effect.value;
      Preconditions.checkArgument(value != 0, "Health change must be non-zero");
    }

    final int adjustChange = (value > 0) ? Math.min(value, creature.healthLoss()) : value;
    return new AttributeEffect(creature.health(), adjustChange, effect.isPermanent);
  }

  private static int getValueByDependency(final ConstDependency constDependency, final Side side) {
    switch (constDependency) {
      case BOARD_SIZE:
        // Minus one because at this moment the minion is put on board already.
        return side.board.size() - 1;
      case HEALTH_LOSS:
        return side.hero.healthLoss();
      case MINIONS_PLAYED:
        // TODO: need an accumulator.
        return 0;
      default:
        throw new RuntimeException("Unknown dependency: " + constDependency);
    }
  }

  private static Effect getGeneralAttributeEffect(final Side side, final ValueAttribute attr,
                                                  final EffectConfig effect) {
    final int value;
    if (effect.valueDependency.isPresent()) {
      value = getValueByDependency(effect.valueDependency.get(), side);
    } else {
      value = effect.value;
      Preconditions.checkArgument(value != 0, "Health change must be non-zero");
    }
    Preconditions.checkArgument(value != 0, "Attribute change must be non-zero");
    return new AttributeEffect(attr, value, effect.isPermanent);
  }

  public static void pipeEffectsByConfig(final Spell spell, final Creature creature) {
    final List<Effect> effects = spell.getEffects().stream()
        .flatMap(effect -> pipeEffectsByConfig(effect, creature).stream())
        .collect(Collectors.toList());
    spell.binder().getSide().getEffectQueue().enqueue(effects);
  }

  public static List<Effect> pipeEffectsByConfig(final SpellConfig config, final Creature creature) {
    return config.effects.stream()
        .flatMap(effect -> pipeEffectsByConfig(effect, creature).stream())
        .collect(Collectors.toList());
  }


  public static class AttackFactory {

    public static void getPhysicalDamageEffect(final Creature attacker, final Creature attackee) {
      final List<Effect> effects = attacker.booleanMechanics().has(ConstMechanic.FORGETFUL) ?
          getForgetfulPhysicalDamageEffect(attacker, attackee) :
          Collections.singletonList(new PhysicalDamageEffect(attacker, attackee));
      attacker.binder().getSide().getEffectQueue().enqueue(effects);
    }

    private static List<Effect> getForgetfulPhysicalDamageEffect(final Creature attacker, final
    Creature attackee) {
      final boolean isForgetfulToPickNewTarget = RandomMinionGenerator.getBool();
      final Effect attackEffect;
      if (isForgetfulToPickNewTarget) {
        logger.debug("Forgetful triggered");
        final Creature substituteAttackee = RandomMinionGenerator.randomExcept(attackee.binder().getSide().allCreatures(), attackee);
        logger.debug(String.format("Change attackee from %s to %s", attackee.toString(), substituteAttackee.toString()));
        Preconditions.checkArgument(substituteAttackee != attackee);
        attackEffect = new PhysicalDamageEffect(attacker, substituteAttackee);
      } else {
        logger.debug("Forgetful not triggered");
        attackEffect = new PhysicalDamageEffect(attacker, attackee);
      }

      return Collections.singletonList(attackEffect);
    }
  }
}
