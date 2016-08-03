package com.herthrone.constant;

public enum ConstHero {

  ANDUIN_WRYNN("Anduin Wrynn", ConstClass.PRIEST),
  GULDAN("Gul'dan", ConstClass.WARLOCK),
  JAINA_PROUDMOORE("Jaina Proudmoore", ConstClass.MAGE),
  MALFURION_STORMRAGE("Malfurion Stormrage", ConstClass.DRUID),
  REXXAR("Rexxar", ConstClass.HUNTER),
  THRALL("Thrall", ConstClass.SHAMAN),
  UTHER_LIGHTBRINGER("Uther Lightbringer", ConstClass.PALADIN),
  VALEERA_SANGUINAR("Valeera Sanguinar", ConstClass.ROGUE),
  GARROSH_HELLSCREAM("Garrosh Hellscream", ConstClass.WARRIOR);

  public final String hero;
  public final ConstClass clazz;

  ConstHero(final String hero, final ConstClass clazz) {
    this.hero = hero;
    this.clazz = clazz;
  }
}
