package com.herthrone.constant;

/**
 * Created by yifengliu on 5/15/16.
 */
public enum ConstEffectType {

  ATTRIBUTE("attribute"),
  DRAW("draw"),
  SUMMON("summon"),
  WEAPON("weapon");

  public final String name;

  ConstEffectType(final String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
