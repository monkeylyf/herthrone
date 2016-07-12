package com.herthrone.factory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.herthrone.base.Creature;
import com.herthrone.base.Mechanic;
import com.herthrone.base.Minion;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.game.Side;
import com.herthrone.helper.RandomMinionGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by yifengliu on 7/10/16.
 */
public class TriggerFactory {

  static void activeTrigger(final Mechanic.TriggeringMechanic triggerrer,
                            final ConstTrigger triggerType, final Creature target) {
    triggerrer.getTriggeringMechanics().get(triggerType).stream()
        .forEach(mechanicConfig ->
            EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                Optional.of(mechanicConfig), triggerrer.binder().getSide(), target)
      );
  }

  static void passiveTrigger(final Mechanic.TriggeringMechanic listener, final ConstTrigger trigger) {
    final Side side = listener.binder().getSide();
    listener.getTriggeringMechanics().get(trigger).stream()
        .filter(mechanicConfig -> !mechanicConfig.triggerOnlyWithTarget)
        .forEach(mechanicConfig -> {
          Preconditions.checkArgument(mechanicConfig.effect.isPresent());
          final EffectConfig effectConfig = mechanicConfig.effect.get();
          List<Creature> targets;
          try {
            targets = TargetFactory.getProperTargets(effectConfig.target, side);
          } catch (TargetFactory.NoTargetFoundException error) {
            targets = (listener instanceof Minion) ?
                Arrays.asList((Minion) listener) : Collections.emptyList();
          }
          targets = effectConfig.isRandom ?
              Arrays.asList(RandomMinionGenerator.randomOne(targets)) : targets;
          targets.stream().forEach(
              realTarget -> EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                  Optional.of(mechanicConfig), side, realTarget)
          );
        });
  }
}
