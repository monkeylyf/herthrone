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

/**
 * Created by yifengliu on 7/10/16.
 */
public class TriggerFactory {

  private final static Logger logger = Logger.getLogger(TriggerFactory.class.getName());

  static void activeTrigger(final Mechanic.TriggeringMechanic triggerrer,
                            final ConstTrigger triggerType, final Creature target) {
    triggerrer.getTriggeringMechanics().get(triggerType).stream()
        .forEach(mechanicConfig ->
            EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                Optional.of(mechanicConfig), triggerrer.binder().getSide(), target)
      );
  }

  static void passiveTrigger(final Side side, final ConstTrigger triggerType) {
    List<Effect> useHeroPowerMechanics = side.board.stream()
        .sorted(EffectFactory.compareBySequenceId)
        .flatMap(minion -> minion.getTriggeringMechanics().get(triggerType).stream())
        .flatMap(mechanic -> EffectFactory.getMechanicEffects(mechanic, side).stream())
        .collect(Collectors.toList());

    side.getEffectQueue().enqueue(useHeroPowerMechanics);
  }

  static void passiveTrigger(final Mechanic.TriggeringMechanic triggerrer,
                             final ConstTrigger triggerType) {
    final Side side = triggerrer.binder().getSide();
    List<MechanicConfig> mechanicConfigs = triggerrer.getTriggeringMechanics().get(triggerType);
    triggerWithoutTarget(mechanicConfigs, side);
  }

  static void triggerWithoutTarget(final List<MechanicConfig> mechanicConfigs, final Side side) {
    mechanicConfigs.stream()
        .filter(mechanicConfig -> !mechanicConfig.triggerOnlyWithTarget)
        .forEach(effectConfig -> {
          try {
            TargetFactory.getProperTargets(effectConfig.target, side).stream()
                .forEach(
                    target -> EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                        Optional.of(effectConfig), side, target)
                );
          } catch (TargetFactory.NoTargetFoundException error) {
            EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                Optional.of(effectConfig), side);
          }
        });
  }

  static void triggerWithTarget() {

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
