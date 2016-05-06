package com.herthrone.base;

import com.herthrone.stats.IntAttribute;

/**
 * Created by yifeng on 4/2/16.
 */

public interface BaseCard {

  String getCardName();

  String getType();

  String getClassName();

  IntAttribute getCrystalManaCost();

  boolean isCollectible();
}
