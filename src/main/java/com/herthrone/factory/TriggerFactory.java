package com.herthrone.factory;

import com.google.common.base.Optional;
import com.herthrone.base.Creature;
import com.herthrone.base.Mechanic;
import com.herthrone.base.Minion;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.game.Side;
import com.herthrone.helper.RandomMinionGenerator;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

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

  static void passiveTrigger(final Mechanic.TriggeringMechanic triggerrer,
                             final ConstTrigger triggerType) {
    final Side side = triggerrer.binder().getSide();
    triggerrer.getTriggeringMechanics().get(triggerType).stream()
        .filter(mechanicConfig -> !mechanicConfig.triggerOnlyWithTarget)
        .forEach(effectConfig -> {
          List<Creature> targets;
          try {
            targets = TargetFactory.getProperTargets(effectConfig.target, side);
          } catch (TargetFactory.NoTargetFoundException error) {
            targets = (triggerrer instanceof Minion) ?
                Collections.singletonList((Minion) triggerrer) : Collections.emptyList();
          }
          targets = effectConfig.isRandom ?
              Collections.singletonList(RandomMinionGenerator.randomOne(targets)) : targets;
          logger.debug("Total " + targets.size() + " passive targets for " + effectConfig);
          targets.stream().forEach(
              realTarget -> EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                  Optional.of(effectConfig), side, realTarget)
          );
        });
  }
}
