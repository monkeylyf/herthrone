package com.herthrone.game;

import com.google.common.annotations.VisibleForTesting;
import com.herthrone.base.Effect;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by yifengliu on 6/5/16.
 */
public class EffectQueue {

  static Logger logger = Logger.getLogger(EffectQueue.class.getName());

  private final Queue<Effect> queue;

  @VisibleForTesting
  EffectQueue() {
    this.queue = new LinkedList<>();
  }

  public void enqueue(final Effect effect) {
    logger.debug("Enqueuing effect: " + effect.getEffectType().toString());
    queue.add(effect);
    executeUntilEmpty();
  }

  private void executeUntilEmpty() {
    while (!queue.isEmpty()) {
      final Effect effect = queue.remove();
      effect.act();
    }
  }

  public void enqueue(final List<Effect> effects) {
    logger.debug(String.format("Enqueuing %d effects", effects.size()));
    effects.stream().forEach(e -> {
      logger.debug("Enqueuing effect: " + e.getEffectType().toString());
      queue.add(e);
    });
    executeUntilEmpty();
  }
}
