package com.herthrone.base;

import com.herthrone.action.AttackActionFactory;

/**
 * Created by yifeng on 4/2/16.
 */


public interface Minion extends BaseCard, AttackActionFactory {

  public Attribute getHealthAttr();
  public Attribute getHealthUpperAttr();
  public Attribute getAttackAttr();
  public String getHeroClass();

  public void causeDamage(Minion creature);
  public void takeDamage(final int damage);

  public boolean canDamage();
}
