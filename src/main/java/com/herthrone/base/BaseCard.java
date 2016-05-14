package com.herthrone.base;

import com.herthrone.Constant;
import com.herthrone.stats.IntAttribute;

/**
 * Created by yifeng on 4/2/16.
 */

public interface BaseCard {

  String getCardName();

  Constant.Type getType();

  Constant.Clazz getClassName();

  IntAttribute getCrystalManaCost();

  boolean isCollectible();
}
