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

  /**
   * A minion actively to be played by a player onto a board.
   * Battlecry effects will be triggered by the action of play.
   *
   * @param board
   */
  void playOnBoard(final Container<Minion> board);

  /**
   * A minion passively to be put onto a board.
   *
   * @param board
   */
  void summonOnBoard(final Container<Minion> board);
}
