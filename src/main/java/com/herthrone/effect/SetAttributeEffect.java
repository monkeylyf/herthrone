package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.object.ValueAttribute;

/**
 * Created by yifengliu on 7/19/16.
 */
public class SetAttributeEffect implements Effect {

  private final ValueAttribute valueAttribute;
  private final int valueToSet;

  public SetAttributeEffect(final ValueAttribute valueAttribute, final int valueToSet) {
    this.valueAttribute = valueAttribute;
    this.valueToSet = valueToSet;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.SET;
  }

  @Override
  public void act() {
    valueAttribute.set(valueToSet);
  }
}
