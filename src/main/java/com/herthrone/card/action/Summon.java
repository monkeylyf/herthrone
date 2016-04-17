package com.herthrone.card.action;

import com.herthrone.action.Action;
import com.herthrone.container.Board;

import java.util.List;

/**
 * Created by yifeng on 4/13/16.
 */
public class Summon implements Action {

  private final Board board;
  private final List<String> minionNames;

  public Summon(Board board, List<String> minionNames) {
    this.board = board;
    this.minionNames = minionNames;
  }

  @Override
  public void act() {
    for (String name : this.minionNames) {
      //Minion minion = MinionFactory.createMinionByName(name);
      this.board.addMinion(null);
    }
  }
}
