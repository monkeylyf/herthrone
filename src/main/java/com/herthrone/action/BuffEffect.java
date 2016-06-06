package com.herthrone.action;

import com.herthrone.base.Effect;
import com.herthrone.stats.IntAttribute;

/**
 * Created by yifeng on 4/28/16.
 */
public class BuffEffect implements Effect {

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