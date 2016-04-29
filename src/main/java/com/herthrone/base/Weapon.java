package com.herthrone.base;

import com.herthrone.stats.IntAttribute;

/**
 * Created by yifeng on 4/2/16.
 */
public interface Weapon extends BaseCard {

  public abstract int use();
  public abstract IntAttribute getDurabilityAttr();
  public abstract IntAttribute getAttackAttr();
}
