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
  private int overloaded;
  private int lockedCrystal;

  public Crystal() {
    this.crystal = 1;
    this.overloaded = 0;
    this.lockedCrystal = 0;
  }

  public boolean canPlay(final BaseCard card) {
    return crystal >= card.getCrystalManaCost().getVal();
  }

  public void consume(final int crystalCost) {
    Preconditions.checkArgument(crystal >= crystalCost);
    crystal -= crystalCost;
  }

  public void increaseUpperBound() {
    crystalUpperBound = Math.min(crystalUpperBound + 1, Crystal.MAX_CRYSTALS);
  }

  public int getCrystal() {
    return crystal;
  }

  public int getCrystalUpperBound() {
    return crystalUpperBound;
  }

  public int getLockedCrystal() {
    return lockedCrystal;
  }

  @Override
  public void nextRound() {
    increaseUpperBound();
    crystal = crystalUpperBound;
    applyOverload();
  }

  public void overload(final int val) {
    Preconditions.checkArgument(val > 0, "Overload must be positive, not " + val);
    overloaded += val;
  }

  private void applyOverload() {
    crystal -= overloaded;
    lockedCrystal = overloaded;
    overloaded = 0;
  }
}
