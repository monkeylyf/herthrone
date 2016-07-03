package com.herthrone.constant;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yifengliu on 5/15/16.
 * <p>
 * http://hearthstone.gamepedia.com/<mechanicName>
 */
public enum ConstMechanic {
  // Boolean mechanics.
  CHARGE(true),
  DIVINE_SHIELD(true),
  ELUSIVE(true),
  ENRAGE(true),
  FORGETFUL(true),
  FREEZE(true), // Consequence of being hit by a minion has freeze ability.
  FROZEN(true),
  IMMUNE(true),
  POISON(true),
  STEALTH(true),
  TAUNT(true),
  WINDFURY(true),
  // Effect mechanics.
  AURA,
  //BATTLECRY,
  CARD_DRAW,
  CHOOSE_ONE,
  COMBO,
  COPY,
  DEAL_DAMAGE,
  DEATHRATTLE,
  DESTROY,
  DISCARD,
  DISCOVER,
  ON_ATTACK,
  ON_EQUIP,
  ON_PLAY,
  ON_SUMMON,
  EQUIP,
  GENERATE,
  INSPIRE,
  JOUST,
  MIND_CONTROL,
  OVERLOAD,
  REPLACE,
  RESTORE_HEALTH,
  RETURN_TO_HAND,
  SECRET,
  SHUFFLE_INTO_DECK,
  SILENCE,
  SPELL_DAMAGE,
  SUMMON,
  TAKE_CONTROL,
  TRANSFORM,
  TRIGGERED;

  private boolean isBooleanMechanics;

  ConstMechanic() {
    this(false);
  }

  ConstMechanic(final boolean isBooleanMechanisc) {
    this.isBooleanMechanics = isBooleanMechanisc;
  }

  public static List<ConstMechanic> getBooleanMechanics() {
    return Arrays.stream(values()).filter(m -> m.isBooleanMechanics).collect(Collectors.toList());
  }

  public static List<ConstMechanic> getEffectMechanics() {
    return Arrays.stream(values()).filter(m -> !m.isBooleanMechanics).collect(Collectors.toList());
  }
}
