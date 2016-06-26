package com.herthrone.game;

import com.herthrone.base.Card;
import com.herthrone.base.Creature;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Reset;
import com.herthrone.base.Round;
import com.herthrone.base.Secret;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.objects.IntAttribute;
import com.herthrone.objects.ManaCrystal;
import com.herthrone.objects.Replay;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class Side implements Round {

  static Logger logger = Logger.getLogger(Side.class.getName());
  public final Hero hero;
  public final Container<Card> hand;
  public final Container<Card> deck;
  public final Container<Minion> board;
  public final Container<Secret> secrets;
  public final ManaCrystal manaCrystal;
  public final IntAttribute heroPowerMovePoints;
  public final Replay replay;
  private EffectQueue effectQueue;
  private int fatigue;

  public Side(final Hero hero, final EffectQueue effectQueue) {
    final int handCapacity = Integer.parseInt(ConfigLoader.getResource().getString("hand_max_capacity"));
    final int boardCapacity = Integer.parseInt(ConfigLoader.getResource().getString("board_max_capacity"));
    final int deckCapacity = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));

    this.hero = hero;
    hero.getBinder().bind(this);
    this.hand = new Container<>(handCapacity);
    this.board = new Container<>(boardCapacity);
    this.secrets = new Container<>();
    this.manaCrystal = new ManaCrystal();

    this.deck = new Container<>(deckCapacity);
    this.heroPowerMovePoints = new IntAttribute(1);
    this.replay = new Replay();

    this.fatigue = 0;
    this.effectQueue = effectQueue;
  }

  public void populateDeck(final List<Enum> cards) {
    cards.stream().forEach(cardName -> {
      final Card card = GameManager.createCardInstance(cardName);
      deck.add(card);
      if (card instanceof Minion) {
        card.getBinder().bind(this);
      }
    });
  }

  public void takeFatigueDamage() {
    fatigue += 1;
    logger.debug(String.format("Increase fatigue to %d", fatigue));
    hero.takeDamage(fatigue);
  }

  public boolean hasCreature(final Creature creature) {
    if (creature instanceof Hero) {
      return creature == hero;
    } else {
      return board.contains((Minion) creature);
    }
  }

  public List<Creature> allCreatures() {
    List<Creature> allCreatures = new ArrayList<>();
    allCreatures.add(hero);
    for (int i = 0; i < board.size(); ++i) {
      allCreatures.add(board.get(i));
    }

    return allCreatures;
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
}
