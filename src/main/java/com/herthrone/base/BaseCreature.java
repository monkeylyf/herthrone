package com.herthrone.base;

import com.herthrone.action.Damage;

/**
 * Created by yifeng on 4/2/16.
 */


public interface BaseCreature extends BaseCard {

  public Attribute getHealthAttr();
  public Attribute getAttackAttr();
  public void causeDamage(BaseCreature creature);
  public void takeDamage(final int damage);
}
