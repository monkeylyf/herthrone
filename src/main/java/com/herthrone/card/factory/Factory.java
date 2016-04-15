package com.herthrone.card.factory;

import com.herthrone.base.BaseCard;
import com.herthrone.base.Battlefield;
import com.herthrone.exception.CardNotFoundException;
import com.herthrone.exception.MinionNotFoundException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class Factory {

  private final Battlefield battlefield;
  private final MinionFactory minionFactory;
  private final SpellFactory spellFactory;
  private final WeaponFactory weaponFactory;

  public Factory(final Battlefield battlefield) {
    this.battlefield = battlefield;
    this.minionFactory = new MinionFactory(battlefield);
    this.spellFactory = new SpellFactory(battlefield);
    this.weaponFactory = new WeaponFactory(battlefield);
  }


  public List<BaseCard> createCardsByName(final List<String> cardNames) throws CardNotFoundException {
    List<BaseCard> cards = new ArrayList<>();
    for (String cardName : cardNames) {
      cards.add(createCardByName(cardName));
    }
    return cards;
  }

  public BaseCard createCardByName(final String cardName) throws CardNotFoundException {
    try {
      return this.minionFactory.createMinionByName(cardName);
    } catch (FileNotFoundException |MinionNotFoundException e) {
      throw new CardNotFoundException(cardName);
    }
  }
}
