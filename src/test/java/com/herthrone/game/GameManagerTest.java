package com.herthrone.game;

import com.herthrone.base.BaseCard;
import com.herthrone.base.Minion;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.constant.ConstCommand;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by yifengliu on 5/17/16.
 */
@RunWith(JUnit4.class)
public class GameManagerTest {

  private static final int DECK_SIZE = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
  private static final int HAND_SIZE = Integer.parseInt(ConfigLoader.getResource().getString("hand_max_capacity"));
  private static ConstMinion MINION = ConstMinion.CHILLWIND_YETI;
  private GameManager gameManager;
  private ConstHero hero1 = ConstHero.ANDUIN_WRYNN;
  private ConstHero hero2 = ConstHero.JAINA_PROUDMOORE;

  private Side mySide;
  private Side opponentSide;

  @Before
  public void setUp() {
    List<String> cards1 = Collections.nCopies(DECK_SIZE, MINION.toString());
    List<String> cards2 = Collections.nCopies(DECK_SIZE, MINION.toString());

    gameManager = new GameManager(ConstHero.ANDUIN_WRYNN, ConstHero.JAINA_PROUDMOORE, cards1, cards2);

    mySide = gameManager.battlefield1.mySide;
    opponentSide = gameManager.battlefield1.opponentSide;

    CommandLine.turnOffStdout();

  }

  private void jumpIntoRoundFour() {
    // At least 4 crystals so YETI can be played and show up as options.
    for (int i = 0; i < 8; ++i) {
      gameManager.drawCard();
      gameManager.activeBattlefield.mySide.crystal.nextRound();
      gameManager.switchTurn();
    }
  }

  @Test
  public void testInitDeckSize() {
    assertThat(mySide.deck.size()).isEqualTo(DECK_SIZE);
    assertThat(opponentSide.deck.size()).isEqualTo(DECK_SIZE);
  }

  @Test
  public void testInitCardsInDeck() {
    assertThat(mySide.deck.top() instanceof Minion).isTrue();
  }

  @Test
  public void testInitHero() {
    assertThat(mySide.hero.getCardName()).isEqualTo(hero1.toString());
    assertThat(opponentSide.hero.getCardName()).isEqualTo(hero2.toString());
  }

  @Test
  public void testInitCrystal() {
    // TODO: need to define turn/round.
    // turn and start and end. Different events can be triggered by starting the turn
    // and ending the turn. Crystal should increase one when starting a new turn, not ending
    // previous turn.
    assertThat(mySide.crystal.getCrystal()).isEqualTo(1);
    assertThat(opponentSide.crystal.getCrystal()).isEqualTo(1);
  }

  @Test
  public void testInitHandSize() {
    assertThat(mySide.hand.size()).isEqualTo(0);
    assertThat(opponentSide.hand.size()).isEqualTo(0);
  }

  @Test
  public void testInitBoardSize() {
    assertThat(mySide.board.size()).isEqualTo(0);
    assertThat(opponentSide.board.size()).isEqualTo(0);
  }

  @Test
  public void testInitSecretSize() {
    assertThat(mySide.secrets.size()).isEqualTo(0);
    assertThat(opponentSide.secrets.size()).isEqualTo(0);
  }

  @Test
  public void testInitHeroPowerMovePoints() {
    assertThat(mySide.heroPowerMovePoints.getVal()).isEqualTo(1);
    assertThat(opponentSide.heroPowerMovePoints.getVal()).isEqualTo(1);
  }

  @Test
  public void testDrawCard() {
    assertThat(mySide.hand.size()).isEqualTo(0);
    gameManager.drawCard();
    assertThat(mySide.hand.size()).isEqualTo(1);
  }

  @Test
  public void testOverdraw() {
    assertThat(DECK_SIZE).isGreaterThan(HAND_SIZE);
    while (!mySide.deck.isEmpty()) {
      gameManager.drawCard();
    }
    assertThat(mySide.hand.size()).isEqualTo(HAND_SIZE);
  }

  @Test
  public void testFatigue() {
    while (!mySide.deck.isEmpty()) {
      gameManager.drawCard();
    }

    assertThat(mySide.hero.getHealthLoss()).isEqualTo(0);
    int damage = 0;
    final int repeat = 10;
    for (int i = 1; i <= repeat; ++i) {
      final int healthBeforeDrawCard = mySide.hero.getHealthAttr().getVal();
      gameManager.drawCard();
      final int healthAfterDrawCard = mySide.hero.getHealthAttr().getVal();

      assertThat(healthBeforeDrawCard - healthAfterDrawCard).isEqualTo(i);

      damage += i;
    }

    assertThat(mySide.hero.getHealthLoss()).isEqualTo(damage);
    assertThat(mySide.hero.isDead()).isTrue();
  }

  @Test
  public void testPlayMinionCardWithProperCrystal() {
    gameManager.drawCard();

    assertThat(mySide.hand.get(0) instanceof Minion).isTrue();
    assertThat(mySide.hand.get(0).getCardName()).isEqualTo(MINION.toString());
    assertThat(mySide.board.size()).isEqualTo(0);

    final BaseCard card = mySide.hand.get(0);
    final int requiredCrystalCost = card.getCrystalManaCost().getVal();

    while (mySide.crystal.getCrystal() < requiredCrystalCost) {
      try {
        gameManager.playCard(0);
      } catch (IllegalArgumentException expected) {
        assertThat(expected).hasMessage("Not enough mana to play " + card.getCardName());
      }

      mySide.crystal.nextRound();
    }

    gameManager.playCard(0);
    assertThat(mySide.board.size()).isEqualTo(1);
    assertThat(mySide.board.get(0).getCardName()).isEqualTo(MINION.toString());
  }

  @Test
  public void testUseHeroPower() {
    final int damage = 2;
    opponentSide.hero.takeDamage(damage);
    assertThat(opponentSide.hero.getHealthLoss()).isEqualTo(damage);
    gameManager.useHeroPower(opponentSide.hero);
    assertThat(opponentSide.hero.getHealthLoss()).isEqualTo(0);

    try {
      gameManager.useHeroPower(opponentSide.hero);
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("Cannot use hero power any more in current turn");
    }
  }

  @Test
  public void testSwitchTurn() {
    final Side previousMySide = mySide;
    final Side previousOpponentSide = opponentSide;

    gameManager.switchTurn();

    assertThat(gameManager.activeBattlefield.mySide).isEqualTo(previousOpponentSide);
    assertThat(gameManager.activeBattlefield.opponentSide).isEqualTo(previousMySide);
  }

  @Test
  public void testGenerateCommandNodes() throws IOException {
    jumpIntoRoundFour();
    final int numOfMyMinions = 2;
    final int numOfOpponentMinions = 1;
    populateBoardWithMinions(numOfMyMinions, numOfOpponentMinions);

    assertThat(mySide.board.size()).isEqualTo(numOfMyMinions);
    assertThat(opponentSide.board.size()).isEqualTo(numOfOpponentMinions);

    final CommandLine.CommandNode myRoot = CommandLine.yieldCommands(gameManager.activeBattlefield);
    checkCommands(myRoot, numOfMyMinions);

    // Switch side.
    gameManager.switchTurn();

    final CommandLine.CommandNode opponentRoot = CommandLine.yieldCommands(gameManager.activeBattlefield);
    checkCommands(opponentRoot, numOfOpponentMinions);
  }

  @Test
  public void testCommandNodes() {
    final int numOfMyMinions = 2;
    final int numOfOpponentMinions = 1;
    populateBoardWithMinions(numOfMyMinions, numOfOpponentMinions);

    jumpIntoRoundFour();

    final CommandLine.CommandNode myRoot = CommandLine.yieldCommands(gameManager.activeBattlefield);
    // Choose option 1 which is play card.
    final InputStream playCardInput = new ByteArrayInputStream("1\n1".getBytes());
    final CommandLine.CommandNode playCardLeaf = CommandLine.run(myRoot, playCardInput);
    assertThat(playCardLeaf.getParentType()).isEqualTo(ConstCommand.PLAY_CARD.toString());
    assertThat(playCardLeaf.option).isEqualTo(ConstMinion.CHILLWIND_YETI.toString());

    // Choose option 2 which is move minion.
    final InputStream moveMinionInput = new ByteArrayInputStream("2\n1\n1".getBytes());
    final CommandLine.CommandNode moveMinionLeaf = CommandLine.run(myRoot, moveMinionInput);
    assertThat(moveMinionLeaf.getParentType()).isEqualTo(ConstMinion.CHILLWIND_YETI.toString());
    assertThat(moveMinionLeaf.index).isEqualTo(0); // TODO: 1 points to first minion, which index is 0...

    // Choose option 3 which is use hero power.
    final InputStream useHeroPowerInput = new ByteArrayInputStream("3\n1".getBytes());
    final CommandLine.CommandNode heroPowerLeafNode = CommandLine.run(myRoot, useHeroPowerInput);
    assertThat(heroPowerLeafNode.getParentType()).isEqualTo(ConstCommand.USE_HERO_POWER.toString());
    assertThat(heroPowerLeafNode.option).startsWith("{hero=");
    assertThat(heroPowerLeafNode.index).isEqualTo(-1); // TODO: 1 points to own hero, which index is -1...

    // Choose option 4 which is end turn.
    final InputStream endTurnInput = new ByteArrayInputStream("4".getBytes());
    CommandLine.CommandNode endTurnLeafNode = CommandLine.run(myRoot, endTurnInput);
    assertThat(endTurnLeafNode.option).isEqualTo(ConstCommand.END_TURN.toString());
  }

  private void populateBoardWithMinions(final int numOfOwnMinions, final int numOfOpponentMinions) {
    // Directly move minions from deck to board to avoid waiting the crystals growing one by one.
    for (int i = 0; i < numOfOwnMinions; ++i) {
      final Minion minion = (Minion) mySide.deck.top();
      minion.nextRound();
      mySide.board.add(minion);
    }

    for (int i = 0; i < numOfOpponentMinions; ++i) {
      final Minion minion = (Minion) opponentSide.deck.top();
      minion.nextRound();
      opponentSide.board.add(minion);
    }
  }

  private void checkCommands(CommandLine.CommandNode root, final int numOfMinions) {
    assertThat(root.childOptions.size()).isEqualTo(4);
    List<String> childOptions = root.childOptions.stream().map(option -> option.option).collect(Collectors.toList());
    assertThat(childOptions).containsExactly(
            ConstCommand.END_TURN.toString(),
            ConstCommand.MOVE_MINION.toString(),
            ConstCommand.PLAY_CARD.toString(),
            ConstCommand.USE_HERO_POWER.toString());

    for (CommandLine.CommandNode node : root.childOptions) {
      final String optionName = node.option;
      if (optionName.equals(ConstCommand.END_TURN.toString())) {
        assertThat(node.childOptions.size()).isEqualTo(0);
      } else if (optionName.equals(ConstCommand.USE_HERO_POWER.toString())) {
        // 1(own hero) + 2(own minions) + 1(opponent hero) + 1(opponent minion) = 5
        assertThat(node.childOptions.size()).isEqualTo(5);
      } else if (optionName.equals(ConstCommand.MOVE_MINION)) {
        assertThat(node.childOptions.size()).isEqualTo(numOfMinions);
      } else if (optionName.equals(ConstCommand.PLAY_CARD)) {
        assertThat(node.childOptions.size()).isEqualTo(0);
      }
    }
  }
}