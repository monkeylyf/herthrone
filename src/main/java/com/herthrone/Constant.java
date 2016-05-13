package com.herthrone;

/**
 * Created by yifengliu on 5/13/16.
 */
public class Constant {

  // There are used in switch/case so cannot be enum type.
  public static final String ARMOR = "armor";
  public static final String ATTACK = "attack";
  public static final String CRYSTAL = "crystal";
  public static final String HEALTH = "health";
  public static final String HEALTH_UPPER_BOUND = "health_upper_bound";

  public static final String ATTRIBUTE = "attribute";
  public static final String DRAW = "draw";
  public static final String SUMMON = "summon";

  public static final String HERO = "hero";
  public static final String SPELL = "spell";
  public static final String MINION = "minion";
  public static final String SECRET = "secret";
  public static final String WEAPON = "weapon";

  public enum Hero {

    ANDUIN_WRYNN ("Anduin Wrynn", Clazz.PRIEST.name),
    GULDAN ("Gul'dan", Clazz.WARLOCK.name),
    JAINA_PROUDMOORE ("Jaina Proudmoore", Clazz.MAGE.name),
    MALFURION_STORMRAGE ("Malfurion Stormrage", Clazz.DRUID.name),
    REXXAR ("Rexxar", Clazz.HUNTER.name),
    THRALL ("Thrall", Clazz.SHAMAN.name),
    UTHER_LIGHTBRINGER ("Uther Lightbringer", Clazz.PALADIN.name),
    VALEERA_SANGUINAR ("Valeera Sanguinar", Clazz.ROGUE.name),
    GARROSH_HELLSCREAM ("Garrosh Hellscream", Clazz.WARRIOR.name);

    public final String hero;
    public final String clazz;

    Hero(final String hero, final String clazz) {
      this.hero = hero;
      this.clazz = clazz;
    }
  }

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

    public final String name;

    Clazz(final String name) {
      this.name = name;
    }
  }

  public enum EffectType {

    ATTRIBUTE ("attribute"),
    DRAW ("draw"),
    SUMMON ("summon"),
    WEAPON ("weapon");

    public final String name;

    EffectType(final String name) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }
  }

  public enum HeroPower {
    ARMOR_UP ("ArmorUp"),
    DAGGER_MASTERY ("DaggerMastery"),
    FIRE_BLAST ("FireBlast"),
    LESSER_HEAL ("LesserHeal"),
    LIFE_TAP ("LifeTap"),
    REINFORCE ("Reinforce"),
    SHAPE_SHIFT ("Shapeshift"),
    STEADY_SHOT ("SteadyShot"),
    TOTEMIC_CALL ("TotemicCall");

    public final String name;

    HeroPower(final String name) {
      this.name = name;
    }
  }

  public static enum Mechanic {
    BATTLECRY,
    CARD_DRAW_EFFECT,
    CHARGE,
    CHOOSE_ONE,
    COMBO,
    COPY_EFFECT,
    DEAL_DAMAGE,
    DEATHRATTLE,
    DESTROY_EFFECT,
    DISCARD_EFFECT,
    DIVINE_SHIELD,
    ELUSIVE,
    ENRAGE,
    EQUIP,
    FORGETFUl,
    FREEZE,
    GENERATE_EFFECT,
    IMMUNE,
    INSPIRE,
    JOUST,
    MIND_CONTROL_EFFECT,
    OVERLOAD,
    POISON,
    RESTORE_HEALTH,
    RETURN_EFFECT,
    SECRET,
    SHUFFLE_INTO_DECK,
    SILENCE,
    SPELL_DAMAGE,
    STEALTH,
    SUMMON,
    TAUNT,
    TAKE_CONTROL,
    TRANSFORM,
    TRIGGERED_EFFECT,
    WINDFURY,
  }

  public enum Minion {

    CHILLWIND_YETI ("Chillwind Yeti"),
    WOLFRIDER ("Wolfrider");

    public final String name;

    Minion(final String name) {
      this.name = name;
    }
  }

  public enum Secret {
  }

  public enum Spell {
  }

  public enum Weapon {

    FIERY_WAR_AEX ("FieryWarAxe"),
    WICKED_KNIFE ("WickedKnife");

    public final String name;

    Weapon(final String name) {
      this.name = name;
    }

  }
}
