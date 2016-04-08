package com.herthrone.container;

import com.herthrone.base.BaseCard;

import java.util.List;
import java.util.Random;

/**
 * Created by yifeng on 4/2/16.
 */
public class Deck {

  private static final int deckSize = 30;
  private final List<BaseCard> deck;

  public Deck(final List<BaseCard> deck) {
    assert(deck.size() == Deck.deckSize);
    this.deck = deck;
  }

  public BaseCard topCard() {
    BaseCard top = this.deck.remove(this.deck.size() - 1);
    return top;
  }

  public BaseCard randomCard() {
    final Random rand = new Random();
    final int index = rand.nextInt(this.deck.size());
    BaseCard baseCard = this.deck.remove(index);
    return baseCard;
  }

  public void randomShuffle(final BaseCard baseCard) {
    final Random rand = new Random();
    final int index = rand.nextInt(this.deck.size() + 1);
    this.deck.add(index, baseCard);
  }
}
