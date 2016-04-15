package com.herthrone.card.action;

import com.herthrone.action.Action;
import com.herthrone.container.Deck;
import com.herthrone.container.Hand;

/**
 * Created by yifeng on 4/13/16.
 */
public class DrawCard implements Action {

  private final Hand hand;
  private final Deck deck;

  public DrawCard(Hand hand, Deck deck) {
    this.hand = hand;
    this.deck = deck;
  }

  @Override
  public void act() {
    this.hand.add(this.deck.topCard());
  }
}
