package com.herthrone.factory;

import com.google.common.base.Preconditions;
import com.herthrone.base.Creature;
import com.herthrone.base.Destroyable;
import com.herthrone.base.Effect;
import com.herthrone.base.Mechanic;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConditionConfig;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.constant.ConstSelect;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstType;
import com.herthrone.effect.HealEffect;
import com.herthrone.game.Side;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TriggerFactory {

  private final static Logger logger = Logger.getLogger(TriggerFactory.class.getName());

  /**
   * Actively trigger passive mechanics.
   * Each mechanics has its own target which is chosen on-the-fly.
   *
   * @param triggerrer
   */
  public static void activeTrigger(final Mechanic.ActiveMechanic triggerrer) {
    Preconditions.checkArgument(
        triggerrer.getSelectTargetConfig().select.equals(ConstSelect.PASSIVE));
    triggerWithoutTarget(
        triggerrer, triggerrer.getActiveMechanics().get(ConstTrigger.ON_PLAY));
  }

  /**
   * Actively trigger mechanics with a specific target.
   * At least one of the mechanics will use passed-in creature as target. Note that not all of the
   * mechanics will be applied to this selected target.
   *
   * @param triggerrer
   * @param selectedTarget
   */
  public static void activeTrigger(final Mechanic.ActiveMechanic triggerrer,
                                   final Creature selectedTarget) {
    Preconditions.checkArgument(
        triggerrer.getSelectTargetConfig().select.equals(ConstSelect.MANDATORY) ||
        triggerrer.getSelectTargetConfig().select.equals(ConstSelect.OPTIONAL));
    final Side side = triggerrer.binder().getSide();
    triggerrer.getActiveMechanics()
        .get(ConstTrigger.ON_PLAY)
        .forEach(mechanicConfig -> TargetFactory.getTarget(
            triggerrer, selectedTarget, side, mechanicConfig.targetOptional)
            .forEach(t -> EffectFactory.pipeMechanicEffectConditionally(mechanicConfig, side, t))
        );
  }


  static void passiveTrigger(final Mechanic.ActiveMechanic triggerrer,
                             final ConstTrigger triggerType) {
    Preconditions.checkArgument(!triggerType.equals(ConstTrigger.ON_PLAY));
    final List<MechanicConfig> mechanicConfigs = triggerrer.getActiveMechanics().get(
        triggerType);
    if (!mechanicConfigs.isEmpty()) {
      triggerWithoutTarget(triggerrer, mechanicConfigs);
    }
  }

  private static void triggerWithoutTarget(final Mechanic.ActiveMechanic triggerrer,
                                           final List<MechanicConfig> mechanicConfigs) {
    mechanicConfigs.stream()
        .filter(mechanicConfig -> !mechanicConfig.triggerOnlyWithTarget)
        .forEach(mechanicConfig -> {
          final List<Creature> targets = TargetFactory.getProperTargets(triggerrer,
                mechanicConfig.targetOptional.get(), triggerrer.binder().getSide());
          targets.forEach(
              target -> EffectFactory.pipeMechanicEffectConditionally(
                  mechanicConfig, triggerrer.binder().getSide(), target));
        });
  }

  public static void triggerOnHealMinion(final Effect effect) {
    if (effect instanceof HealEffect && ((HealEffect) effect).getTarget() instanceof Minion) {
      final Side side = ((HealEffect) effect).getTarget().binder().getSide();
      TriggerFactory.triggerByBoard(side, ConstTrigger.ON_HEAL_MINION);
    }
  }

  public static void triggerByBoard(final Stream<Minion> minionStream,
                                    final Creature selectedTarget, final ConstTrigger triggerType) {
    Preconditions.checkArgument(!triggerType.equals(ConstTrigger.ON_PLAY));
    final Side side = selectedTarget.binder().getSide();
    minionStream.forEach(triggerrer ->
    triggerrer.getActiveMechanics().get(triggerType)
        .forEach(mechanicConfig -> {
          final List<Creature> targets = (mechanicConfig.targetOptional.isPresent()) ?
              TargetFactory.getProperTargets(triggerrer, mechanicConfig.targetOptional.get(), side) :
              Collections.singletonList(selectedTarget);
          targets.forEach(target ->
              EffectFactory.pipeMechanicEffectConditionally(mechanicConfig, side, target));
        })
    );
  }

  public static void triggerByBoard(final Stream<Minion> minionStream, final Side triggeringSide,
                                    final ConstTrigger triggerType) {
    minionStream
        .sorted(EffectFactory.compareBySequenceId)
        .forEach(minion ->
            minion.getActiveMechanics().get(triggerType)
                .forEach(mechanicConfig ->
                  TargetFactory.getTarget(
                      minion, minion, triggeringSide, mechanicConfig.targetOptional)
                      .forEach(target -> EffectFactory.pipeMechanicEffectConditionally(
                          mechanicConfig, triggeringSide, target))
                )
        );
  }

  public static void triggerByBoard(final Side side, final ConstTrigger triggerType) {
    logger.debug("Triggering " + triggerType + " on own side board");
    triggerByBoard(side.board.stream(), side, triggerType);
    logger.debug("Triggering " + triggerType + " on foe side board");
    triggerByBoard(side.getFoeSide().board.stream(), side, triggerType);
  }

  static void refreshAura(final Side side) {
    logger.debug("Updating aura effects on all minions");
    side.board.stream().forEach(Minion::refresh);
  }

  static void refreshSpellDamage(final Side side)  {
    logger.debug("Updating spell damage on all spells in hand");
    side.hand.stream()
        .filter(card -> card instanceof Spell)
        .map(card -> (Spell) card)
        .forEach(Spell::refresh);
  }

  public static boolean isTriggerConditionMet(final MechanicConfig mechanicConfig,
                                              final Side activatingSide,
                                              final Creature target) {
    final Side targetSide;
    if (mechanicConfig.targetOptional.isPresent()) {
      final List<Side> realSide = TargetFactory.getSide(
          mechanicConfig.targetOptional.get(), activatingSide);
      Preconditions.checkArgument(realSide.size() == 1, "Does not support two side check");
      targetSide = realSide.get(0);
    } else {
      targetSide = activatingSide;
    }
    final boolean willBeTriggered = isConditionTriggered(mechanicConfig, targetSide, target);
    final String word = willBeTriggered ? "is" : "is not";
    logger.debug(String.format("Condition %s met and mechanic effect %s triggered", word, word));
    return willBeTriggered;
  }

  private static boolean isConditionTriggered(final MechanicConfig mechanicConfig, final Side side,
                                              final Creature target) {
    if (!mechanicConfig.conditionConfigOptional.isPresent()) {
      // If no condition configured, return true and the effect should be triggered any way.
      return true;
    }
    // Check if there is condition config. If there is, return whether condition is met.
    final ConditionConfig conditionConfig = mechanicConfig.conditionConfigOptional.get();
    switch (conditionConfig.conditionType) {
      case ATTACK_VALUE:
        return conditionConfig.inRange(target.attack().value());
      case TARGET_TYPE_BEAST:
        return target.type().equals(ConstType.BEAST);
      case BEAST_COUNT:
        final int beastCount = side.board.stream()
            .filter(m -> m.type().equals(ConstType.BEAST))
            .collect(Collectors.toList()).size();
        return conditionConfig.inRange(beastCount);
      case BOARD_SIZE:
        return conditionConfig.inRange(side.board.size());
      case COMBO:
        return side.replay.size() > 1;
      case HAND_SIZE:
        return conditionConfig.inRange(side.hand.size());
      case HEALTH_LOSS:
        return conditionConfig.inRange(target.healthLoss());
      case HEALTH_VALUE:
        return conditionConfig.inRange(target.health().value());
      case WEAPON_EQUIPPED:
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
}
