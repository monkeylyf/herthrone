package com.herthrone.action;

import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.stats.IntAttribute;
import com.herthrone.stats.Value;

/**
 * Created by yifeng on 4/28/16.
 */
public class BuffEffect implements Effect {

  private final IntAttribute attr;
  private final int buffDelta;
  private final boolean permanent;

  public BuffEffect(final IntAttribute attr, final int buffDelta, final boolean permanent) {
    this.attr = attr;
    this.buffDelta = buffDelta;
    this.permanent = permanent;
  }

  public BuffEffect(final IntAttribute attr, final int setToValue) {
    this.attr = attr;
    this.buffDelta = setToValue - attr.getVal();
    this.permanent = true;
  }

  @Override
  public ConstEffectType getEffectType() {
    return ConstEffectType.BUFF;
  }

  @Override
  public void act() {
    final Value value = permanent ? attr.buff.perm : attr.buff.temp;
    value.increase(buffDelta);
  }
}