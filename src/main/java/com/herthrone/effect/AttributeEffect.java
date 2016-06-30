package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.objects.IntAttribute;

/**
 * Created by yifeng on 4/14/16.
 */
public class AttributeEffect implements Effect {

  private final IntAttribute attr;
  private final int delta;
  private final boolean permanent;

  public AttributeEffect(final IntAttribute attr, final int delta, final boolean permanent) {
    this.attr = attr;
    this.delta = delta;
    this.permanent = permanent;
  }

  @Override
  public ConstEffectType getEffectType() {
    return ConstEffectType.ATTRIBUTE;
  }

  @Override
  public void act() {
    attr.increase(delta);
  }
}
