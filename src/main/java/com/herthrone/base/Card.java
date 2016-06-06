package com.herthrone.base;

import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstType;
import com.herthrone.stats.IntAttribute;

/**
 * Created by yifeng on 4/2/16.
 */

public interface Card extends View {

  String getCardName();

  ConstType getType();

  ConstClass getClassName();

  IntAttribute getCrystalManaCost();

  boolean isCollectible();
}
