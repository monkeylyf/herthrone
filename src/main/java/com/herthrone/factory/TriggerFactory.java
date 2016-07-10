package com.herthrone.factory;

import com.google.common.base.Optional;
import com.herthrone.base.Creature;
import com.herthrone.base.Mechanic;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.game.Side;
import com.herthrone.helper.RandomMinionGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yifengliu on 7/10/16.
 */
public class TriggerFactory {

  static void activeTrigger(final Mechanic.TriggeringMechanic listener, final ConstTrigger trigger,
                            final Side side, final Creature caster, final Creature target) {
    listener.getTriggeringMechanics().get(trigger).stream()
        .forEach(mechanicConfig ->
            EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                Optional.of(mechanicConfig), side, caster, target)
      );
  }

  static void activeTrigger(final Mechanic.TriggeringMechanic listener, final ConstTrigger trigger,
                            final Side side, final Creature caster) {
    activeTrigger(listener, trigger, side, caster, caster);
  }

  static void trigger(final Mechanic.TriggeringMechanic listener, final ConstTrigger trigger,
                      final Side side, final Creature caster, final Creature target) {
    listener.getTriggeringMechanics().get(trigger).stream()
        .filter(mechanicConfig -> !mechanicConfig.triggerOnlyWithTarget)
        .forEach(mechanic -> {
          final EffectConfig effectConfig = mechanic.effect.get();
          List<Creature> targets = TargetFactory.getProperTargets(
              effectConfig.target, side, target);
          targets = effectConfig.isRandom ?
              Arrays.asList(RandomMinionGenerator.randomOne(targets)) : targets;
          targets.stream().forEach(
              realTarget -> EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                  Optional.of(mechanic), side, caster, realTarget)
          );
        });
  }

  static void trigger(final Mechanic.TriggeringMechanic listener, final ConstTrigger trigger,
                      final Side side, final Creature caster) {
    trigger(listener, trigger, side, caster, caster);
  }

  static void passiveTrigger() {

  }
}
