package com.herthrone.base;

import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstType;
import com.herthrone.game.Binder;
import com.herthrone.object.IntAttribute;

/**
 * Created by yifeng on 4/2/16.
 */

public interface Card extends View {

  String cardName();

  String displayName();

  ConstType type();

  ConstClass className();

  IntAttribute manaCost();

  boolean isCollectible();

  Binder binder();
}
