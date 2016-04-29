package com.herthrone.base;

import com.herthrone.stats.IntAttribute;
import com.herthrone.stats.BooleanAttribute;

/**
 * Created by yifeng on 4/2/16.
 */


public interface Minion extends BaseCard {

  public IntAttribute getHealthAttr();
  public IntAttribute getHealthUpperAttr();
  public IntAttribute getAttackAttr();

  public BooleanAttribute getDamageImmunity();
  public BooleanAttribute getFrozen();
  public BooleanAttribute getDivineShield();

  public void causeDamage(Minion creature);
  public void takeDamage(final int damage);
  public boolean canDamage();

  public boolean isDead();
  public int getHealthLoss();
}
