package com.herthrone.effect;

import com.herthrone.base.Destroyable;
import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;

/**
 * Created by yifengliu on 7/5/16.
 */
public class DestroyEffect implements Effect {

  private final Destroyable destroyableItem;

  public DestroyEffect(final Destroyable destroyableItem) {
    this.destroyableItem = destroyableItem;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.DESTROY;
  }

  @Override
  public void act() {
    destroyableItem.destroy();
  }
}
