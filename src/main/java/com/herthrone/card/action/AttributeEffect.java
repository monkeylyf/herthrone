package com.herthrone.card.action;

import com.herthrone.card.factory.Action;
import com.herthrone.stats.Attribute;

/**
 * Created by yifeng on 4/14/16.
 */
public class AttributeEffect implements Action {

  private final Attribute attr;
  private final int delta;
  private final int duration;

  public AttributeEffect(final Attribute attr, final int delta, final int duration) {
    this.attr = attr;
    this.delta = delta;
    this.duration = duration;
  }

  @Override
  public void act() {
    this.attr.increase(delta);
    this.attr.setDuration(this.duration);
  }
}
