package com.herthrone.game;

import com.google.common.base.Preconditions;
import com.herthrone.base.BaseCard;
import com.herthrone.base.Minion;
import com.herthrone.base.Secret;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.card.factory.Action;
import com.herthrone.card.factory.Factory;
import com.herthrone.card.factory.HeroFactory;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.HeroConfig;
import com.herthrone.constant.ConstCommand;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Created by yifeng on 4/14/16.
 */
public class GameManager {

  public final Factory factory1;
  public final Factory factory2;
  public final Battlefield battlefield1;
  public final Battlefield battlefield2;
  private final Queue<Action> actionQueue;

  Battlefield activeBattlefield;
  private Factory activeFactory;

  public GameManager(final ConstHero hero1, final ConstHero hero2, final List<String> cardNames1, final List<String> cardNames2) {
    // TODO: need to find a place to init deck given cards in a collection.
    this.battlefield1 = new Battlefield(
            HeroFactory.createHeroByName(hero1),
            HeroFactory.createHeroByName(hero2));
    this.battlefield2 = battlefield1.getMirrorBattlefield();
    this.factory1 = new Factory(battlefield1);
    this.factory2 = new Factory(battlefield2);
    this.actionQueue = new LinkedList<>();
    this.activeBattlefield = battlefield1;
    this.activeFactory = factory1;

    final List<BaseCard> cards1 = generateDeck(cardNames1, factory1);
    final List<BaseCard> cards2 = generateDeck(cardNames1, factory2);

    final Spell heroPower1 = generateHeroPower(hero1, factory1);
    final Spell heroPower2 = generateHeroPower(hero2, factory2);

    battlefield1.mySide.setHeroPower(heroPower1);
    battlefield2.mySide.setHeroPower(heroPower2);

    battlefield1.mySide.populateDeck(cards1);
    battlefield2.mySide.populateDeck(cards2);

  }

  private static List<BaseCard> generateDeck(final List<String> cardNames, final Factory factory) {
    return cardNames.stream().map(cardName -> factory.createCardInstance(cardName)).collect(Collectors.toList());
  }

  private static Spell generateHeroPower(final ConstHero hero, final Factory factory) {
    final HeroConfig heroConfig = ConfigLoader.getHeroConfigByName(hero);
    return factory.spellFactory.createHeroPowerByName(heroConfig.getHeroPower());
  }

  public void play() {
    //int turn = 1;
    while (!isGameFinished()) {
      startTurn();
      playUtilEndTurn();
      CommandLine.println("Your turn is finished.");
      switchTurn();
      //turn += 1;
    }
  }

  void startTurn() {
    increaseCrystalUpperBound();
    drawCard();
    activeBattlefield.mySide.board.stream().forEach(minion -> minion.nextRound());
    activeBattlefield.mySide.hero.nextRound();
  }

  void increaseCrystalUpperBound() {
    activeBattlefield.mySide.crystal.nextRound();
  }

  void playUtilEndTurn() {
    CommandLine.CommandNode leafNode = null;
    do {
      final CommandLine.CommandNode root = CommandLine.yieldCommands(activeBattlefield);
      leafNode = CommandLine.run(root);
      play(leafNode);
      clearBoard();
    } while (!isGameFinished() && !isTurnFinished(leafNode));
  }

  boolean isGameFinished() {
    return activeBattlefield.mySide.hero.isDead() || activeBattlefield.opponentSide.hero.isDead();
  }

  boolean isTurnFinished(final CommandLine.CommandNode node) {
    return node == null || node.option.equals(ConstCommand.END_TURN.toString());
  }

  void play(final CommandLine.CommandNode leafNode) {
    if (leafNode.option.equals(ConstCommand.END_TURN.toString())) {
      return;
    }

    if (leafNode.option.equals(ConstCommand.USE_HERO_POWER.toString())) {
      // Use hero power without a specific target.
      activeFactory.effectFactory.getActionsByConfig(activeBattlefield.mySide.heroPower, activeBattlefield.mySide.hero).stream().forEach(Action::act);
      consumeCrystal(activeBattlefield.mySide.heroPower);
    } else if (leafNode.getParentType().equals(ConstCommand.USE_HERO_POWER.toString())) {
      // Use hero power with a specific target.
      final Minion minion = CommandLine.targetToMinion(activeBattlefield, leafNode);
      activeFactory.effectFactory.getActionsByConfig(activeBattlefield.mySide.heroPower, minion).stream().forEach(Action::act);
    } else if (leafNode.getParentType().equals(ConstCommand.PLAY_CARD.toString())) {
      final BaseCard card = activeBattlefield.mySide.board.get(leafNode.index);
      playCard(leafNode.index);
      consumeCrystal(card);
    } else if (leafNode.option.equals(ConstCommand.MOVE_MINION.toString())) {
      final Minion attackee = CommandLine.targetToMinion(activeBattlefield, leafNode);
      final Minion attacker = CommandLine.targetToMinion(activeBattlefield, leafNode.getParent());
      activeFactory.attackFactory.getPhysicalDamageAction(attacker, attackee).act();
    } else {
      throw new RuntimeException("Unknown option: " + leafNode.option.toString());
    }
  }

  void switchTurn() {
    if (activeBattlefield == battlefield1) {
      activeBattlefield = battlefield2;
      activeFactory = factory2;
    } else {
      activeBattlefield = battlefield1;
      activeFactory = factory1;
    }
  }

  void playCard(final BaseCard card) {
    if (card instanceof Minion) {
      Minion minion = (Minion) card;
      activeBattlefield.mySide.board.add(minion);
    } else if (card instanceof Secret) {
      Secret secret = (Secret) card;
      activeBattlefield.mySide.secrets.add(secret);
    } else if (card instanceof Weapon) {
      Weapon weapon = (Weapon) card;
      activeBattlefield.mySide.hero.arm(weapon);
    } else if (card instanceof Spell) {
      Spell spell = (Spell) card;
      //spell.getEffects().
    } else {

    }
  }

  void playCard(final int index) {
    checkManaCost(index);
    final BaseCard card = activeBattlefield.mySide.hand.remove(index);
    playCard(card);
  }

  void clearBoard() {
    clearBoard(activeBattlefield.mySide.board);
    clearBoard(activeBattlefield.opponentSide.board);
  }

  void clearBoard(final Container<Minion> board) {
    for (int i = 0; i < board.size(); ++i) {
      if (board.get(i).isDead()) {
        board.remove(i);
      }
    }
  }


  void playCard(final int index, final Minion target) {
    checkManaCost(index);
    final BaseCard card = activeBattlefield.mySide.hand.remove(index);

    if (card instanceof Minion) {
      Minion minion = (Minion) card;
      activeBattlefield.mySide.board.add(minion);
    } else if (card instanceof Weapon) {
      Weapon weapon = (Weapon) card;
      activeBattlefield.mySide.hero.arm(weapon);
    } else if (card instanceof Spell) {
      Spell spell = (Spell) card;
      //spell.getEffects().
    } else {

    }
  }

  void drawCard() {
    if (activeBattlefield.mySide.deck.isEmpty()) {
      activeBattlefield.mySide.fatigue += 1;
      activeBattlefield.mySide.hero.takeDamage(activeBattlefield.mySide.fatigue);
    } else {
      final BaseCard card = activeBattlefield.mySide.deck.top();
      activeBattlefield.mySide.hand.add(card);
    }
  }

  void consumeCrystal(final BaseCard card) {
    final int cost = card.getCrystalManaCost().getVal();
    activeBattlefield.mySide.crystal.consume(cost);
  }

  void useHeroPower(final Minion minion) {
    final Side side = activeBattlefield.mySide;
    Preconditions.checkArgument(side.heroPowerMovePoints.getVal() > 0, "Cannot use hero power any more in current turn");
    activeFactory.effectFactory.getActionsByConfig(side.heroPower, minion).stream().forEach(Action::act);
    side.heroPowerMovePoints.decrease(1);
  }

  private void checkManaCost(final int index) {
    final BaseCard card = activeBattlefield.mySide.hand.get(index);
    final int manaCost = card.getCrystalManaCost().getVal();
    Preconditions.checkArgument(
            manaCost <= activeBattlefield.mySide.crystal.getCrystal(),
            "Not enough mana to play " + card.getCardName());
  }


  public static void main(String[] args) {
    final int deck_size = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    ConstMinion MINION = ConstMinion.CHILLWIND_YETI;
    List<String> cards1 = Collections.nCopies(deck_size, MINION.toString());
    List<String> cards2 = Collections.nCopies(deck_size, MINION.toString());

    final GameManager gameManager = new GameManager(ConstHero.ANDUIN_WRYNN, ConstHero.JAINA_PROUDMOORE, cards1, cards2);
    gameManager.play();
  }
}
