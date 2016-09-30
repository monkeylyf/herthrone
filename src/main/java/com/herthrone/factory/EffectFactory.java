package com.herthrone.factory;

import com.google.common.base.Preconditions;
import com.herthrone.base.Card;
import com.herthrone.base.Creature;
import com.herthrone.base.Destroyable;
import com.herthrone.base.Effect;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
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
import com.herthrone.effect.AddTriggeringMechanicEffect;
import com.herthrone.effect.AttributeEffect;
import com.herthrone.effect.BuffEffect;
import com.herthrone.effect.CopyCardEffect;
import com.herthrone.effect.DestroyEffect;
import com.herthrone.effect.DiscardEffect;
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
import com.herthrone.effect.TakeDamageEffect;
import com.herthrone.effect.TransformEffect;
import com.herthrone.game.Container;
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

  static class AuraEffectFactory {

    static void addAuraEffect(final MechanicConfig mechanicConfig, final Minion minion,
                              final Minion target) {
      Preconditions.checkArgument(mechanicConfig.targetOptional.isPresent());
      final TargetConfig targetConfig = mechanicConfig.targetOptional.get();
      switch (mechanicConfig.type) {
        case Constant.ATTACK:
          if (targetConfig.isAdjacent &&
              !target.binder().getSide().board.isAdjacent(target, minion)) {
            logger.debug(minion + " and " + target + " is not adjacent. " + target +
                " will not be effected by aura");
          } else {
            target.attack().addAuraBuff(minion, mechanicConfig.value);
          }
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

  public static void pipeMechanicEffectConditionally(final MechanicConfig mechanicConfig,
                                                     final Side triggeringSide,
                                                     final Creature target) {
    if (TriggerFactory.isTriggerConditionMet(mechanicConfig, triggeringSide, target)) {
      logger.debug("Triggering " + mechanicConfig.mechanic);
      final List<Effect> effects = pipeEffects(mechanicConfig, target, triggeringSide);
      triggeringSide.getEffectQueue().enqueue(effects);
    } else {
      logger.debug(mechanicConfig.mechanic + " passed because triggering condition not met");
    }
  }

  private static List<Effect> getCopyCardEffect(MechanicConfig config, Side side) {
    final Side opponentSide = side.getOpponentSide();
    switch (config.type) {
      case Constant.HAND:
        if (opponentSide.hand.isEmpty()) {
          return Collections.emptyList();
        } else {
          Preconditions.checkArgument(config.targetOptional.isPresent());
          final TargetConfig targetConfig = config.targetOptional.get();
          Preconditions.checkArgument(targetConfig.randomTarget.isPresent());
          final int n = targetConfig.randomTarget.getAsInt();
          return RandomMinionGenerator.randomN(opponentSide.hand.asList(), n).stream()
              .map(card -> new CopyCardEffect(card, side.hand))
              .collect(Collectors.toList());
        }
      default:
        throw new IllegalArgumentException("unknown copy card type: " + config.type);
    }
  }

  public static List<Effect> pipeEffects(final MechanicConfig config, final Creature target,
                                         final Side triggeringSide) {
    switch (config.effectType) {
      case ADD_MECHANIC_TRIGGER:
        // TODO: Need a way to add complicated triggering mechanics on-the-fly.
        Preconditions.checkArgument(config.mechanicToAddOptional.isPresent());
        final Minion minion = creatureToMinion(target);
        final Effect effect = new AddTriggeringMechanicEffect(
                minion.getTriggeringMechanics(), config.mechanicToAddOptional.get());
        return Collections.singletonList(effect);
      case ADD_MECHANIC:
        final ConstMechanic mechanic = ConstMechanic.valueOf(config.type.toUpperCase());
        return Collections.singletonList(new AddMechanicEffect(mechanic, target));
      case ATTRIBUTE:
        return getAttributeEffect(config, target);
      case BUFF:
        return getBuffEffect(config, target);
      case COPY_CARD:
        return getCopyCardEffect(config, triggeringSide);
      case CRYSTAL:
        return getCrystalEffect(config, target);
      case DISCARD:
        return getDiscardEffect(config, target);
      case DESTROY:
        return Collections.singletonList(new DestroyEffect(creatureToDestroyable(target)));
      case DRAW:
        return Collections.singletonList(new MoveCardEffect(
            triggeringSide.hand, triggeringSide.deck, triggeringSide, config.value));
      case FULL_HEAL:
        return (target.healthLoss() > 0) ?
          Collections.singletonList(new HealEffect(target, target.healthLoss())) :
          Collections.emptyList();
      case HEAL:
        return (target.healthLoss() > 0) ?
          Collections.singletonList(new HealEffect(target, config.value)) :
          Collections.emptyList();
      case GENERATE:
        return getGenerateEffect(config, triggeringSide);
      case RETURN_TO_HAND:
        return Collections.singletonList(new ReturnToHandEffect(creatureToMinion(target)));
      case SET:
        return getSetAttributeEffect(config, target);
      case SUMMON:
        return getSummonEffect(config, target.binder().getSide());
      case TAKE_CONTROL:
        return Collections.singletonList(new TakeControlEffect(creatureToMinion(target)));
      case TRANSFORM:
        final Minion minionTarget = creatureToMinion(target);
        return Collections.singletonList(new TransformEffect(minionTarget, config.choices));
      case WEAPON:
        return getEquipWeaponEffect(config, creatureToHero(target));
      default:
        throw new IllegalArgumentException("Unknown effect type: " + config.effectType);
    }
  }

  private static List<Effect> getDiscardEffect(final MechanicConfig config, final Creature target) {
    Preconditions.checkArgument(config.targetOptional.isPresent());
    final TargetConfig targetConfig = config.targetOptional.get();
    final Container<Card> container;
    switch (targetConfig.type) {
      case DECK:
        container = target.binder().getSide().deck;
        break;
      case HAND:
        container = target.binder().getSide().hand;
        break;
      default:
        throw new IllegalArgumentException("Unsupported target type: " + targetConfig.type);
    }
    List<Effect> effects = new ArrayList<>();
    final boolean discardRandomly = targetConfig.type.equals(ConstType.HAND);
    // Discard as many as configured.
    final int numberOfCardToDiscard = Math.min(config.value, container.size());
    for (int i = 0; i < numberOfCardToDiscard; ++i) {
      effects.add(new DiscardEffect(container, discardRandomly));
    }
    return effects;
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
        if (effect.targetOptional.isPresent() &&
            effect.targetOptional.get().type.equals(ConstType.WEAPON)) {
          final Hero hero = creatureToHero(creature);
          final Weapon weapon = hero.getWeapon().get();
          return Collections.singletonList(getGeneralBuffEffect(
              side, weapon.getAttackAttr(), effect));
        } else {
          return Collections.singletonList(getGeneralBuffEffect(side, creature.attack(), effect));
        }
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

  private static List<Effect> getGenerateEffect(final MechanicConfig config, final Side side) {
    final Effect generateEffect = new GenerateEffect(
        config.choices, config.type, config.targetOptional.get(), side);
    return Collections.singletonList(generateEffect);
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

  private static Destroyable creatureToDestroyable(final Creature creature) {
    if (creature instanceof Hero) {
      final Hero hero = (Hero) creature;
      Preconditions.checkArgument(hero.getWeapon().isPresent(), "Hero has no weapon equipped");
      return hero.getWeapon().get();
    } else {
      return (Minion) creature;
    }
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
      // TODO: set sequence id as well, somewhere.
      side.bind(minion);
      summonEffects.add(new SummonEffect(side, minion));
    }
    return summonEffects;
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
    return new TakeDamageEffect(creature, value);
  }

  private static int getValueByDependency(final ConstDependency constDependency, final Side side) {
    switch (constDependency) {
      case BOARD_SIZE:
        // At this moment, the minion frostwolf warlord has not been played on board yet.
        return side.board.size();
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
    Preconditions.checkArgument(effect.value != 0, "Attribute change must be non-zero");
    return new AttributeEffect(attr, effect.value, effect.isPermanent);
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
      if (isForgetfulToPickNewTarget) {
        logger.debug("Forgetful triggered");
        final Creature substituteAttackee = RandomMinionGenerator.randomExcept(
            attackee.binder().getSide().allCreatures(), attackee);
        logger.debug(String.format("Change attackee from %s to %s", attackee, substituteAttackee));
        Preconditions.checkArgument(substituteAttackee != attackee);
        return Collections.singletonList(new PhysicalDamageEffect(attacker, substituteAttackee));
      } else {
        logger.debug("Forgetful not triggered");
        return Collections.singletonList(new PhysicalDamageEffect(attacker, attackee));
      }
    }
  }
}
