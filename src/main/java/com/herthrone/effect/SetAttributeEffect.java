package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.object.ValueAttribute;

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
