package com.herthrone.factory;

import com.google.common.base.Optional;
import com.herthrone.base.Creature;
import com.herthrone.base.Effect;
import com.herthrone.base.Mechanic;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.game.Side;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TriggerFactory {

  private final static Logger logger = Logger.getLogger(TriggerFactory.class.getName());

  public static void activeTrigger(final Mechanic.TriggeringMechanic triggerrer,
                                   final ConstTrigger triggerType, final Creature target) {
    activeTrigger(triggerrer, triggerType, target, target.binder().getSide());

  }
  public static void activeTrigger(final Mechanic.TriggeringMechanic triggerrer,
                                   final ConstTrigger triggerType, final Creature target,
                                   final Side triggeringSide) {
    triggerrer.getTriggeringMechanics().get(triggerType).stream()
        .forEach(mechanicConfig ->
            EffectFactory.pipeMechanicEffectConditionally(
                Optional.of(mechanicConfig), triggeringSide, target)
      );
  }

  static void passiveTrigger(final Side side, final ConstTrigger triggerType) {
    List<Effect> effects = side.board.stream()
        .sorted(EffectFactory.compareBySequenceId)
        .flatMap(minion -> minion.getTriggeringMechanics().get(triggerType).stream())
        .flatMap(mechanic -> EffectFactory.getMechanicEffects(mechanic, side).stream())
        .collect(Collectors.toList());
    side.getEffectQueue().enqueue(effects);
  }

  public static void passiveTrigger(final Mechanic.TriggeringMechanic triggerrer,
                             final ConstTrigger triggerType) {
    final Side side = triggerrer.binder().getSide();
    List<MechanicConfig> mechanicConfigs = triggerrer.getTriggeringMechanics().get(triggerType);
    triggerWithoutTarget(mechanicConfigs, side);
  }

  static void triggerWithoutTarget(final List<MechanicConfig> mechanicConfigs, final Side
      side) {
    final List<MechanicConfig> validMechanicConfigs = mechanicConfigs.stream()
        .filter(mechanicConfig -> !mechanicConfig.triggerOnlyWithTarget)
        .collect(Collectors.toList());
    logger.debug(String.format("Total %d valid mechanic configs", validMechanicConfigs.size()));
    validMechanicConfigs.stream()
        .forEach(effectConfig -> {
          try {
            TargetFactory.getProperTargets(effectConfig.targetOptional.get(), side).stream()
                .forEach(
                    target -> EffectFactory.pipeMechanicEffectConditionally(
                        Optional.of(effectConfig), side, target)
                );
          } catch (TargetFactory.NoTargetFoundException error) {
            EffectFactory.pipeMechanicEffectConditionally(Optional.of(effectConfig), side);
          }
        });
  }

  public static void triggerByBoard(final Minion excludedMinion,
                                    final ConstTrigger triggerType) {
    final Side side = excludedMinion.binder().getSide();
    side.board.stream()
        .filter(minion -> minion != excludedMinion)
        .sorted(EffectFactory.compareBySequenceId)
        .forEach(minion ->
            minion.getTriggeringMechanics().get(triggerType).stream()
              .forEach(mechanicConfig -> EffectFactory.pipeMechanicEffectConditionally(
                  Optional.of(mechanicConfig), side, minion))
        );
  }

  public static void triggerByBoard(final Stream<Minion> minionStream, final Side triggeringSide,
                                    final ConstTrigger triggerType) {
    minionStream
        .sorted(EffectFactory.compareBySequenceId)
        .forEach(minion ->
            minion.getTriggeringMechanics().get(triggerType).stream()
                .forEach(mechanicConfig -> EffectFactory.pipeMechanicEffectConditionally(
                    Optional.of(mechanicConfig), triggeringSide, minion))
        );
  }

  public static void triggerByBoard(final Side side, final Side opponentSide,
                                    final ConstTrigger triggerType) {
    triggerByBoard(side.board.stream(), side, triggerType);
    triggerByBoard(opponentSide.board.stream(), side, triggerType);
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

}
