package com.herthrone.game;

import com.google.common.annotations.VisibleForTesting;
import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.effect.HealEffect;
import com.herthrone.factory.TriggerFactory;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class EffectQueue {

  private static Logger logger = Logger.getLogger(EffectQueue.class.getName());

  private final Queue<Effect> queue;

  @VisibleForTesting
  EffectQueue() {
    this.queue = new LinkedList<>();
  }

  private void executeUntilEmpty() {
    while (!queue.isEmpty()) {
      final Effect effect = queue.remove();
      logger.debug("Acting effect " + effect.effectType());
      effect.act();

      triggerOnHealMinion(effect);
    }
  }

  public void enqueue(final List<Effect> effects) {
    if (effects.size() > 0) {
      logger.debug(String.format("Enqueuing %d effects", effects.size()));
      effects.forEach(effect -> {
        logger.debug("Enqueuing effect: " + effect.effectType());
        queue.add(effect);
      });
      executeUntilEmpty();
    }
  }

  private void triggerOnHealMinion(final Effect effect) {
    if (effect instanceof HealEffect && ((HealEffect) effect).getTarget() instanceof Minion) {
      final Side side = ((HealEffect) effect).getTarget().binder().getSide();
      TriggerFactory.triggerByBoard(side, ConstTrigger.ON_HEAL_MINION);
    }
  }

}
