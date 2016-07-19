package com.herthrone.object;

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
    this.crystal = 0;
    this.overloaded = 0;
    this.lockedCrystal = 0;
    this.crystalUpperBound = 0;
  }

  public boolean canPlay(final Card card) {
    return crystal >= card.manaCost().value();
  }

  public void consume(final int crystalCost) {
    Preconditions.checkArgument(crystal >= crystalCost, "Not enough mana");
    crystal -= crystalCost;
  }

  @Override
  public void endTurn() {
  }

  @Override
  public void startTurn() {
    increaseUpperBound();
    crystal = crystalUpperBound;
    applyOverload();
  }

  private void applyOverload() {
    crystal -= overloaded;
    lockedCrystal = overloaded;
    overloaded = 0;
  }

  public void increase(final int gain) {
    Preconditions.checkArgument(gain > 0, "Mana crystal gain must be positive");
    crystal += gain;
  }

  public void increaseUpperBound() {
    crystalUpperBound = Math.min(crystalUpperBound + 1, ManaCrystal.MAX_CRYSTALS);
  }

  public void increaseUpperBound(final int incrementValue) {
    crystalUpperBound = Math.min(crystalUpperBound + incrementValue, ManaCrystal.MAX_CRYSTALS);
  }

  public void overload(final int val) {
    Preconditions.checkArgument(val > 0, "Overload must be positive, not " + val);
    overloaded += val;
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

  public int getCrystal() {
    return crystal;
  }

  public int getCrystalUpperBound() {
    return crystalUpperBound;
  }

  public int getLockedCrystal() {
    return lockedCrystal;
  }
}
