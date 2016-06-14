package com.herthrone.base;

import com.google.common.base.Optional;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.stats.EffectMechanics;

/**
 * Created by yifeng on 4/2/16.
 */


public interface Minion extends Creature {

  EffectMechanics getEffectMechanics();

  int getSequenceId();

  void setSequenceId(final int id);

  void silence();
}
