package com.herthrone.game;

import com.herthrone.base.Card;
import com.herthrone.base.Creature;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Round;
import com.herthrone.base.Secret;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.object.Replay;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Side implements Round {

  private static Logger logger = Logger.getLogger(Side.class.getName());
  public final Hero hero;
  public final Container<Card> hand;
  public final Container<Card> deck;
  public final Container<Minion> board;
  public final Container<Secret> secrets;
  public final Replay replay;
  private EffectQueue effectQueue;
  private int fatigue;
  private Side opponentSide;

  public Side(final Hero hero, final EffectQueue effectQueue) {
    final int handCapacity = Integer.parseInt(ConfigLoader.getResource().getString("hand_max_capacity"));
    final int boardCapacity = Integer.parseInt(ConfigLoader.getResource().getString("board_max_capacity"));
    final int deckCapacity = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));

    this.hero = hero;
    hero.binder().bind(this);
    this.hand = new Container<>(handCapacity);
    this.board = new Container<>(boardCapacity);
    this.secrets = new Container<>();

    this.deck = new Container<>(deckCapacity);
    this.replay = new Replay();

    this.fatigue = 0;
    this.effectQueue = effectQueue;
  }

  static Side createSide(final Hero hero1, final Hero hero2, final EffectQueue effectQueue) {
    final Side thisSide = new Side(hero1, effectQueue);
    thisSide.bind(hero1);
    thisSide.bind(hero1.getHeroPower());
    final Side thatSide = new Side(hero2, effectQueue);
    thatSide.bind(hero2);
    thatSide.bind(hero2.getHeroPower());

    thisSide.opponentSide = thatSide;
    thatSide.opponentSide = thisSide;
    return thisSide;
  }

  void populateDeck(final List<Enum> cards) {
    cards.stream().forEach(cardName -> {
      final Card card = GameManager.createCardInstance(cardName);
      deck.add(card);
      if (card instanceof Minion) {
        card.binder().bind(this);
      }
    });
  }

  public void takeFatigueDamage() {
    fatigue += 1;
    logger.debug(String.format("Increase fatigue to %d", fatigue));
    hero.takeDamage(fatigue);
  }

  public List<Creature> allCreatures() {
    List<Creature> allCreatures = new ArrayList<>();
    allCreatures.add(hero);
    for (int i = 0; i < board.size(); ++i) {
      allCreatures.add(board.get(i));
    }

    return allCreatures;
  }

  public void bind(final Card card) {
    card.binder().bind(this);
  }

  public EffectQueue getEffectQueue() {
    return effectQueue;
  }

  @Override
  public void endTurn() {
    replay.endTurn();
  }

  @Override
  public void startTurn() {
    replay.startTurn();
  }

  public Side getOpponentSide() {
    return opponentSide;
  }

  @Override
  public String toString() {
    return board.toString();
  }
}
