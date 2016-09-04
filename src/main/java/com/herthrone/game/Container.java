package com.herthrone.game;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.herthrone.base.Card;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Container<T extends Card> {

  private static Logger logger = Logger.getLogger(Container.class.getName());

  private final int maxCapacity;
  private final List<T> container;

  public Container(final int maxCapacity) {
    this(new ArrayList<T>(maxCapacity), maxCapacity);
  }

  public Container(final List<T> container, final int maxCapacity) {
    Preconditions.checkArgument(
        container.size() <= maxCapacity, "Container size larger that max capacity: " + maxCapacity);
    this.container = container;
    this.maxCapacity = maxCapacity;
  }

  public Container() {
    this.container = new ArrayList<>();
    this.maxCapacity = Integer.MAX_VALUE;
  }

  public boolean isEmpty() {
    return container.isEmpty();
  }

  public boolean add(final T card) {
    final boolean willCardBeAdded = !isFull();
    if (willCardBeAdded) {
      container.add(card);
    } else {
      logger.debug(String.format("Container is already full with size %d", container.size()));
      logger.debug(String.format("Card %s not added", card.cardName()));
    }
    return willCardBeAdded;
  }

  public boolean isFull() {
    return container.size() == maxCapacity;
  }

  public void addToRandomPos(final T card) {
    final Random random = new Random();
    final int index = random.nextInt(container.size() + 1);
    add(index, card);
  }

  public void add(final int index, final T card) {
    // index can be out of boundary, e.g., when you play a minion to index 1 but this minion also
    // returns minion at index 0 to hand.

    container.add(Math.min(index, size()), card);
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

  public Stream<T> stream() {
    return container.stream();
  }

  public List<T> asList() {
    return container;
  }

  public boolean contains(final T card) {
    return container.stream().anyMatch(c -> c == card);
  }

  public int size() {
    return container.size();
  }

  public T remove(final int index) {
    return container.remove(index);
  }

  public void remove(final T card) {
    final boolean foundAndRemoved = container.remove(card);
    Preconditions.checkArgument(foundAndRemoved);
  }

  public int indexOf(final T card) {
    return container.indexOf(card);
  }

  public boolean isAdjacent(final T card1, final T card2) {
    final int index1 = indexOf(card1);
    final int index2 = indexOf(card2);
    return Math.abs(index1 - index2) == 1;
  }

  @Override
  public String toString() {
    final Objects.ToStringHelper stringHelper = Objects.toStringHelper(this);
    for (int i = 0; i < container.size(); ++i) {
      stringHelper.add(Integer.toString(i), container.get(i).cardName());
    }
    return stringHelper.toString();
  }
}
