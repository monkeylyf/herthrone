package com.herthrone.base;

import com.herthrone.stats.IntAttribute;

/**
 * Created by yifeng on 4/2/16.
 */

public interface BaseCard {

  String getCardName();

  // TODO: return type should be enum.
  String getType();

  String getClassName();

  IntAttribute getCrystalManaCost();

  boolean isCollectible();
}
