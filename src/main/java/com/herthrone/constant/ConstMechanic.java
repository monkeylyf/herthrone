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
  ON_ATTACK(true),
  // Effect mechanics.
  BATTLECRY,
  CARD_DRAW_EFFECT,
  CHOOSE_ONE,
  COMBO,
  COPY_EFFECT,
  DEAL_DAMAGE,
  DEATHRATTLE,
  DESTROY_EFFECT,
  DISCARD_EFFECT,
  ON_EQUIP,
  EQUIP,
  GENERATE_EFFECT,
  INSPIRE,
  JOUST,
  MIND_CONTROL_EFFECT,
  OVERLOAD,
  RESTORE_HEALTH,
  RETURN_EFFECT,
  SECRET,
  SHUFFLE_INTO_DECK,
  SILENCE,
  SPELL_DAMAGE,
  SUMMON,
  TAKE_CONTROL,
  TRANSFORM,
  TRIGGERED_EFFECT;

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
