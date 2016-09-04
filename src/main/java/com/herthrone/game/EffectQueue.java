package com.herthrone.game;

import com.herthrone.base.Effect;
import com.herthrone.factory.TriggerFactory;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class EffectQueue {

  private static Logger logger = Logger.getLogger(EffectQueue.class.getName());

  private final Queue<Effect> primaryQueue;

  EffectQueue() {
    this.primaryQueue = new LinkedList<>();
  }

  private void executeUntilEmpty() {
    while (!primaryQueue.isEmpty()) {
      final Effect effect = primaryQueue.remove();
      logger.debug("Acting effect " + effect.effectType());
      effect.act();

      TriggerFactory.triggerOnHealMinion(effect);
    }
  }

  public void enqueue(final List<Effect> effects) {
    if (effects.size() > 0) {
      logger.debug(String.format("Enqueuing %d effects", effects.size()));
      effects.forEach(effect -> {
        logger.debug("Enqueuing effect: " + effect.effectType());
        primaryQueue.add(effect);
      });
      executeUntilEmpty();
    }
  }
}
