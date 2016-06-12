package com.herthrone.constant;

import com.herthrone.configuration.MechanicConfig;

/**
 * Created by yifengliu on 5/15/16.
 */
public enum ConstMechanic {
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
  WINDFURY;

  public static ConstMechanic lowerValueOf(final String lowerCaseName) {
    for (ConstMechanic mechanic : values()) {
      if (mechanic.lowerName().equals(lowerCaseName)) {
        return mechanic;
      }
    }
    throw new IllegalArgumentException(String.format("No enum constant %s.%s", MechanicConfig
            .class.toString(),
        lowerCaseName));
  }

  public String lowerName() {
    return name().toLowerCase();
  }
}