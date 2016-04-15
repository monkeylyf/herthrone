package com.herthrone.action;

import com.herthrone.base.Minion;

/**
 * Created by yifeng on 4/4/16.
 */
public interface PhysicalDamage {

  public void causeDamage(Minion creature);
  public void takeDamage(final int damage);

  //public Action AttackAction(BaseCreature baseCreature);
}
