package com.herthrone.card.factory;

import com.herthrone.base.BaseCard;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSecret;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.game.Battlefield;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class Factory {

  public final MinionFactory minionFactory;
  public final SpellFactory spellFactory;
  public final WeaponFactory weaponFactory;
  public final SecretFactory secretFactory;
  public final EffectFactory effectFactory;
  public final AttackFactory attackFactory;
  private final Battlefield battlefield;

  public Factory(final Battlefield battlefield) {
    this.battlefield = battlefield;
    this.minionFactory = new MinionFactory(battlefield);
    this.weaponFactory = new WeaponFactory(battlefield);
    this.effectFactory = new EffectFactory(this.minionFactory, this.weaponFactory, battlefield);
    this.spellFactory = new SpellFactory(this.effectFactory);
    this.secretFactory = new SecretFactory(battlefield);
    this.attackFactory = new AttackFactory(battlefield);
  }

  public BaseCard createCardInstance(final String cardName) {
    ConstType type = ConfigLoader.getCardTypeByName(cardName);
    switch (type) {
      case MINION:
        ConstMinion minionName = ConstMinion.valueOf(cardName);
        return this.minionFactory.createMinionByName(minionName);
      case WEAPON:
        ConstWeapon weaponName = ConstWeapon.valueOf(cardName);
        return this.weaponFactory.createWeaponByName(weaponName);
      case SPELL:
        ConstSpell spellName = ConstSpell.valueOf(cardName);
        return this.spellFactory.createSpellByName(spellName);
      case SECRET:
        ConstSecret secretName = ConstSecret.valueOf(cardName);
        return this.secretFactory.createSecretByName(secretName);
      default:
        throw new RuntimeException(String.format("Unknown type %s for card %s", type.toString(), cardName));
    }
  }

  public static List<Action> singleActionToList(Action action) {
    return Arrays.asList(action);
  }

  public static boolean targetAny(BaseCard card) {
    return targetMinion(card) || targetMinion(card);
  }

  public static boolean targetMinion(BaseCard card) {
    return card instanceof Minion && !(card instanceof Hero);
  }

  public static boolean targetHero(BaseCard card) {
    return card instanceof Hero;
  }
}
