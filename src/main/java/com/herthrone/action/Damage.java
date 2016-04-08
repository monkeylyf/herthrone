package com.herthrone.action;

import com.herthrone.base.BaseCreature;

/**
 * Created by yifeng on 4/4/16.
 */
public interface Damage {

  public void causeDamage(BaseCreature creature);
  public void takeDamage(final int damage);

  //public Action AttackAction(BaseCreature baseCreature);
}
