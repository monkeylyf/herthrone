package com.herthrone.base;

import com.herthrone.container.Board;
import com.herthrone.container.Container;

/**
 * Created by yifeng on 4/14/16.
 */
public class Side {
  private final Hero hero;
  private final Container<BaseCard> hand;
  private final Container<BaseCard> deck;
  private final Board board;

  public Side(Hero hero, Container<BaseCard> hand, Container<BaseCard> deck, Board board) {
    this.hero = hero;
    this.hand = hand;
    this.deck = deck;
    this.board = board;
  }

  public Hero getHero() { return this.hero; }
  public Container<BaseCard> getHand() { return this.hand; }
  public Container<BaseCard> getDeck() { return this.deck; }
  public Board getBoard() { return this.board; }
}
