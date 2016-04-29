package com.herthrone.stats;

import com.google.common.base.Preconditions;
import com.herthrone.base.BaseCard;

/**
 * Created by yifeng on 4/27/16.
 */
public class Crystal implements Round {

  private static final int MAX_CRYSTALS = 10;
  private int crystal;
  private int crystalUpperBound;

  public Crystal() {
    this.crystal = 1;
  }

  public boolean canPlay(final BaseCard card) {
    return this.crystal >= card.getCrystalManaCost().getVal();
  }

  public void consume(final int crystalCost) {
    Preconditions.checkArgument(this.crystal >= crystalCost);
    this.crystal -= crystalCost;
  }

  public void increaseUpperBound() {
    this.crystalUpperBound = Math.min(this.crystalUpperBound + 1, Crystal.MAX_CRYSTALS);
  }

  @Override
  public void nextRound() {
    increaseUpperBound();
    this.crystal = crystalUpperBound;
  }

}
