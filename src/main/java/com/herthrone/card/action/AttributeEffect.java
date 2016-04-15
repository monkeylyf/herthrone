package com.herthrone.card.action;

import com.herthrone.action.Action;
import com.herthrone.base.Attribute;

/**
 * Created by yifeng on 4/14/16.
 */
public class AttributeEffect implements Action {

  private final Attribute attr;
  private final int delta;

  public AttributeEffect(final Attribute attr, final int delta) {
    this.attr = attr;
    this.delta = delta;
  }

  @Override
  public void act() {
    this.attr.vary(delta);
  }
}
