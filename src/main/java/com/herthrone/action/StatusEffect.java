package com.herthrone.action;

import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.stats.BooleanAttribute;

/**
 * Created by yifeng on 4/15/16.
 */
public class StatusEffect implements Effect {

  private final BooleanAttribute booleanAttribute;
  private final double roundUntilExpire;

  public StatusEffect(final BooleanAttribute booleanAttribute, final double roundUntilExpire) {
    this.booleanAttribute = booleanAttribute;
    this.roundUntilExpire = roundUntilExpire;
  }

  @Override
  public ConstEffectType getEffectType() {
    return ConstEffectType.STATUS;
  }

  @Override
  public void act() {
    booleanAttribute.on(roundUntilExpire);
  }
}
