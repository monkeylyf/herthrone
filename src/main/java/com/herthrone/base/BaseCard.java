package com.herthrone.base;

/**
 * Created by yifeng on 4/2/16.
 */

public abstract interface BaseCard {

  public String getCardName();
  public String getType();
  public String getClassName();
  public Attribute getCrystalManaCost();
}
