package com.herthrone.factory;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.herthrone.base.Card;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSecret;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.game.Battlefield;

/**
 * Created by yifeng on 4/14/16.
 */
public class Factory {

  public Factory(final Battlefield battlefield) {
  }

  public static Card createCardInstance(final Enum cardName) {
    final String name = cardName.toString();

    Optional<ConstMinion> constMinion = Enums.getIfPresent(ConstMinion.class, name);
    if (constMinion.isPresent()) {
      return MinionFactory.createMinionByName(constMinion.get());
    }

    Optional<ConstWeapon> constWeapon = Enums.getIfPresent(ConstWeapon.class, name);
    if (constWeapon.isPresent()) {
      return WeaponFactory.createWeaponByName(constWeapon.get());
    }

    Optional<ConstSpell> constSpell = Enums.getIfPresent(ConstSpell.class, name);
    if (constSpell.isPresent()) {
      return SpellFactory.createSpellByName(constSpell.get());
    }

    Optional<ConstSecret> constSecret = Enums.getIfPresent(ConstSecret.class, name);
    if (constSecret.isPresent()) {
      return SecretFactory.createSecretByName(constSecret.get());
    }

    throw new RuntimeException(String.format("Unknown card %s", name));
  }
}
