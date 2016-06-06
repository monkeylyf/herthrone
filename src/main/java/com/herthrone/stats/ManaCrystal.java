package com.herthrone.stats;

import com.google.common.base.Preconditions;
import com.herthrone.base.Card;
import com.herthrone.base.Round;

/**
 * Created by yifeng on 4/27/16.
 */
public class ManaCrystal implements Round {

  private static final int MAX_CRYSTALS = 10;
  private int crystal;
  private int crystalUpperBound;
  private int overloaded;
  private int lockedCrystal;

  public ManaCrystal() {
    this.crystal = 1;
    this.overloaded = 0;
    this.lockedCrystal = 0;
  }

  public boolean canPlay(final Card card) {
    return crystal >= card.getCrystalManaCost().getVal();
  }

  public void consume(final int crystalCost) {
    Preconditions.checkArgument(crystal >= crystalCost, "Not enough mana");
    crystal -= crystalCost;
  }

  public void increaseUpperBound() {
    crystalUpperBound = Math.min(crystalUpperBound + 1, ManaCrystal.MAX_CRYSTALS);
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
  public void endTurn() {
    increaseUpperBound();
    crystal = crystalUpperBound;
    applyOverload();
  }

  @Override
  public void startTurn() {

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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getCrystal());
    sb.append("/");
    sb.append(getCrystalUpperBound());
    if (getLockedCrystal() > 0) {
      sb.append("/");
      sb.append(getLockedCrystal());
    }

    return sb.toString();
  }
}
