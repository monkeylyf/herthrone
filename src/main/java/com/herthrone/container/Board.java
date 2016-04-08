package com.herthrone.container;

import com.herthrone.base.BaseCreature;
import com.herthrone.base.BaseMinion;
import com.herthrone.base.Secret;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by yifeng on 4/2/16.
 */
public class Board {

  private static final int maxCapacity = 7;

  private final List<BaseMinion> board;
  private final List<Secret> secrets;

  public Board() {
    this.board = new ArrayList<BaseMinion>();
    this.secrets = new ArrayList<Secret>();
  }

  public boolean isFull() {
    return this.board.size() == Board.maxCapacity;
  }

  public void addMinion(final BaseMinion minion) {
    if (!isFull()) {
      this.board.add(minion);
    }
  }

  public void addMinion(final BaseMinion minion, final int index) {
    if (!isFull()) {
      this.board.add(index, minion);
    }
  }

  public void removeDead() {
    for (Iterator<BaseMinion> iterator = this.board.iterator(); iterator.hasNext();) {
      BaseMinion minion = iterator.next();
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

  public boolean attackable(BaseCreature attackee) {
    // Check whether there is taunt on board. If there is, the attackee must be one of the taunt otherwise it's not attackable.
    return true;
  }

  public Stream stream() {
    return this.board.stream();
  }
}
