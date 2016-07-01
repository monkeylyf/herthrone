package com.herthrone.base;

import com.herthrone.constant.ConstEffectType;

/**
 * Created by yifeng on 4/5/16.
 */
public interface Effect {

  ConstEffectType effectType();

  void act();

  //public boolean actionable(Card card);
}
