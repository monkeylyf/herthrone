package com.herthrone.game;

/**
 * Created by yifengliu on 5/18/16.
 */
public class Target {

  private final int index;
  private final TargetSide target;

  public Target(final int index) {
    this.index = index;
    this.target = TargetSide.ENEMY;
  }

  private enum TargetSide {
    ENEMY,
    OWN,
  }
}
