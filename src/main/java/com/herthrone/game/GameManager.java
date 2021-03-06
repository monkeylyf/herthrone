package com.herthrone.game;

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
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.HeroFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.SecretFactory;
import com.herthrone.factory.SpellFactory;
import com.herthrone.factory.TriggerFactory;
import com.herthrone.factory.WeaponFactory;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GameManager implements Round {

  private static final Logger logger = Logger.getLogger(GameManager.class.getName());

  private final Battlefield battlefield1;
  private final Battlefield battlefield2;
  public Battlefield activeBattlefield;
  public Side activeSide;
  public Side inactiveSide;

  public GameManager(final ConstHero hero1, final ConstHero hero2,
                     final List<Enum> cardNames1, final List<Enum> cardNames2) {
    // TODO: need to find a place to init deck given cards in a collection.
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

    final GameManager gameManager = new GameManager(ConstHero.ANDUIN_WRYNN, ConstHero.JAINA_PROUDMOORE, cards1, cards2);
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
      minion.playOnBoard(activeSide.board);
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
      minion.playOnBoard(activeSide.board, target);
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

  public void useHeroPower(final Creature creature) {
    activeSide.hero.useHeroPower(creature);
  }

}
