package com.herthrone.game;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.herthrone.base.Card;
import com.herthrone.base.Creature;
import com.herthrone.base.Minion;
import com.herthrone.base.Round;
import com.herthrone.base.Secret;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstAction;
import com.herthrone.constant.ConstCommand;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSecret;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.HeroFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.SecretFactory;
import com.herthrone.factory.SpellFactory;
import com.herthrone.factory.TargetFactory;
import com.herthrone.factory.TriggerFactory;
import com.herthrone.factory.WeaponFactory;
import com.herthrone.service.Command;
import com.herthrone.service.Entity;
import com.herthrone.service.StartGameSetting;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Game implements Round {

  private static final Logger logger = Logger.getLogger(Game.class.getName());
  private static Map<String, Game> gamePool = new HashMap<>();

  private final String gameId;
  private final Battlefield battlefield1;
  private final Battlefield battlefield2;
  public Battlefield activeBattlefield;
  public Side activeSide;
  public Side inactiveSide;

  public static String StartGame(final List<StartGameSetting> settings) {
    // TODO:
    Preconditions.checkArgument(settings.size() == 2);

    // Concatenate user IDs to form a game ID in alphabetical order.
    final List<String> IdSequence = settings.stream()
        .map(setting -> setting.getUserId())
        .sorted()
        .collect(Collectors.toList());
    final String gameId = Joiner.on(':').join(IdSequence);

    if (!gamePool.containsKey(gameId)) {
      synchronized (gamePool) {
        final Game game = new Game(gameId,
            ConstHero.valueOf(settings.get(0).getHero()),
            ConstHero.valueOf(settings.get(1).getHero()),
            settings.get(0).getCardsList().stream().map(Game::toEnum).collect(Collectors.toList()),
            settings.get(1).getCardsList().stream().map(Game::toEnum).collect(Collectors.toList())
        );
        gamePool.put(gameId, game);
        logger.info(String.format("Creating game with ID: %s", gameId));
      }
    } else {
      logger.info(String.format("Game already exists for Id: %s", gameId));
    }
    return gameId;
  }

  private static Enum toEnum(final String cardName) {
    final String upperCardName = cardName.toUpperCase();
    IllegalArgumentException finalError;
    try {
      return ConstMinion.valueOf(upperCardName);
    } catch (IllegalArgumentException error) {
      finalError = error;
    }
    try {
      return ConstSpell.valueOf(upperCardName);
    } catch (IllegalArgumentException error) {
      finalError = error;
    }
    try {
      return ConstWeapon.valueOf(upperCardName);
    } catch (IllegalArgumentException error) {
      finalError = error;
    }

    throw new IllegalArgumentException(finalError.toString());
  }

  public Game(final String gameId, final ConstHero hero1, final ConstHero hero2,
              final List<Enum> cardNames1, final List<Enum> cardNames2) {
    // TODO: need to find a place to init deck given cards in a collection.
    this.gameId = gameId;
    this.battlefield1 = new Battlefield(HeroFactory.create(hero1), HeroFactory.create(hero2));
    this.battlefield2 = battlefield1.getMirrorBattlefield();
    this.activeBattlefield = battlefield1;
    this.activeSide = battlefield1.mySide;
    this.inactiveSide = battlefield1.opponentSide;

    activeSide.populateDeck(cardNames1);
    inactiveSide.populateDeck(cardNames2);
  }

  public static Card createCardInstance(final String cardName, final ConstType type) {
    switch (type) {
      case MINION:
        return MinionFactory.create(ConstMinion.valueOf(cardName.toUpperCase()));
      case WEAPON:
        return WeaponFactory.create(ConstWeapon.valueOf(cardName.toUpperCase()));
      case SPELL:
        return SpellFactory.create(ConstSpell.valueOf(cardName.toUpperCase()));
      case SECRET:
        return SecretFactory.create(ConstSecret.valueOf(cardName.toUpperCase()));
      default:
        throw new RuntimeException(String.format("Unknown card %s", cardName));
    }
  }

  public static void main(String[] args) {
    logger.info("Starting game");

    final int deck_size = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    ConstMinion MINION = ConstMinion.CHILLWIND_YETI;
    List<Enum> cards1 = Collections.nCopies(deck_size, MINION);
    List<Enum> cards2 = Collections.nCopies(deck_size, MINION);

    final Game gameManager = new Game(
        "testing_id", ConstHero.ANDUIN_WRYNN, ConstHero.JAINA_PROUDMOORE, cards1, cards2);
    gameManager.play();
  }

  public void play() {
    int turn = 1;
    while (!isGameFinished()) {
      logger.debug("Turn #." + turn);
      logger.debug("Round #." + (turn + 1) / 2);
      startTurn();
      playUtilEndTurn();
      CommandLine.println("Your turn is finished.");
      switchTurn();
      turn += 1;
    }
  }

  private boolean isGameFinished() {
    return activeSide.hero.isDead() || inactiveSide.hero.isDead();
  }

  @Override
  public void endTurn() {
    activeSide.endTurn();
  }

  @Override
  public void startTurn() {
    increaseCrystalUpperBound();
    activeSide.startTurn();
    drawCard();
  }

  void playUtilEndTurn() {
    CommandLine.CommandNode leafNode;
    do {
      final CommandLine.CommandNode root = CommandLine.yieldCommands(activeBattlefield);
      for (Map.Entry entry : activeBattlefield.view().entrySet()) {
        CommandLine.println(entry.getKey() + " " + entry.getValue());
      }
      leafNode = CommandLine.run(root);
      play(leafNode);
    } while (!isGameFinished() && !isTurnFinished(leafNode));
  }

  public void switchTurn() {
    activeSide.endTurn();
    if (activeBattlefield == battlefield1) {
      activeBattlefield = battlefield2;
    } else {
      activeBattlefield = battlefield1;
    }

    activeSide = activeBattlefield.mySide;
    inactiveSide = activeBattlefield.opponentSide;
    activeSide.startTurn();
  }

  void increaseCrystalUpperBound() {
    activeSide.hero.manaCrystal().endTurn();
  }

  void drawCard() {
    if (activeSide.deck.isEmpty()) {
      activeSide.takeFatigueDamage();
    } else {
      final Card card = activeSide.deck.top();
      activeSide.hand.add(card);
    }
  }

  void play(final CommandLine.CommandNode leafNode) {
    if (leafNode.option.equals(ConstCommand.END_TURN.toString())) {
      return;
    }

    if (leafNode.option.equals(ConstCommand.USE_HERO_POWER.toString())) {
      // Use hero power without a specific target.
      EffectFactory.pipeEffects(activeSide.hero.getHeroPower(), activeSide.hero);
      consumeCrystal(activeSide.hero.getHeroPower());
      activeSide.hero.heroPowerMovePoints().getTemporaryBuff().increase(-1);
    } else if (leafNode.getParentType().equals(ConstCommand.USE_HERO_POWER.toString())) {
      // Use hero power with a specific target.
      final Creature creature = CommandLine.toTargetCreature(activeBattlefield, leafNode);
      EffectFactory.pipeEffects(activeSide.hero.getHeroPower(), creature);
      consumeCrystal(activeSide.hero.getHeroPower());
      activeSide.hero.heroPowerMovePoints().getTemporaryBuff().increase(-1);
    } else if (leafNode.getParentType().equals(ConstCommand.PLAY_CARD.toString())) {
      final Card card = activeSide.hand.get(leafNode.index);
      //playCard(leafNode.index);
      consumeCrystal(card);
    } else if (leafNode.getParent().getParentType().equals(ConstCommand.MINION_ATTACK.toString())) {
      final Creature attacker = CommandLine.toTargetCreature(activeBattlefield, leafNode.getParent());
      final Creature attackee = CommandLine.toTargetCreature(activeBattlefield, leafNode);
      EffectFactory.AttackFactory.pipePhysicalDamageEffect(attacker, attackee);
      // Cost one move point.
      attacker.attackMovePoints().getTemporaryBuff().increase(-1);
    } else {
      throw new RuntimeException("Unknown option: " + leafNode.option);
    }
  }

  boolean isTurnFinished(final CommandLine.CommandNode node) {
    return node == null || node.option.equals(ConstCommand.END_TURN.toString());
  }

  void consumeCrystal(final Card card) {
    final int cost = card.manaCost().value();
    activeSide.hero.manaCrystal().consume(cost);
  }

  private void checkManaCost(final int index) {
    final Card card = activeSide.hand.get(index);
    final int manaCost = card.manaCost().value();
    Preconditions.checkArgument(
        manaCost <= activeSide.hero.manaCrystal().getCrystal(),
        "Not enough mana for: " + card.cardName());
  }

  public void command(final Command commandProto) {
    Preconditions.checkNotNull(commandProto.getType(), "Command type cannot be empty");
    switch (commandProto.getType()) {
      case END_TURN:
        switchTurn();
        break;
      case PLAY_CARD:
        playCard(
            commandProto.getDoer(), commandProto.getTarget(), commandProto .getBoardPosition());
        break;
      case USE_HERO_POWER:
        useHeroPower(commandProto.getTarget());
        break;
      case ATTACK:
        attack(commandProto.getDoer(), commandProto.getTarget());
        break;
      default:
        throw new RuntimeException("Unknown command type: " + commandProto.getType());
    }
  }

  private void playCard(final Entity doer, final Entity target, final int boardPosition) {
    final Card cardToPlay = TargetParser.toCard(activeSide, doer);
    activeSide.hand.remove(cardToPlay);
    if (cardToPlay instanceof Minion) {
      final Minion minion = (Minion) cardToPlay;
      activeSide.replay.add(null, -1, ConstAction.PLAY_CARD, minion.cardName());
      // Assign game board sequence id to minion.
      activeSide.setSequenceId(minion);
      // TODO:
      if (target.toString().length() == 0) {
        minion.playOnBoard(activeSide.board, boardPosition);
      } else {
        final Creature targetCreature = TargetParser.toCreature(activeSide, target);
        minion.playOnBoard(activeSide.board, boardPosition, targetCreature);
      }
    } else if (cardToPlay instanceof Secret) {
      activeSide.secrets.add((Secret) cardToPlay);
    } else if (cardToPlay instanceof Weapon) {
      activeSide.hero.equip((Weapon) cardToPlay);
    } else if (cardToPlay instanceof Spell) {
      final Spell spell = (Spell) cardToPlay;
      if (target.toString().length() == 0) {
        TriggerFactory.activeTrigger(spell);
      } else {
        final Creature targetCreature = TargetParser.toCreature(activeSide, target);
        TriggerFactory.activeTrigger(spell, targetCreature);
      }
    } else {

    }
  }

  private void useHeroPower(final Entity target) {
    final Spell heroPower = activeSide.hero.getHeroPower();

    final Creature targetCreature;
    if (target.toString().length() != 0) {
      targetCreature = TargetParser.toCreature(activeSide, target);
      EffectFactory.pipeEffects(heroPower, targetCreature);
    } else {
      EffectFactory.pipeEffects(heroPower, activeSide.hero);
    }
  }

  private void attack(final Entity doer, final Entity target) {
    final Creature attacker = TargetParser.toCreature(activeSide, doer);
    final Creature attackee = TargetParser.toCreature(activeSide, target);
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(attacker, attackee);
  }


  public void useHeroPower(final Creature creature) {
    activeSide.hero.useHeroPower(creature);
  }

  private static class TargetParser {

    static Creature toCreature(final Side side, final Entity entity) {
      final Card card = toCard(side, entity);
      Preconditions.checkArgument(card instanceof Creature);
      return (Creature) card;
    }

    static Card toCard(final Side side, final Entity entity) {
      switch (entity.getContainerType()) {
        case HERO:
          return toSide(side, entity).hero;
        case BOARD:
          final Container<Minion> board = toSide(side, entity).board;
          Preconditions.checkArgument(board.size() > entity.getPosition());
          return board.get(entity.getPosition());
        case HAND:
          final Container<Card> hand = toSide(side, entity).hand;
          Preconditions.checkArgument(hand.size() > entity.getPosition());
          return hand.get(entity.getPosition());
        default:
          throw new RuntimeException("Unknown container type: " + entity.getContainerType());
      }
    }

    private static  Side toSide(final Side side, final Entity entity) {
      switch (entity.getSide()) {
        case OWN:
          return side;
        case FOE:
          return side.getOpponentSide();
        default:
          throw new RuntimeException("Unknown type: " + entity.getSide());
      }
    }
  }
}