package com.herthrone.game;

import com.herthrone.base.BaseCard;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Secret;
import com.herthrone.card.factory.Factory;
import com.herthrone.card.factory.HeroFactory;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.constant.ConstHero;
import com.herthrone.stats.Crystal;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yifeng on 4/14/16.
 */
public class Side {
  public final Hero hero;
  public final Container<BaseCard> hand;
  public final Container<BaseCard> deck;
  public final Container<Minion> minions;
  public final Container<Secret> secrets;
  public final Crystal crystal;

  public Side(final ConstHero hero) {
    final int handCapacity = Integer.parseInt(ConfigLoader.getResource().getString("hand_max_capacity"));
    final int boardCapacity = Integer.parseInt(ConfigLoader.getResource().getString("board_max_capacity"));
    final int deckCapacity = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));

    this.hero = HeroFactory.createHeroByName(hero);
    this.hand = new Container<>(handCapacity);
    this.minions = new Container<>(boardCapacity);
    this.secrets = new Container<>();
    this.crystal = new Crystal();

    this.deck = new Container<>(deckCapacity);
  }

  public void populateDeck(final List<BaseCard> cards) {
    cards.stream().forEach(card -> this.deck.add(card));
  }

}
