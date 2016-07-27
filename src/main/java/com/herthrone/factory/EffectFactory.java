package com.herthrone.factory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.herthrone.base.Card;
import com.herthrone.base.Creature;
import com.herthrone.base.Destroyable;
import com.herthrone.base.Effect;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConditionConfig;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstDependency;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.constant.Constant;
import com.herthrone.effect.AddMechanicEffect;
import com.herthrone.effect.AttributeEffect;
import com.herthrone.effect.BuffEffect;
import com.herthrone.effect.CopyCardEffect;
import com.herthrone.effect.DestroyEffect;
import com.herthrone.effect.EquipWeaponEffect;
import com.herthrone.effect.GenerateEffect;
import com.herthrone.effect.HealEffect;
import com.herthrone.effect.ManaCrystalEffect;
import com.herthrone.effect.MaxHealthBuffEffect;
import com.herthrone.effect.MaxManaCrystalEffect;
import com.herthrone.effect.MoveCardEffect;
import com.herthrone.effect.OverloadEffect;
import com.herthrone.effect.PhysicalDamageEffect;
import com.herthrone.effect.ReturnToHandEffect;
import com.herthrone.effect.SetAttributeEffect;
import com.herthrone.effect.SummonEffect;
import com.herthrone.effect.TakeControlEffect;
import com.herthrone.effect.TransformEffect;
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

public class EffectFactory {

  private static Logger logger = Logger.getLogger(EffectFactory.class.getName());
  static final Comparator<Minion> compareBySequenceId = (m1, m2) -> Integer.compare(
      m1.getSequenceId(), m2.getSequenceId());

  public static class AuraEffectFactory {

    static void addAuraEffect(final MechanicConfig mechanicConfig, final Minion minion,
                              final Minion target) {
      switch (mechanicConfig.type) {
        case Constant.ATTACK:
          target.attack().addAuraBuff(minion, mechanicConfig.value);
          break;
        case Constant.MAX_HEALTH:
          target.maxHealth().addAuraBuff(minion, mechanicConfig.value);
          target.health().addAuraBuff(minion, mechanicConfig.value);
          break;
        case Constant.CHARGE:
          final ValueAttribute movePoints = target.attackMovePoints();
          if (movePoints.getTemporaryBuff().value() == -1) {
            movePoints.getTemporaryBuff().increase(1);
          }
          break;
        default:
          throw new RuntimeException(mechanicConfig.type + " not supported for aura");
      }
    }

    static void removeAuraEffect(final MechanicConfig mechanicConfig, final Minion minion,
                                 final Minion target) {
      switch (mechanicConfig.type) {
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
          throw new RuntimeException(mechanicConfig.type + " not supported for aura");
      }
    }
  }

  public static boolean isTriggerConditionMet(final Optional<MechanicConfig> mechanicConfigOptional,
                                              final Side side) {
    if (!mechanicConfigOptional.isPresent()) {
      logger.debug("Mechanic configuration is absent");
      return true;
    }
    final MechanicConfig mechanicConfig = mechanicConfigOptional.get();
    if (!mechanicConfig.targetOptional.isPresent()) {
      return true;
    }
    final List<Side> realSide = TargetFactory.getSide(mechanicConfig.targetOptional.get(), side);
    Preconditions.checkArgument(realSide.size() == 1, "Does not support two side check");
    final boolean willBeTriggered = isConditionTriggered(mechanicConfig, realSide.get(0));
    final String word = willBeTriggered ? "is" : "is not";
    logger.debug(String.format("Condition %s met and mechanic effect %s triggered", word, word));
    return willBeTriggered;
  }

  public static void pipeMechanicEffectConditionally(
      final Optional<MechanicConfig> mechanicConfigOptional,
      final Side triggeringSide, final Creature target) {
    if (isTriggerConditionMet(mechanicConfigOptional, triggeringSide)) {
      final MechanicConfig mechanicConfig = mechanicConfigOptional.get();
      logger.debug("Triggering " + mechanicConfig.mechanic.toString());
      final List<Effect> effects = getMechanicEffects(mechanicConfig, target, triggeringSide);
      triggeringSide.getEffectQueue().enqueue(effects);
    }
  }

  public static void pipeMechanicEffectConditionally(
      final Optional<MechanicConfig> mechanicConfigOptional,
      final Side side) {
    if (isTriggerConditionMet(mechanicConfigOptional, side)) {
      final MechanicConfig mechanicConfig = mechanicConfigOptional.get();
      logger.debug("Triggering " + mechanicConfig.mechanic.toString());
      final List<Effect> effects = getMechanicEffects(mechanicConfig, side);
      side.getEffectQueue().enqueue(effects);
    }
  }

  public static void triggerEndTurnMechanics(final Side side) {
    final ConstTrigger endTurnTrigger = ConstTrigger.ON_END_TURN;
    final List<Minion> minions = side.board.stream()
        .sorted(compareBySequenceId)
        .filter(minion -> minion.getTriggeringMechanics().has(endTurnTrigger))
        .collect(Collectors.toList());

    for (final Minion minion : minions) {
      for (MechanicConfig mechanic : minion.getTriggeringMechanics().get(endTurnTrigger)) {
        final List<Creature> targets = TargetFactory.getProperTargets(
            mechanic.targetOptional.get(), side);
        final List<Effect> effects = targets.stream()
            .flatMap(target -> getMechanicEffects(mechanic, target, side).stream())
            .collect(Collectors.toList());
        side.getEffectQueue().enqueue(effects);
      }
    }
  }

  private static boolean isConditionTriggered(final MechanicConfig mechanicConfig,
                                              final Side side) {
    if (!mechanicConfig.conditionConfigOptional.isPresent()) {
      // If no condition configured, return true and the effect should be triggered any way.
      return true;
    }
    // Check if there is condition config. If there is, return whether condition is met.
    final ConditionConfig conditionConfig = mechanicConfig.conditionConfigOptional.get();
    switch (conditionConfig.conditionType) {
      case BEAST_COUNT:
        final int beastCount = side.board.stream()
            .filter(m -> m.type().equals(ConstType.BEAST))
            .collect(Collectors.toList()).size();
        return conditionConfig.inRange(beastCount);
      case BOARD_SIZE:
        return conditionConfig.inRange(side.board.size());
      case COMBO:
        return side.replay.size() > 1;
      case WEAPON_EQUIPED:
        // Call getDestroyablesBySide instead of getDestroyables because side is already picked
        // given target config.
        final List<Destroyable> destroyables = TargetFactory.getDestroyablesBySide(
            mechanicConfig.targetOptional.get(), side);
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
  }

  public static List<Effect> getMechanicEffects(final MechanicConfig mechanic, final Side side) {
    try {
      List<Creature> targets = TargetFactory.getProperTargets(mechanic.targetOptional.get(), side);
      return targets.stream()
          .flatMap(target -> pipeEffects(mechanic, target, side).stream())
          .collect(Collectors.toList());
    } catch (TargetFactory.NoTargetFoundException error) {
      return pipeEffects(mechanic, side);
    }
  }

  public static List<Effect> getMechanicEffects(final MechanicConfig mechanic,
                                                final Creature target, final Side triggeringSide) {
    if (mechanic.targetOptional.isPresent()) {
      final TargetConfig targetConfig = mechanic.targetOptional.get();
      if (targetConfig.type.equals(ConstType.OTHER)) {
        // For swipe.
        return TargetFactory.getOtherTargets(target).stream()
            .flatMap(realTarget -> pipeEffects(mechanic, realTarget, triggeringSide).stream())
            .collect(Collectors.toList());
      } else {
        final Creature realTarget = targetConfig.isRandom ?
            RandomMinionGenerator.randomCreature(targetConfig, target.binder().getSide()) : target;
        //RandomMinionGenerator.randomOne(
        //    TargetFactory.getProperTargetsBySide(targetConfig, target.binder().getSide())) :
        //    target;
        return pipeEffects(mechanic, realTarget, triggeringSide);
      }
    } else {
      return pipeEffects(mechanic, target, triggeringSide);
    }
  }

  public static List<Effect> pipeEffects(final MechanicConfig config, final Side side) {
    switch (config.effectType) {
      case COPY_CARD:
        return getCopyCardEffect(config, side);
      case SUMMON:
        return getSummonEffect(config, side);
      case GENERATE:
        return getGenerateEffect(config, side);
      case DRAW:
        return getDrawCardEffect(config, side);
      case DESTROY:
        return getDestroyEffect(config, side);
      default:
        throw new IllegalArgumentException("unknown: " + config.effectType);
    }
  }

  private static List<Effect> getCopyCardEffect(MechanicConfig config, Side side) {
    final Side opponentSide = side.getOpponentSide();
    switch (config.type) {
      case Constant.HAND:
        if (opponentSide.hand.isEmpty()) {
          return Collections.emptyList();
        } else {
          final Card cardToCopy = RandomMinionGenerator.randomOne(opponentSide.hand.asList());
          return Collections.singletonList(new CopyCardEffect(cardToCopy, side.hand));
        }
      default:
        throw new IllegalArgumentException("unknown copy card type: " + config.type);
    }
  }

  public static List<Effect> pipeEffects(final MechanicConfig config, final Creature target) {
    return pipeEffects(config, target, target.binder().getSide());
  }

  public static List<Effect> pipeEffects(final MechanicConfig config, final Creature target,
                                         final Side triggeringSide) {
    switch (config.effectType) {
      case ADD_MECHANIC:
        final ConstMechanic mechanic = ConstMechanic.valueOf(config.type.toUpperCase());
        return Collections.singletonList(new AddMechanicEffect(mechanic, target));
      case ATTRIBUTE:
        return getAttributeEffect(config, target);
      case BUFF:
        return getBuffEffect(config, target);
      case CRYSTAL:
        return getCrystalEffect(config, target);
      case DRAW:
        return getDrawCardEffect(config, triggeringSide);
      case HEAL:
        if (target.healthLoss() > 0) {
          return Collections.singletonList(new HealEffect(target, config.value));
        } else {
          return Collections.emptyList();
        }
      case RETURN_TO_HAND:
        return getReturnToHandEffect(creatureToMinion(target));
      case SET:
        return getSetAttributeEffect(config, target);
      case SUMMON:
        return getSummonEffect(config, target.binder().getSide());
      case TAKE_CONTROL:
        return getTakeControlEffect(config, target);
      case TRANSFORM:
        final Minion minionTarget = creatureToMinion(target);
        return Collections.singletonList(new TransformEffect(minionTarget, config.choices));
      case WEAPON:
        return getEquipWeaponEffect(config, creatureToHero(target));
      default:
        throw new IllegalArgumentException("Unknown effect type: " + config.effectType);
    }
  }

  private static List<Effect> getSetAttributeEffect(final MechanicConfig config,
                                                    final Creature target) {
    switch (config.type) {
      case Constant.MAX_HEALTH:
        return Arrays.asList(
            new SetAttributeEffect(target.maxHealth(), config.value),
            new SetAttributeEffect(target.health(), config.value));
      case Constant.ATTACK:
        return Collections.singletonList(new SetAttributeEffect(target.attack(), config.value));
      default:
        throw new IllegalArgumentException("Unknown effect type: " + config.effectType);
    }
  }

  private static List<Effect> getBuffEffect(final MechanicConfig effect, final Creature creature) {
    final Side side = creature.binder().getSide();
    final String type = effect.type;
    switch (type) {
      case (Constant.ATTACK):
        return Collections.singletonList(getGeneralBuffEffect(side, creature.attack(), effect));
      case (Constant.CRYSTAL):
        return Collections.singletonList(getGeneralBuffEffect(side, creature.manaCost(), effect));
      case (Constant.MAX_HEALTH):
        return Collections.singletonList(getMaxHealthBuffEffect(creatureToMinion(creature), effect));
      default:
        throw new IllegalArgumentException("Unknown effect type for buff: " + type);
    }
  }

  private static Effect getMaxHealthBuffEffect(final Minion minion, final MechanicConfig effect) {
    final int gain = (effect.isFolded) ?
        minion.maxHealth().value() * (effect.value - 1) :
        getGain(minion.binder().getSide(), effect);
    return new MaxHealthBuffEffect(minion, gain);
  }

  private static int getGain(final Side side, final MechanicConfig mechanicConfig) {
    if (mechanicConfig.valueDependency.isPresent()) {
      return getValueByDependency(mechanicConfig.valueDependency.get(), side);
    } else {
      Preconditions.checkArgument(mechanicConfig.value != 0, "Gain value must be non-zero");
      return mechanicConfig.value;
    }
  }

  private static Effect getGeneralBuffEffect(final Side side, final ValueAttribute attribute,
                                             final MechanicConfig effect) {
    final int gain = getGain(side, effect);
    return new BuffEffect(attribute, gain, effect.isPermanent);
  }

  private static List<Effect> getDestroyEffect(final MechanicConfig config, final Side side) {
    List<Effect> effects = TargetFactory.getDestroyables(config.targetOptional.get(), side)
        .stream()
        .map(DestroyEffect::new)
        .collect(Collectors.toList());
    return effects;
  }

  private static List<Effect> getReturnToHandEffect(final Minion target) {
    return Collections.singletonList(new ReturnToHandEffect(target));
  }

  private static List<Effect> getGenerateEffect(final MechanicConfig config, final Side side) {
    final Effect generateEffect = new GenerateEffect(
        config.choices, config.type, config.targetOptional.get(), side);
    return Collections.singletonList(generateEffect);
  }

  private static List<Effect> getTakeControlEffect(final MechanicConfig effect,
                                                   final Creature creature) {
    final Creature traitorMinion = effect.targetOptional.isPresent() ?
        RandomMinionGenerator.randomCreature(
            effect.targetOptional.get(), creature.binder().getSide()) :
        creature;
    Preconditions.checkArgument(traitorMinion instanceof Minion);
    return Collections.singletonList(new TakeControlEffect((Minion) traitorMinion));
  }

  private static Hero creatureToHero(final Creature creature) {
    Preconditions.checkArgument(
        creature instanceof Hero, "Expect Hero instance, not %s", creature.type());
    return (Hero) creature;
  }

  private static Minion creatureToMinion(final Creature creature) {
    Preconditions.checkArgument(
        creature instanceof Minion, "Expect Minion instance, not %s", creature.type());
    return (Minion) creature;
  }

  private static List<Effect> getAttributeEffect(final MechanicConfig effect,
                                                 final Creature creature) {
    final Side side = creature.binder().getSide();
    final String type = effect.type;
    switch (type) {
      case (Constant.ARMOR):
        return Collections.singletonList(
            getGeneralAttributeEffect(side, creatureToHero(creature).armor(), effect));
      case (Constant.ATTACK):
        return Collections.singletonList(
            getGeneralAttributeEffect(side, creature.attack(), effect));
      case (Constant.CRYSTAL):
        return Collections.singletonList(
            getGeneralAttributeEffect(side, creature.manaCost(), effect));
      case (Constant.HEALTH):
        return Collections.singletonList(getHealthAttributeEffect(creature, effect));
      case (Constant.MAX_HEALTH):
        return Arrays.asList(
            getGeneralAttributeEffect(side, creature.maxHealth(), effect),
            getHealthAttributeEffect(creature, effect));
      case (Constant.MANA_CRYSTAL):
        return Collections.singletonList(
            new ManaCrystalEffect(creatureToHero(creature).manaCrystal(), effect.value));
      case (Constant.MAX_MANA_CRYSTAL):
        return Collections.singletonList(
            new MaxManaCrystalEffect(creatureToHero(creature).manaCrystal(), effect.value));
      default:
        throw new IllegalArgumentException("Unknown effect type: " + type);
    }
  }

  private static List<Effect> getEquipWeaponEffect(final MechanicConfig effect, final Hero hero) {
    final String weaponName = effect.type;
    final ConstWeapon weapon = ConstWeapon.valueOf(weaponName.toUpperCase());
    final Weapon weaponInstance = WeaponFactory.create(weapon);
    return Collections.singletonList(new EquipWeaponEffect(hero, weaponInstance));
  }

  private static List<Effect> getSummonEffect(final MechanicConfig effect, final Side side) {
    List<Effect> summonEffects = new ArrayList<>(effect.value);
    for (int i = 0; i < effect.value; ++i) {
      final List<String> summonChoices = effect.choices.stream()
          .map(String::toUpperCase).collect(Collectors.toList());
      // Summon candidates must be non-existing on the board to avoid dups.
      final String summonTargetName = effect.isUnique ?
          RandomMinionGenerator.randomUnique(summonChoices, new ArrayList<>(side.board.asList())) :
          RandomMinionGenerator.randomOne(summonChoices);
      final ConstMinion summonTarget = ConstMinion.valueOf(summonTargetName);
      final Minion minion = MinionFactory.create(summonTarget);
      summonEffects.add(new SummonEffect(side.board, minion));
    }
    return summonEffects;
  }

  private static List<Effect> getDrawCardEffect(final MechanicConfig effect, final Side side) {
    // TODO: draw from own deck/opponent deck/opponent hand
    final TargetConfig target = effect.targetOptional.get();
    switch (target.type) {

    }
    return Collections.singletonList(new MoveCardEffect(side.hand, side.deck, side));
  }

  private static List<Effect> getCrystalEffect(final MechanicConfig config, final Creature creature) {
    final String type = config.type;
    final ManaCrystal manaCrystal = creature.binder().getSide().hero.manaCrystal();
    switch (type) {
      case (Constant.CRYSTAL_LOCK):
        return Collections.singletonList(new OverloadEffect(manaCrystal, config.value));
      default:
        throw new IllegalArgumentException("Unknown type: " + type);
    }
  }

  private static Effect getHealthAttributeEffect(final Creature creature, final MechanicConfig effect) {
    final int value = (effect.valueDependency.isPresent()) ?
      getValueByDependency(effect.valueDependency.get(), creature.binder().getSide()) :
      effect.value;
    Preconditions.checkArgument(value >= 0, "damage must be non-negative");
    return new AttributeEffect(creature.health(), -value, effect.isPermanent);
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
                                                  final MechanicConfig effect) {
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

  public static void pipeEffects(final Spell spell, final Creature target) {
    pipeEffects(spell, target, target.binder().getSide());
  }
  public static void pipeEffects(final Spell spell, final Creature target,
                                 final Side triggeringSide) {
    final List<Effect> effects = spell.getTriggeringMechanics().get(ConstTrigger.ON_PLAY).stream()
        .flatMap(effect -> pipeEffects(effect, target, triggeringSide).stream())
        .collect(Collectors.toList());
    spell.binder().getSide().getEffectQueue().enqueue(effects);
  }

  public static void pipeEffects(final Spell spell) {
    TriggerFactory.triggerWithoutTarget(
        spell.getTriggeringMechanics().get(ConstTrigger.ON_PLAY), spell.binder().getSide());
  }

  public static class AttackFactory {

    public static void pipePhysicalDamageEffect(final Creature attacker, final Creature attackee) {
      final List<Effect> effects = attacker.booleanMechanics().isOn(ConstMechanic.FORGETFUL) ?
          getForgetfulPhysicalDamageEffect(attacker, attackee) :
          Collections.singletonList(new PhysicalDamageEffect(attacker, attackee));
      attacker.binder().getSide().getEffectQueue().enqueue(effects);
    }

    private static List<Effect> getForgetfulPhysicalDamageEffect(final Creature attacker,
                                                                 final Creature attackee) {
      final boolean isForgetfulToPickNewTarget = RandomMinionGenerator.getBool();
      final Effect attackEffect;
      if (isForgetfulToPickNewTarget) {
        logger.debug("Forgetful triggered");
        final Creature substituteAttackee = RandomMinionGenerator.randomExcept(attackee.binder().getSide().allCreatures(), attackee);
        logger.debug(String.format("Change attackee from %s to %s", attackee, substituteAttackee));
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
