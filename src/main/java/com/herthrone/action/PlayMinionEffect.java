package com.herthrone.action;

import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.game.Container;

/**
 * Created by yifengliu on 5/25/16.
 */
public class PlayMinionEffect implements Effect {

  private final Minion minion;
  private final Container<Minion> board;

  public PlayMinionEffect(final Minion minion, final Container<Minion> board) {
    this.minion = minion;
    this.board = board;
  }

  @Override
  public void act() {
    board.add(minion);
  }
}
