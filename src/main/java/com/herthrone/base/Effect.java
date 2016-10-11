package com.herthrone.base;

import com.herthrone.constant.ConstEffectType;

public interface Effect {

  ConstEffectType effectType();

  void act();

  //public boolean actionable(Card card);
}
