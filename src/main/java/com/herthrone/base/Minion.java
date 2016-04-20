package com.herthrone.base;

/**
 * Created by yifeng on 4/2/16.
 */


public interface Minion extends BaseCard {

  public Attribute getHealthAttr();
  public Attribute getHealthUpperAttr();
  public Attribute getAttackAttr();

  public Status getDamageImmunity();
  public Status getFrozen();
  public Status getDivineShield();

  public void causeDamage(Minion creature);
  public void takeDamage(final int damage);
  public boolean canDamage();

  public boolean isDead();
}
