package com.herthrone.base;

/**
 * Created by yifeng on 4/14/16.
 */
public class Status {

  private boolean on;
  private boolean resetAfterRound;

  public Status(final boolean on, final boolean resetAfterRound) {
    this.on = on;
    this.resetAfterRound = resetAfterRound;
  }

  public void resetAfter() {
    this.on = false;
  }
}
