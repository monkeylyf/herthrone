package com.herthrone.base;

import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstType;
import com.herthrone.object.ValueAttribute;

/**
 * Created by yifeng on 4/2/16.
 */

public interface Card extends View, Bind {

  String cardName();

  String displayName();

  ConstType type();

  ConstClass className();

  ValueAttribute manaCost();

  boolean isCollectible();
}
