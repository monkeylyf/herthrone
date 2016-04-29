package com.herthrone.base;

import com.herthrone.stats.IntAttribute;

/**
 * Created by yifeng on 4/2/16.
 */

public abstract interface BaseCard {

  public String getCardName();
  public String getType();
  public String getClassName();
  public IntAttribute getCrystalManaCost();
  public boolean isCollectible();
}
