package com.herthrone.stats;

/**
 * Created by yifeng on 4/22/16.
 */
public class Buff {

  private final int buffValue;
  private int duration;

  public Buff(final int buffValue, final int duration) {
    this.buffValue = buffValue;
    this.duration = duration;
  }

  public Buff(final int buffValue) {
    this.buffValue = buffValue;
    this.duration = Integer.MAX_VALUE;
  }

  public Buff() {
    this(0);
  }

  public int getBuff() {
    return this.buffValue;
  }

  public int getDuration() {
    return this.duration;
  }

  public void nextRound() {
    this.duration -= 1;
  }
}