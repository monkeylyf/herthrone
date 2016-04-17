package com.herthrone.base;

import com.herthrone.container.Board;
import com.herthrone.container.Container;

/**
 * Created by yifeng on 4/2/16.
 */
public class Battlefield {

  private final Side mySide;
  private final Side opponentSide;

  public Battlefield(Hero hero1, Hero hero2, Container<BaseCard> hand1, Container<BaseCard> hand2, Container<BaseCard> deck1, Container<BaseCard> deck2, Board board1, Board board2) {
    this.mySide = new Side(hero1, hand1, deck1, board1);
    this.opponentSide = new Side(hero2, hand2, deck2, board2);
  }

  public Side getMySide() { return this.mySide; }
  public Side getOpponentSide() { return this.opponentSide; }

}


