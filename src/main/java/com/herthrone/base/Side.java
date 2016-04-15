package com.herthrone.base;

import com.herthrone.container.Board;
import com.herthrone.container.Deck;
import com.herthrone.container.Hand;

/**
 * Created by yifeng on 4/14/16.
 */
public class Side {
  private final Hero hero;
  private final Hand hand;
  private final Deck deck;
  private final Board board;

  public Side(Hero hero, Hand hand, Deck deck, Board board) {
    this.hero = hero;
    this.hand = hand;
    this.deck = deck;
    this.board = board;
  }

  public Hero getHero() { return this.hero; }
  public Hand getHand() { return this.hand; }
  public Deck getDeck() { return this.deck; }
  public Board getBoard() { return this.board; }
}
