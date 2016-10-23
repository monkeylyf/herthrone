package com.herthrone.game;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
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
import com.herthrone.constant.ConstSelect;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstTarget;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.SecretFactory;
import com.herthrone.factory.SpellFactory;
import com.herthrone.factory.TriggerFactory;
import com.herthrone.factory.WeaponFactory;
import com.herthrone.service.Command;
import com.herthrone.service.Entity;
import com.herthrone.service.StartGameSetting;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Game implements Round {

  private static final Logger logger = Logger.getLogger(Game.class.getName());
  private static final Map<String, Game> gamePool = new HashMap<>();
  public static final Range<Integer> SINGLE_COMMAND = Range.closed(1, 1);

  private final String gameId;
  public Side activeSide;
  public Side inactiveSide;

  public static String StartGame(final List<StartGameSetting> settings) {
    // For now it only support duel game.
    Preconditions.checkArgument(settings.size() == 2);
    // Concatenate user IDs to form a game ID in alphabetical order.
    final List<String> IdSequence = settings.stream()
        .map(StartGameSetting::getUserId)
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
    try {
      return ConstMinion.valueOf(upperCardName);
    } catch (IllegalArgumentException error) {
    }
    try {
      return ConstSpell.valueOf(upperCardName);
    } catch (IllegalArgumentException error) {
    }
    try {
      return ConstWeapon.valueOf(upperCardName);
    } catch (IllegalArgumentException error) {
    }

    throw new IllegalArgumentException("Unknown card: " + cardName);
  }

  public Game(final String gameId, final ConstHero hero1, final ConstHero hero2,
              final List<Enum> cardNames1, final List<Enum> cardNames2) {
    // TODO: need to find a place to init deck given cards in a collection.
    this.gameId = gameId;
    this.activeSide = Side.createSidePair(hero1, cardNames1, hero2, cardNames2);
    this.inactiveSide = activeSide.getFoeSide();
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
    final List<Enum> cards1 = Collections.nCopies(deck_size, MINION);
    final List<Enum> cards2 = Collections.nCopies(deck_size, MINION);
    new Game("demo", ConstHero.ANDUIN_WRYNN, ConstHero.JAINA_PROUDMOORE, cards1, cards2).play();
  }

  public void play() {
    pickStartingHand();
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

  private void pickStartingHand() {
    pickStartingHand(true);
    switchActiveSide();
    pickStartingHand(false);
    switchActiveSide();
  }

  private void pickStartingHand(final boolean isFirstToPlay) {
    Preconditions.checkArgument(activeSide.hand.isEmpty());
    logger.info("Picking starting hand...");
    final List<Card> startingHandCandidates = createStartingHandCandidates(isFirstToPlay);
    final CommandLine.CommandNode root = CommandLine.yieldCommands(startingHandCandidates);
    final Set<Integer> chosenCardIndexes = CommandLine.run(
        root, Range.closed(0, startingHandCandidates.size())).stream()
        .map(node -> node.index).collect(Collectors.toSet());
    // Add chosen cards to player's hand and put unchosen ones back to deck.
    for (int i = 0; i < startingHandCandidates.size(); ++i) {
      if (chosenCardIndexes.contains(i)) {
        activeSide.hand.add(startingHandCandidates.get(i));
      } else {
        activeSide.deck.add(startingHandCandidates.get(i));
      }
    }
    // Shuffle deck no matter there are cards been put back or not.
    activeSide.deck.shuffle();
    // TODO: add coin to the hand of player who starts second.
    if (!isFirstToPlay) {
      activeSide.hand.add(SpellFactory.create(ConstSpell.THE_COIN));
    }
  }

  private List<Card> createStartingHandCandidates(final boolean firstToStart) {
    final String key = (firstToStart) ? "first_starting_hand_size" : "second_starting_hand_size";
    final int firstStartingHandSize = Integer.parseInt(ConfigLoader.getResource().getString(key));
    final List<Card> startingHandCandidates = new ArrayList<>();
    for (int i = 0; i < firstStartingHandSize; ++i) {
      // In gameplay, it seems to pick x random cards from the deck. Here since the deck
      // is already shuffle multiple times so taking the top x is equal to random picks.
      startingHandCandidates.add(activeSide.deck.top());
    }
    return startingHandCandidates;
  }

  private boolean isGameFinished() {
    return activeSide.hero.isDead() || inactiveSide.hero.isDead();
  }

  @Override
  public void endTurn() {
    activeSide.hero.manaCrystal().endTurn();
    activeSide.endTurn();
  }

  @Override
  public void startTurn() {
    activeSide.hero.manaCrystal().startTurn();
    activeSide.startTurn();
  }

  private void playUtilEndTurn() {
    CommandLine.CommandNode leafNode;
    do {
      final CommandLine.CommandNode root = CommandLine.yieldCommands(activeSide);
      printPrettyView(activeSide.view());
      final List<CommandLine.CommandNode> optionsNodes = CommandLine.run(root, SINGLE_COMMAND);
      Preconditions.checkArgument(optionsNodes.size() == 1);
      leafNode = optionsNodes.get(0);
      play(leafNode);
    } while (!isGameFinished() && !isTurnFinished(leafNode));
  }

  private static void printPrettyView(final Map<String, String> view) {
    printPrettyView(view, ConstTarget.FOE.toString());
    printPrettyView(view, ConstTarget.OWN.toString());
  }

  private static void printPrettyView(final Map<String, String> view, final String prefix) {
    for (Map.Entry<String, String> entry : view.entrySet()) {
      final String key = entry.getKey();
      if (key.startsWith(prefix)) {
        CommandLine.println(key + ": " + entry.getValue());
      }
    }
    CommandLine.println();
  }

  public void switchTurn() {
    activeSide.endTurn();
    switchActiveSide();
    activeSide.startTurn();
  }

  private void switchActiveSide() {
    activeSide = activeSide.getFoeSide();
    inactiveSide = activeSide.getFoeSide();
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
      final Creature creature = CommandLine.toTargetCreature(activeSide, leafNode);
      EffectFactory.pipeEffects(activeSide.hero.getHeroPower(), creature);
      consumeCrystal(activeSide.hero.getHeroPower());
      activeSide.hero.heroPowerMovePoints().getTemporaryBuff().increase(-1);
    } else if (leafNode.getParentType().equals(ConstCommand.PLAY_CARD.toString())) {
      final Card card = activeSide.hand.get(leafNode.index);
      //playCard(leafNode.index);
      consumeCrystal(card);
    } else if (leafNode.getParent().getParentType().equals(ConstCommand.MINION_ATTACK.toString())) {
      final Creature attacker = CommandLine.toTargetCreature(activeSide, leafNode.getParent());
      final Creature attackee = CommandLine.toTargetCreature(activeSide, leafNode);
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
      if (isProtobufMessageEmpty(target.toByteArray())) {
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
      if (isProtobufMessageEmpty(target.toByteArray())) {
        TriggerFactory.activeTrigger(spell);
      } else {
        final Creature targetCreature = TargetParser.toCreature(activeSide, target);
        TriggerFactory.activeTrigger(spell, targetCreature);
      }
    } else {

    }
  }

  public static boolean isProtobufMessageEmpty(byte[] message) {
    return message.length == 0;
  }

  private void useHeroPower(final Entity target) {
    final Spell heroPower = activeSide.hero.getHeroPower();
    if (isProtobufMessageEmpty(target.toByteArray())) {
      TriggerFactory.activeTrigger(activeSide.hero.getHeroPower());
    } else {
      Preconditions.checkArgument(!heroPower.getSelectTargetConfig().select.equals(
          ConstSelect.PASSIVE));
      final Creature targetCreature = TargetParser.toCreature(activeSide, target);
      TriggerFactory.activeTrigger(activeSide.hero.getHeroPower(), targetCreature);
    }
  }

  private void attack(final Entity doer, final Entity target) {
    final Creature attacker = TargetParser.toCreature(activeSide, doer);
    final Creature attackee = TargetParser.toCreature(activeSide, target);
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(attacker, attackee);
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
          return side.getFoeSide();
        default:
          throw new RuntimeException("Unknown type: " + entity.getSide());
      }
    }
  }
}
