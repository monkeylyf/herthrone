package com.herthrone.action;

import com.herthrone.factory.Action;
import com.herthrone.stats.IntAttribute;

/**
 * Created by yifeng on 4/28/16.
 */
public class BuffEffect implements Action {

  private final IntAttribute attr;
  private final int buffDelta;

  public BuffEffect(final IntAttribute attr, final int buffDelta) {
    this.attr = attr;
    this.buffDelta = buffDelta;
  }

  @Override
  public void act() {

  }
}