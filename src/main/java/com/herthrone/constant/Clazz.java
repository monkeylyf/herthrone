package com.herthrone.constant;

/**
 * Created by yifengliu on 5/12/16.
 */
public enum Clazz {

  WARRIOR ("Warrior"),
  PRIEST ("Priest"),
  ROGUE ("Rogue"),
  MAGE ("Mage"),
  PALADIN ("Paladin"),
  WARLOCK ("Warlock"),
  SHAMAN ("Shaman"),
  DRUID ("Druid"),
  HUNTER ("Hunter");

  private final String className;

  private Clazz(final String className) {
    this.className = className;
  }

  public String getClassName() {
    return this.className;
  }
}
