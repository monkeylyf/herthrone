package com.herthrone.game;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.herthrone.base.Card;
import com.herthrone.base.Creature;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Round;
import com.herthrone.base.Secret;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSecret;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.SecretFactory;
import com.herthrone.factory.SpellFactory;
import com.herthrone.factory.TriggerFactory;
import com.herthrone.factory.WeaponFactory;
import com.herthrone.object.Replay;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class Side implements Round {

  private static Logger logger = Logger.getLogger(Side.class.getName());
  public final Hero hero;
  public final Container<Card> hand;
  public final Container<Card> deck;
  public final Container<Minion> board;
  public final Container<Secret> secrets;
  public final Replay replay;
  private final EffectQueue effectQueue;
  private final IntSupplier idGenerator;
  private int fatigue;
  private Side opponentSide;

  private Side(final Hero hero, final EffectQueue effectQueue, final IntSupplier idGenerator) {
    final int handCapacity = Integer.parseInt(ConfigLoader.getResource().getString("hand_max_capacity"));
    final int boardCapacity = Integer.parseInt(ConfigLoader.getResource().getString("board_max_capacity"));
    final int deckCapacity = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));

    this.hero = hero;
    bind(hero);
    bind(hero.getHeroPower());
    hero.binder().bind(this);
    this.hand = new Container<>(handCapacity);
    this.board = new Container<>(boardCapacity);
    this.secrets = new Container<>();

    this.deck = new Container<>(deckCapacity);
    this.replay = new Replay();

    this.fatigue = 0;
    this.effectQueue = effectQueue;

    this.idGenerator = idGenerator;
  }

  public void bind(final Card card) {
    card.binder().bind(this);
  }

  static Side createSidePair(final Hero hero1, final Hero hero2, final EffectQueue effectQueue) {
    final IntSupplier sequenceIdGenerator = new IntSupplier() {
      private int id = 0;

      @Override
      public int getAsInt() {
        ++id;
        return id;
      }
    };
    final Side thisSide = new Side(hero1, effectQueue, sequenceIdGenerator);
    final Side thatSide = new Side(hero2, effectQueue, sequenceIdGenerator);
    thisSide.opponentSide = thatSide;
    thatSide.opponentSide = thisSide;
    return thisSide;
  }

  void populateDeck(final List<Enum> cards) {
    cards.forEach(cardName -> {
      final Card card = createCardInstance(cardName);
      deck.add(card);
      bind(card);
    });
  }

  private static Card createCardInstance(final Enum cardName) {
    final String name = cardName.toString();

    Optional<ConstMinion> constMinion = Enums.getIfPresent(ConstMinion.class, name);
    if (constMinion.isPresent()) {
      return MinionFactory.create(constMinion.get());
    }

    Optional<ConstWeapon> constWeapon = Enums.getIfPresent(ConstWeapon.class, name);
    if (constWeapon.isPresent()) {
      return WeaponFactory.create(constWeapon.get());
    }

    Optional<ConstSpell> constSpell = Enums.getIfPresent(ConstSpell.class, name);
    if (constSpell.isPresent()) {
      return SpellFactory.create(constSpell.get());
    }

    Optional<ConstSecret> constSecret = Enums.getIfPresent(ConstSecret.class, name);
    if (constSecret.isPresent()) {
      return SecretFactory.create(constSecret.get());
    }

    throw new RuntimeException(String.format("Unknown card %s", name));
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

  public EffectQueue getEffectQueue() {
    return effectQueue;
  }

  @Override
  public void endTurn() {
    replay.endTurn();
    hero.endTurn();
    board.stream().forEach(Round::startTurn);
    TriggerFactory.triggerByBoard(getOpponentSide().board.stream(), this, ConstTrigger.ON_OPPONENT_END_TURN);
  }

  @Override
  public void startTurn() {
    replay.startTurn();
    hero.startTurn();
    board.stream().forEach(Round::endTurn);
    TriggerFactory.triggerByBoard(getOpponentSide().board.stream(), this, ConstTrigger.ON_OPPONENT_START_TURN);
  }

  public Side getOpponentSide() {
    return opponentSide;
  }

  public void setSequenceId(final Minion minion) {
    final int sequenceId = idGenerator.getAsInt();
    logger.debug("Set ID " + sequenceId + " to minion " + minion);
    minion.setSequenceId(sequenceId);
  }

  @Override
  public String toString() {
    return board.toString();
  }
}
