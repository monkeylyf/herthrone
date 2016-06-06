package com.herthrone.base;

import com.google.common.base.Optional;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.configuration.TargetConfig;

import java.util.List;

/**
 * Created by yifeng on 4/4/16.
 */
public interface Spell extends Card {

  Optional<TargetConfig> getTargetConfig();

  List<EffectConfig> getEffects();
}
