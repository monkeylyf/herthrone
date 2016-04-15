package com.herthrone.base;

import com.herthrone.container.Board;
import com.herthrone.container.Deck;
import com.herthrone.container.Hand;

/**
 * Created by yifeng on 4/2/16.
 */
public class Battlefield {

  private final Side mySide;
  private final Side opponentSide;

  public Battlefield(Hero hero1, Hero hero2, Hand hand1, Hand hand2, Deck deck1, Deck deck2, Board board1, Board board2) {
    this.mySide = new Side(hero1, hand1, deck1, board1);
    this.opponentSide = new Side(hero2, hand2, deck2, board2);
  }

  public Side getMySide() { return this.mySide; }
  public Side getOpponentSide() { return this.opponentSide; }

}


