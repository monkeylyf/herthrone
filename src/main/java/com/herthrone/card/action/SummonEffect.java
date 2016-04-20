package com.herthrone.card.action;

import com.herthrone.base.Minion;
import com.herthrone.card.factory.Action;
import com.herthrone.container.Board;
import com.herthrone.container.Container;

import java.util.List;

/**
 * Created by yifeng on 4/13/16.
 */
public class SummonEffect implements Action {

  private final Container<Minion> board;
  private final List<Minion> minions;

  public SummonEffect(final Container<Minion> board, final List<Minion> minions) {
    this.board = board;
    this.minions = minions;
  }

  @Override
  public void act() {
    for (Minion minion : this.minions) {
      this.board.add(minion);
    }
  }
}
