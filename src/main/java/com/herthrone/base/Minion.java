package com.herthrone.base;

/**
 * Created by yifeng on 4/2/16.
 */


public interface Minion extends Creature {

  void BattleCry();

  void setSequenceId(final int id);

  int getSequenceId();
}
