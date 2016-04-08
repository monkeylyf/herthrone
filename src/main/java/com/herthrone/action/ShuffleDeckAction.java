package com.herthrone.action;

import com.herthrone.base.BaseCard;
import com.herthrone.container.Deck;

/**
 * Created by yifeng on 4/5/16.
 */
public class ShuffleDeckAction implements Action {

  private final Deck deck;
  private final BaseCard baseCard;

  public ShuffleDeckAction(final Deck deck, final BaseCard baseCard) {
    this.deck = deck;
    this.baseCard = baseCard;
  }

  @Override
  public void act() {
    this.deck.randomShuffle(this.baseCard);
  }
}
