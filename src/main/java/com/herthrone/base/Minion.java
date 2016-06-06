package com.herthrone.base;

import com.google.common.base.Optional;
import com.herthrone.configuration.EffectConfig;

/**
 * Created by yifeng on 4/2/16.
 */


public interface Minion extends Creature {

  Optional<EffectConfig> BattleCry();

  int getSequenceId();

  void setSequenceId(final int id);

  void silence();
}
