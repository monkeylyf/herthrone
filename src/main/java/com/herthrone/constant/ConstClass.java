package com.herthrone.constant;

/**
 * Created by yifengliu on 5/15/16.
 */
public enum ConstClass {

  COMMON("Common"),
  WARRIOR("Warrior"),
  PRIEST("Priest"),
  ROGUE("Rogue"),
  MAGE("Mage"),
  PALADIN("Paladin"),
  WARLOCK("Warlock"),
  SHAMAN("Shaman"),
  DRUID("Druid"),
  HUNTER("Hunter");

  public final String name;

  ConstClass(final String name) {
    this.name = name;
  }
}
