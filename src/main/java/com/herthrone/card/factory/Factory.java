package com.herthrone.card.factory;

import com.herthrone.base.BaseCard;
import com.herthrone.game.Battlefield;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.configuration.Constants;
import com.herthrone.exception.CardNotFoundException;
import com.herthrone.exception.MinionNotFoundException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class Factory {

  private final Battlefield battlefield;
  public final MinionFactory minionFactory;
  public final SpellFactory spellFactory;
  public final WeaponFactory weaponFactory;
  public final SecretFactory secretFactory;
  public final EffectFactory effectFactory;
  public final AttackFactory attackFactory;

  public Factory(final Battlefield battlefield) {
    this.battlefield = battlefield;
    this.minionFactory = new MinionFactory(battlefield);
    this.weaponFactory = new WeaponFactory(battlefield);
    this.effectFactory = new EffectFactory(this.minionFactory, this.weaponFactory, battlefield);
    this.spellFactory = new SpellFactory(this.effectFactory);
    this.secretFactory = new SecretFactory(battlefield);
    this.attackFactory = new AttackFactory(battlefield);
  }

  public static List<Action> singleActionToList(Action action) {
    return Arrays.asList(action);
  }


  public List<BaseCard> createCardsByName(final List<String> cardNames) throws CardNotFoundException {
    List<BaseCard> cards = new ArrayList<>();
    for (String cardName : cardNames) {
      cards.add(createCardByName(cardName));
    }
    return cards;
  }

  public BaseCard createCardByName(final String cardName) throws CardNotFoundException {
    return this.minionFactory.createMinionByName(cardName);
  }

  public BaseCard createCardByName(final String cardName, final String cardType) throws FileNotFoundException, CardNotFoundException {
    switch (cardType) {
      case Constants.HERO:  return HeroFactory.createHeroByName(cardName);
      case Constants.MINION: return this.minionFactory.createMinionByName(cardName);
      case Constants.WEAPON: return this.weaponFactory.createWeaponByName(cardName);
      case Constants.SPELL: return this.spellFactory.createSpellByName(cardName);
      case Constants.SECRET: return this.secretFactory.createSecretByName(cardName);
      default: throw new CardNotFoundException(String.format("Card %s with type %s does not exist", cardName, cardType));
    }
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
