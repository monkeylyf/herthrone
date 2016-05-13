package com.herthrone.constant;

/**
 * Created by yifengliu on 5/12/16.
 */
public enum Hero {

   ANDUIN_WRYNN ("Anduin Wrynn", Clazz.PRIEST.name()),
   GULDAN ("Gul'dan", Clazz.WARLOCK.name()),
   JAINA_PROUDMOORE ("Jaina Proudmoore", Clazz.MAGE.name()),
   MALFURION_STORMRAGE ("Malfurion Stormrage", Clazz.DRUID.name()),
   REXXAR ("Rexxar", Clazz.HUNTER.name()),
   THRALL ("Thrall", Clazz.SHAMAN.name()),
   UTHER_LIGHTBRINGER ("Uther Lightbringer", Clazz.PALADIN.name()),
   VALEERA_SANGUINAR ("Valeera Sanguinar", Clazz.ROGUE.name()),
   GARROSH_HELLSCREAM ("Garrosh Hellscream", Clazz.WARRIOR.name());

   private final String heroName;
   private final String className;

   private Hero(final String heroName, final String className) {
      this.heroName = heroName;
      this.className = className;
   }

   public String getHeroName() {
      return this.heroName;
   }

   public String getClassName() {
      return this.className;
   }
}
