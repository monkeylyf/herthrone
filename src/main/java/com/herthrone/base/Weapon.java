package com.herthrone.base;

import com.herthrone.stats.IntAttribute;

/**
 * Created by yifeng on 4/2/16.
 */
public interface Weapon extends BaseCard {

  int use();

  IntAttribute getDurabilityAttr();

  IntAttribute getAttackAttr();
}
