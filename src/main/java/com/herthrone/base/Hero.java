package com.herthrone.base;

import com.herthrone.stats.Attribute;

/**
 * Created by yifeng on 4/13/16.
 */
public interface Hero extends Minion {

  public Attribute getArmorAttr();
  public void equipWeapon(Weapon weapon);
  public void disarm();

}
