package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.object.Value;
import com.herthrone.object.ValueAttribute;

public class BuffEffect implements Effect {

  private final ValueAttribute attr;
  private final int buffDelta;
  private final boolean permanent;

  public BuffEffect(final ValueAttribute attr, final int buffDelta, final boolean permanent) {
    this.attr = attr;
    this.buffDelta = buffDelta;
    this.permanent = permanent;
  }

  public BuffEffect(final ValueAttribute attr, final int setToValue) {
    this.attr = attr;
    this.buffDelta = setToValue - attr.value();
    this.permanent = true;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.BUFF;
  }

  @Override
  public void act() {
    final Value value = permanent ? attr.getPermanentBuff() : attr.getTemporaryBuff();
    value.increase(buffDelta);
  }
}