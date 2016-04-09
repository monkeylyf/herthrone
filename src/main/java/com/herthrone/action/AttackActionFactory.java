package com.herthrone.action;

import com.herthrone.base.BaseCreature;

/**
 * Created by yifeng on 4/8/16.
 */
public interface AttackActionFactory {

  public AttackAction yieldAttackAction(BaseCreature creature);
}
