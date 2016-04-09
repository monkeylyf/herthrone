package com.herthrone.base;

import com.herthrone.action.AttackActionFactory;

import java.util.Optional;

/**
 * Created by yifeng on 4/2/16.
 */


public interface BaseCreature extends BaseCard, AttackActionFactory {

  public Attribute getHealthAttr();
  public Attribute getAttackAttr();
  public Attribute getArmorAttr();

  public void causeDamage(BaseCreature creature);
  public void takeDamage(final int damage);

  public void equipWeapon(Weapon weapon);
  public void disarm();

  public boolean canDamage();
}
