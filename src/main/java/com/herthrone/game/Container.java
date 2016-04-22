package com.herthrone.game;

import com.google.common.base.Preconditions;
import com.herthrone.base.BaseCard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by yifeng on 4/9/16.
 */
public class Container <T extends BaseCard> {

  private final int maxCapacity;
  private final List<T> container;

  public Container(final List<T> container, final int maxCapacity) {
    Preconditions.checkArgument(container.size() <= maxCapacity, "Container size larger that max capacity: " + maxCapacity);
    this.container = container;
    this.maxCapacity = maxCapacity;
  }

  public Container(final int maxCapacity) {
    this(new ArrayList<T>(maxCapacity), maxCapacity);
  }

  public Container() {
    this.maxCapacity = Integer.MAX_VALUE;
    this.container = new ArrayList<>();
  }

  public int getMaxCapacity() { return this.maxCapacity; }
  public boolean isFull() {return this.container.size() == this.maxCapacity; }

  public void add(final T card) {
    this.container.add(card);
  }

  public void add(final int index, final T card) {
    this.container.add(index, card);
  }

  public void addToRandomPos(T card) {
    final Random random = new Random();
    final int index = random.nextInt(this.container.size() + 1);
    this.add(index, card);
  }

  public void shuffle() {
    // TODO: shuffle the deck.
  }

  public T top() { return this.container.remove(this.container.size() - 1); }
  public T get(final int index) { return this.container.get(index); }

  public T random() {
    final Random random = new Random();
    final int index = random.nextInt(this.container.size());
    return this.container.remove(index);
  }

  public Iterator<T> iterator() { return this.container.iterator(); }
  public Stream<T> stream() {
    return this.container.stream();
  }

  public int count(T card) {
    int count = 0;
    final String cardName = card.getCardName();
    for (T existingCard : this.container) {
      if (existingCard.getCardName().equals(cardName)) {
        count += 1;
      }
    }
    return count;
  }
}
