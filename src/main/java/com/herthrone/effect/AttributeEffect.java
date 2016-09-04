package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.object.ValueAttribute;

public class AttributeEffect implements Effect {

  private final ValueAttribute attr;
  private final int delta;
  private final boolean permanent;

  public AttributeEffect(final ValueAttribute attr, final int delta, final boolean permanent) {
    this.attr = attr;
    this.delta = delta;
    this.permanent = permanent;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.ATTRIBUTE;
  }

  @Override
  public void act() {
    attr.increase(delta);
  }
}
