package com.herthrone.card.factory;

import com.herthrone.base.Minion;
import com.herthrone.card.action.PhysicalDamage;

/**
 * Created by yifeng on 4/8/16.
 */
public interface AttackActionFactory {

  public PhysicalDamage yieldAttackAction(Minion creature);
}
