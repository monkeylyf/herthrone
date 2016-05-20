package com.herthrone.stats;

/**
 * Created by yifengliu on 5/5/16.
 */
public class Buff {

  public Value temp;
  public Value perm;

  public Buff() {
    this.temp = new Value();
    this.perm = new Value();
  }

  public int getBuffVal() {
    return temp.getVal() + perm.getVal();
  }

  public void reset() {
    temp.reset();
    perm.reset();
  }
}
