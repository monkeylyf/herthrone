package com.herthrone.factory;

import com.herthrone.base.Effect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifengliu on 5/17/16.
 */
public class EffectPack implements Effect {

  private final List<Effect> effects;

  public EffectPack(final List<Effect> effects) {
    this.effects = effects;
  }

  public EffectPack() {
    this(new ArrayList<>());
  }

  @Override
  public void act() {
    effects.stream().forEach(Effect::act);
  }

}
