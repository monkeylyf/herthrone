package com.herthrone.game;

import com.herthrone.base.Card;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Secret;
import com.herthrone.base.Spell;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.stats.IntAttribute;
import com.herthrone.stats.ManaCrystal;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class Side {

  static Logger logger = Logger.getLogger(Side.class.getName());
  public final Hero hero;
  public final Container<Card> hand;
  public final Container<Card> deck;
  public final Container<Minion> board;
  public final Container<Secret> secrets;
  public final ManaCrystal manaCrystal;
  public final IntAttribute heroPowerMovePoints;
  public Spell heroPower;
  private int fatigue;

  public Side(final Hero hero) {
    final int handCapacity = Integer.parseInt(ConfigLoader.getResource().getString("hand_max_capacity"));
    final int boardCapacity = Integer.parseInt(ConfigLoader.getResource().getString("board_max_capacity"));
    final int deckCapacity = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));

    this.hero = hero;
    this.heroPower = null;
    this.hand = new Container<>(handCapacity);
    this.board = new Container<>(boardCapacity);
    this.secrets = new Container<>();
    this.manaCrystal = new ManaCrystal();

    this.deck = new Container<>(deckCapacity);
    this.heroPowerMovePoints = new IntAttribute(1);

    this.fatigue = 0;
  }

  public void populateDeck(final List<Card> cards) {
    cards.stream().forEach(card -> deck.add(card));
  }

  public void takeFatigueDamage() {
    fatigue += 1;
    logger.debug(String.format("Increase fatigue to %d", fatigue));
    hero.takeDamage(fatigue);
  }

  public void setHeroPower(final Spell heroPower) {
    this.heroPower = heroPower;
  }
}
