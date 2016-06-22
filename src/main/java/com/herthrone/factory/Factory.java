package com.herthrone.factory;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.herthrone.base.Card;
import com.herthrone.base.Effect;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSecret;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.game.Battlefield;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class Factory {

  public final MinionFactory minionFactory;
  public final WeaponFactory weaponFactory;
  public final SecretFactory secretFactory;
  public final EffectFactory effectFactory;
  public final AttackFactory attackFactory;

  public Factory(final Battlefield battlefield) {
    this.minionFactory = new MinionFactory(battlefield);
    this.weaponFactory = new WeaponFactory(battlefield);
    this.effectFactory = new EffectFactory(minionFactory, weaponFactory, battlefield);
    this.secretFactory = new SecretFactory(battlefield);
    this.attackFactory = new AttackFactory(battlefield);
  }

  public Card createCardInstance(final Enum cardName) {
    final String name = cardName.toString();

    Optional<ConstMinion> constMinion = Enums.getIfPresent(ConstMinion.class, name);
    if (constMinion.isPresent()) {
      return minionFactory.createMinionByName(constMinion.get());
    }

    Optional<ConstWeapon> constWeapon = Enums.getIfPresent(ConstWeapon.class, name);
    if (constWeapon.isPresent()) {
      return weaponFactory.createWeaponByName(constWeapon.get());
    }

    Optional<ConstSpell> constSpell = Enums.getIfPresent(ConstSpell.class, name);
    if (constSpell.isPresent()) {
      return SpellFactory.createSpellByName(constSpell.get());
    }

    Optional<ConstSecret> constSecret = Enums.getIfPresent(ConstSecret.class, name);
    if (constSecret.isPresent()) {
      return secretFactory.createSecretByName(constSecret.get());
    }

    throw new RuntimeException(String.format("Unknown card %s", name));
  }
}
