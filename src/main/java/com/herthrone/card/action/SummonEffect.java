package com.herthrone.card.action;

import com.herthrone.base.Minion;
import com.herthrone.card.factory.Action;
import com.herthrone.game.Container;


/**
 * Created by yifeng on 4/13/16.
 */
public class SummonEffect implements Action {

  private final Container<Minion> board;
  private final Minion minion;

  public SummonEffect(final Container<Minion> board, final Minion minion) {
    this.board = board;
    this.minion = minion;
  }

  @Override
  public void act() {
    board.add(minion);
  }
}
