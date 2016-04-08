package com.herthrone.base;

/**
 * Created by yifeng on 4/2/16.
 */
public interface Weapon extends BaseCard {

  public abstract int use();
  public abstract Attribute getDurability();
  public abstract Attribute getAttack();
}
