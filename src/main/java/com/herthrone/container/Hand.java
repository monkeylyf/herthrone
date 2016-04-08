package com.herthrone.container;

import com.herthrone.base.BaseCard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by yifeng on 4/2/16.
 */
public class Hand {

  private static final int maxCapacity = 10;

  private final List<BaseCard> hand;

  public Hand() {
    this.hand = new ArrayList<BaseCard>(Hand.maxCapacity);
  }

  public void add(BaseCard baseCard) {
    if (this.hand.size() == Hand.maxCapacity) {
      // Logger it.
    } else {
      this.hand.add(baseCard);
    }
  }

  public BaseCard select(final int index) {
    return this.hand.remove(index);
  }

  public Stream<BaseCard> stream() {
    return this.hand.stream();
  }

  public boolean isFull() {
    return this.hand.size() == Hand.maxCapacity;
  }
}