package com.herthrone.container;

import com.herthrone.base.BaseCard;
import com.herthrone.exception.CardNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yifeng on 4/2/16.
 */
public class Deck {

  private static final int deckSize = 30;
  private final List<BaseCard> deck;

  public Deck() {
    this.deck = new ArrayList<>();
  }
  public Deck(final List<BaseCard> cards) throws CardNotFoundException {
    assert(cards.size() == Deck.deckSize);
    this.deck = cards;
  }

  public void initDeck(final List<BaseCard> cards) throws CardNotFoundException {
    for (BaseCard card : cards) {
      this.deck.add(card);
    }
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
