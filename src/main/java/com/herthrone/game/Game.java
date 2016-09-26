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
import com.herthrone.constant.ConstAction;
import com.herthrone.constant.ConstCommand;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSecret;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstTarget;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.HeroFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.SecretFactory;
import com.herthrone.factory.SpellFactory;
import com.herthrone.factory.TriggerFactory;
import com.herthrone.factory.WeaponFactory;
import com.herthrone.service.Command;
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
      playCard(leafNode.index);
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

  void playCard(final int index) {
    checkManaCost(index);
    final Card card = activeSide.hand.remove(index);
    playCard(card);
  }

  private void checkManaCost(final int index) {
    final Card card = activeSide.hand.get(index);
    final int manaCost = card.manaCost().value();
    Preconditions.checkArgument(
        manaCost <= activeSide.hero.manaCrystal().getCrystal(),
        "Not enough mana for: " + card.cardName());
  }

  public void playCard(final Card card) {
    if (card instanceof Minion) {
      final Minion minion = (Minion) card;
      activeSide.replay.add(null, -1, ConstAction.PLAY_CARD, minion.cardName());
      // Assign game board sequence id to minion.
      activeSide.setSequenceId(minion);
      final int position = activeSide.board.size();
      minion.playOnBoard(activeSide.board, position);
    } else if (card instanceof Secret) {
      activeSide.secrets.add((Secret) card);
    } else if (card instanceof Weapon) {
      activeSide.hero.equip((Weapon) card);
    } else if (card instanceof Spell) {
      TriggerFactory.activeTrigger((Spell) card);
    } else {

    }
  }

  public void playCard(final Card card, final Creature target) {
    if (card instanceof Minion) {
      final Minion minion = (Minion) card;
      activeSide.replay.add(null, -1, ConstAction.PLAY_CARD, minion.cardName());
      // Assign game board sequence id to minion.
      activeSide.setSequenceId(minion);
      final int index = activeSide.board.size();
      minion.playOnBoard(activeSide.board, index, target);
    } else if (card instanceof Spell) {
      TriggerFactory.activeTrigger((Spell) card, target);
    }
  }

  void playCard(final int index, final Minion target) {
    checkManaCost(index);
    final Card card = activeSide.hand.remove(index);

    if (card instanceof Minion) {
      Minion minion = (Minion) card;
      activeSide.board.add(minion);
    } else if (card instanceof Weapon) {
      Weapon weapon = (Weapon) card;
      activeSide.hero.equip(weapon);
    } else if (card instanceof Spell) {
    } else {

    }
  }

  public void command(final Command commandProto) {
    Preconditions.checkNotNull(commandProto.getType(), "Command type cannot be empty");
    switch (commandProto.getType()) {
      case END_TURN:
        switchTurn();
        break;
      case PLAY_CARD:
        playCard(
            commandProto.getDoerId(), commandProto.getTargetId(), commandProto .getBoardPosition());
        break;
      case USE_HERO_POWER:
        useHeroPower(commandProto.getTargetId());
        break;
      case ATTACK:
        attack(commandProto.getDoerId(), commandProto.getTargetId());
        break;
    }
  }

  private void playCard(final String cardId, final String targetId, final int boardPosition) {
    final TargetParser cardToPlayParser = new TargetParser(cardId);
    final Card cardToPlay = cardToPlayParser.toCard(activeSide);
    if (cardToPlay instanceof Minion) {
      final Minion minion = (Minion) cardToPlay;
      activeSide.replay.add(null, -1, ConstAction.PLAY_CARD, minion.cardName());
      // Assign game board sequence id to minion.
      activeSide.setSequenceId(minion);
      // TODO:
      final int index = activeSide.board.size();
      minion.playOnBoard(activeSide.board, index);
    } else if (cardToPlay instanceof Secret) {
      activeSide.secrets.add((Secret) cardToPlay);
    } else if (cardToPlay instanceof Weapon) {
      activeSide.hero.equip((Weapon) cardToPlay);
    } else if (cardToPlay instanceof Spell) {
      TriggerFactory.activeTrigger((Spell) cardToPlay);
    } else {

    }
  }

  private void useHeroPower(final String targetId) {

  }

  private void attack(final String attackerId, final String attackeeId) {
    final TargetParser attackerParser = new TargetParser(attackerId);
    final Creature attacker = attackerParser.toCreature(activeSide);
    final TargetParser attackeeParser = new TargetParser(attackeeId);
    final Creature attackee = attackeeParser.toCreature(activeSide);
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(attacker, attackee);
  }


  public void useHeroPower(final Creature creature) {
    activeSide.hero.useHeroPower(creature);
  }

  private static class TargetParser {

    final ConstTarget targetSide;
    final ConstType targetType;
    final int index;
    private static final String OWN = "o";
    private static final String FOE = "f";
    private static final String HERO = "r";
    private static final String BOARD = "b";
    private static final String HAND = "h";

    TargetParser(final String targetId) {
      final String[] segment = targetId.split(":");
      Preconditions.checkArgument(segment.length == 3);
      this.targetSide = firstCharToTarget(segment[0]);
      this.targetType = firstCharToType(segment[1]);
      this.index = Integer.parseInt(segment[2]);
    }

    private static ConstTarget firstCharToTarget(final String firstChar) {
      Preconditions.checkArgument(firstChar.length() == 1);
      switch (firstChar) {
        case OWN:
          return ConstTarget.OWN;
        case FOE:
          return ConstTarget.FOE;
        default:
          throw new RuntimeException("Unknown target: " + firstChar);
      }
    }

    private static ConstType firstCharToType(final String firstChar) {
      Preconditions.checkArgument(firstChar.length() == 1);
      switch (firstChar) {
        case HAND:
          return ConstType.HAND;
        case BOARD:
          return ConstType.BOARD;
        case HERO:
          return ConstType.HERO;
        default:
          throw new RuntimeException("Unknown type: " + firstChar);
      }
    }

    private Side toSide(final Side side) {
      switch (targetSide) {
        case OWN:
          return side;
        case FOE:
          return side.getOpponentSide();
        default:
          throw new RuntimeException("Unknown type: " + targetSide);
      }
    }

    Creature toCreature(final Side side) {
      final Card card = toCard(side);
      Preconditions.checkArgument(card instanceof Creature);
      return (Creature) card;
    }

    Card toCard(final Side side) {
      switch (targetType) {
        case HERO:
          return toSide(side).hero;
        case BOARD:
          final Container<Minion> board = toSide(side).board;
          Preconditions.checkArgument(board.size() > index);
          return board.get(index);
        case HAND:
          final Container<Card> hand = toSide(side).hand;
          Preconditions.checkArgument(hand.size() > index);
          return hand.get(index);
        default:
          throw new RuntimeException("Unknown type: " + targetType);
      }
    }
  }
}
