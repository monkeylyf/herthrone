package com.herthrone.game;

import com.herthrone.base.BaseCard;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Secret;
import com.herthrone.base.Spell;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.stats.Crystal;
import com.herthrone.stats.IntAttribute;

import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class Side {
  public final Hero hero;
  public final Container<BaseCard> hand;
  public final Container<BaseCard> deck;
  public final Container<Minion> board;
  public final Container<Secret> secrets;
  public final Crystal crystal;
  public final IntAttribute heroPowerMovePoints;

  public Spell heroPower;

  public Side(final Hero hero) {
    final int handCapacity = Integer.parseInt(ConfigLoader.getResource().getString("hand_max_capacity"));
    final int boardCapacity = Integer.parseInt(ConfigLoader.getResource().getString("board_max_capacity"));
    final int deckCapacity = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));

    this.hero = hero;
    this.heroPower = null;
    this.hand = new Container<>(handCapacity);
    this.board = new Container<>(boardCapacity);
    this.secrets = new Container<>();
    this.crystal = new Crystal();

    this.deck = new Container<>(deckCapacity);
    this.heroPowerMovePoints = new IntAttribute(1);
  }

  public void populateDeck(final List<BaseCard> cards) {
    cards.stream().forEach(card -> this.deck.add(card));
  }

  public void setHeroPower(final Spell heroPower) {
    this.heroPower = heroPower;
  }

}
