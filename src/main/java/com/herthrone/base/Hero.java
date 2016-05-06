package com.herthrone.base;

import com.herthrone.stats.IntAttribute;

/**
 * Created by yifeng on 4/13/16.
 */
public interface Hero extends Minion {

  IntAttribute getArmorAttr();

  void arm(Weapon weapon);

  void disarm();

}
