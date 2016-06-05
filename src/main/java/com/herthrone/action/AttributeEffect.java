package com.herthrone.action;

import com.herthrone.factory.Action;
import com.herthrone.stats.IntAttribute;

/**
 * Created by yifeng on 4/14/16.
 */
public class AttributeEffect implements Action {

  private final IntAttribute attr;
  private final int delta;
  private final boolean permanent;

  public AttributeEffect(final IntAttribute attr, final int delta, final boolean permanent) {
    this.attr = attr;
    this.delta = delta;
    this.permanent = permanent;
  }

  @Override
  public void act() {
    attr.increase(delta);
  }
}
