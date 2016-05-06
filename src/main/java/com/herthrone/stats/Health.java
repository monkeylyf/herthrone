package com.herthrone.stats;

/**
 * Created by yifengliu on 5/5/16.
 */
public class Health {

  private final IntAttribute health;
  private final IntAttribute healthUpper;

  public Health(final int val) {
    this.health = new IntAttribute(val);
    this.healthUpper = new IntAttribute(val);
  }
}
