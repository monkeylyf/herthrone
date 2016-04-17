package com.herthrone.container;

import com.herthrone.base.Minion;
import com.herthrone.base.Secret;
import com.herthrone.configuration.ConfigLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by yifeng on 4/2/16.
 */
public class Board {

  private final Container<Minion> board;
  private final Container<Secret> secrets;

  public Board() {
    final int boardCapacity = Integer.parseInt(ConfigLoader.getResource().getString("board_max_capacity"));
    this.board = new Container<>(boardCapacity);
    this.secrets = new Container<>();
  }

  public boolean isFull() {
    return this.board.isFull();
  }

  public void addMinion(final Minion minion) {
    if (!isFull()) {
      this.board.add(minion);
    }
  }

  public void addMinion(final Minion minion, final int index) {
    if (!isFull()) {
      this.board.add(index, minion);
    }
  }

  public Minion getMinion(final int index) {
    return this.board.get(index);
  }

  public void removeDead() {
    for (Iterator<Minion> iterator = this.board.iterator(); iterator.hasNext();) {
      Minion minion = iterator.next();
      if (minion.getHealthAttr().getVal() <= 0) {
        iterator.remove();
      }
    }
  }

  public boolean hasSecret(final Secret secret) {
    Class<? extends Secret> clz = secret.getClass();
    return this.secrets.stream().anyMatch(s -> s.getClass().equals(clz));
  }

  public void addSecret(Secret secret) {
    if (hasSecret(secret)) {
      throw new IllegalArgumentException("Cannot put existing secret on board!");
    }
    this.addSecret(secret);
  }

  public boolean attackable(Minion attackee) {
    // Check whether there is taunt on board. If there is, the attackee must be one of the taunt otherwise it's not attackable.
    return true;
  }

  public Stream<Minion> stream() {
    return this.board.stream();
  }
}
