package com.herthrone.game;

import com.google.common.base.Preconditions;
import com.herthrone.base.Card;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by yifeng on 4/9/16.
 */
public class Container<T extends Card> implements Iterator<T> {

  static Logger logger = Logger.getLogger(Container.class.getName());

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
      logger.debug(String.format("Card %s not added", card.getCardName()));
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
    container.add(index, card);
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

  public int count(final T card) {
    int count = 0;
    final String cardName = card.getCardName();
    for (T existingCard : container) {
      if (existingCard.getCardName().equals(cardName)) {
        count += 1;
      }
    }
    return count;
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

  @Override
  public boolean hasNext() {
    return this.container.iterator().hasNext();
  }

  @Override
  public T next() {
    return this.container.iterator().next();
  }

  @Override
  public void remove() {
    this.container.iterator().remove();
  }

  @Override
  public void forEachRemaining(Consumer<? super T> action) {
    this.container.forEach(action);
  }
}
