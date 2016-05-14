package com.herthrone;


import java.util.Map;

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

  public enum Attribute {
    ARMOR,
    ATTACK,
    CRYSTAL,
    HEALTH,
    HEALTH_UPPER_BOUND;
  }

  public enum Hero {

    ANDUIN_WRYNN ("Anduin Wrynn", Clazz.PRIEST),
    GULDAN ("Gul'dan", Clazz.WARLOCK),
    JAINA_PROUDMOORE ("Jaina Proudmoore", Clazz.MAGE),
    MALFURION_STORMRAGE ("Malfurion Stormrage", Clazz.DRUID),
    REXXAR ("Rexxar", Clazz.HUNTER),
    THRALL ("Thrall", Clazz.SHAMAN),
    UTHER_LIGHTBRINGER ("Uther Lightbringer", Clazz.PALADIN),
    VALEERA_SANGUINAR ("Valeera Sanguinar", Clazz.ROGUE),
    GARROSH_HELLSCREAM ("Garrosh Hellscream", Clazz.WARRIOR);

    public final String hero;
    public final Clazz clazz;

    Hero(final String hero, final Clazz clazz) {
      this.hero = hero;
      this.clazz = clazz;
    }
  }

  public enum Clazz {

    COMMON ("Common"),
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
    ARMOR_UP,
    DAGGER_MASTERY,
    FIRE_BLAST,
    LESSER_HEAL,
    LIFE_TAP,
    REINFORCE,
    SHAPESHIFT,
    STEADY_SHOT,
    TOTEMIC_CALL;
  }

  public enum Mechanic {
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
    SENJIN_SHIELDMASTA ("shit"),
    WOLFRIDER ("Wolfrider"),
    HEALING_TOTEM ("shit"),
    SEARING_TOTEM ("shit"),
    WRATH_OF_AIR_TOTEM ("shit"),
    STONECLAW_TOTEM ("shit"),
    SILVER_HAND_RECRUIT ("Wolfrider");

    public final String name;

    Minion(final String name) {
      this.name = name;
    }
  }

  public enum Secret {
  }

  public enum Spell {
    ARMOR_UP,
    DAGGER_MASTERY,
    FIRE_BLAST,
    FIRE_BALL,
    LESSER_HEAL,
    LIFE_TAP,
    REINFORCE,
    SHAPESHIFT,
    STEADY_SHOT,
    TOTEMIC_CALL;
  }

  public enum Type {
    HERO,
    SPELL,
    MINION,
    SECRET,
    HERO_POWER,
    WEAPON;
  }

  public enum Weapon {
    FIERY_WAR_AXE,
    WICKED_KNIFE,
    WICKED_BLADE,
    TRUESILVERCHAMPION;
  }

  public static String upperCaseValue(final Map map,final String key) {
    final String value = (String) map.get(key);
    return value.toUpperCase();
  }
}
