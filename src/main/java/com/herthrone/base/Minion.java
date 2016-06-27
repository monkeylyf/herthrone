package com.herthrone.base;

import com.herthrone.game.Container;
import com.herthrone.objects.EffectMechanics;

/**
 * Created by yifeng on 4/2/16.
 */


public interface Minion extends Creature {

  EffectMechanics getEffectMechanics();

  int getSequenceId();

  void setSequenceId(final int id);

  void silence();

  void destroy();

  void playOnBoard(final Container<Minion> board);
}
