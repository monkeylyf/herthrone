package com.herthrone.base;

import com.herthrone.configuration.EffectConfig;

import java.util.List;

/**
 * Created by yifeng on 4/4/16.
 */
public interface Spell extends BaseCard {

  List<EffectConfig> getEffects();
}
