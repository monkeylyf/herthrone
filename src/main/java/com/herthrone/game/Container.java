package com.herthrone.game;

import com.google.common.base.Preconditions;
import com.herthrone.base.BaseCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by yifeng on 4/9/16.
 */
public class Container<T extends BaseCard> {

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

  public int getMaxCapacity() {
    return maxCapacity;
  }

  public boolean isFull() {
    return container.size() == maxCapacity;
  }

  public boolean isEmpty() {
    return container.isEmpty();
  }

  public void add(final T card) {
    if (isFull()) {
      // TODO: logger needed here.
    } else {
      container.add(card);
    }
  }

  public void add(final int index, final T card) {
    container.add(index, card);
  }

  public void addToRandomPos(T card) {
    final Random random = new Random();
    final int index = random.nextInt(container.size() + 1);
    add(index, card);
  }

  public void shuffle() {
    Collections.shuffle(container);
  }

  public T top() {
    return container.remove(container.size() - 1);
  }

  public T get(final int index) {
    return container.get(index);
  }

  public T random() {
    final Random random = new Random();
    final int index = random.nextInt(container.size());
    return container.remove(index);
  }

  public Iterator<T> iterator() {
    return container.iterator();
  }

  public Stream<T> stream() {
    return container.stream();
  }

  public int count(T card) {
    int count = 0;
    final String cardName = card.getCardName();
    for (T existingCard : container) {
      if (existingCard.getCardName().equals(cardName)) {
        count += 1;
      }
    }
    return count;
  }

  public int size() {
    return container.size();
  }

  public T remove(final int index) {
    return container.remove(index);
  }
}
