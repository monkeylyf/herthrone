package com.herthrone.game;

import com.google.common.annotations.VisibleForTesting;
import com.herthrone.base.Effect;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by yifengliu on 6/5/16.
 */
public class ActionQueue {

  static Logger logger = Logger.getLogger(ActionQueue.class.getName());

  private final Queue<Effect> queue;

  @VisibleForTesting
  ActionQueue() {
    this.queue = new LinkedList<>();
  }

  public void enqueue(final Effect effect) {
    queue.add(effect);
    executeUntilEmpty();
  }

  private void executeUntilEmpty() {
    while (!queue.isEmpty()) {
      final Effect effect = queue.remove();
      effect.act();
    }
  }
}
