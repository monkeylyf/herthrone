package com.herthrone.game;

import com.herthrone.base.Card;
import com.herthrone.base.Minion;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.constant.ConstCommand;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.factory.HeroFactory;
import com.herthrone.object.ManaCrystal;
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

@RunWith(JUnit4.class)
public class GameManagerTest {

  private static final int DECK_SIZE = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
  private static final int HAND_SIZE = Integer.parseInt(ConfigLoader.getResource().getString("hand_max_capacity"));
  private static ConstMinion MINION = ConstMinion.CHILLWIND_YETI;
  private Game gm;
  private ConstHero hero1 = ConstHero.ANDUIN_WRYNN;
  private ConstHero hero2 = ConstHero.JAINA_PROUDMOORE;

  @Before
  public void setUp() {
    final List<Enum> cards = Collections.nCopies(DECK_SIZE, MINION);
    gm = new Game("id", ConstHero.ANDUIN_WRYNN, ConstHero.JAINA_PROUDMOORE, cards, cards);
    CommandLine.turnOffStdout();
  }

  @Test
  public void testInitDeckSize() {
    assertThat(gm.activeSide.deck.size()).isEqualTo(DECK_SIZE);
    assertThat(gm.inactiveSide.deck.size()).isEqualTo(DECK_SIZE);
  }

  @Test
  public void testInitCardsInDeck() {
    assertThat(gm.activeSide.deck.top() instanceof Minion).isTrue();
  }

  @Test
  public void testInitHero() {
    assertThat(gm.activeSide.hero.cardName()).isEqualTo(hero1.toString());
    assertThat(gm.inactiveSide.hero.cardName()).isEqualTo(hero2.toString());
  }

  @Test
  public void testInitCrystal() {
    final ManaCrystal manaCrystal1 = gm.activeSide.hero.manaCrystal();
    final ManaCrystal manaCrystal2 = gm.inactiveSide.hero.manaCrystal();

    assertThat(manaCrystal1.getCrystal()).isEqualTo(0);
    assertThat(manaCrystal2.getCrystal()).isEqualTo(0);

    manaCrystal1.startTurn();
    assertThat(manaCrystal1.getCrystal()).isEqualTo(1);

    manaCrystal2.startTurn();
    assertThat(manaCrystal2.getCrystal()).isEqualTo(1);
  }

  @Test
  public void testInitHandSize() {
    assertThat(gm.activeSide.hand.size()).isEqualTo(0);
    assertThat(gm.inactiveSide.hand.size()).isEqualTo(0);
  }

  @Test
  public void testInitBoardSize() {
    assertThat(gm.activeSide.board.size()).isEqualTo(0);
    assertThat(gm.inactiveSide.board.size()).isEqualTo(0);
  }

  @Test
  public void testInitSecretSize() {
    assertThat(gm.activeSide.secrets.size()).isEqualTo(0);
    assertThat(gm.inactiveSide.secrets.size()).isEqualTo(0);
  }

  @Test
  public void testInitHeroPowerMovePoints() {
    assertThat(gm.activeSide.hero.heroPowerMovePoints().value()).isEqualTo(1);
    assertThat(gm.inactiveSide.hero.heroPowerMovePoints().value()).isEqualTo(1);
  }

  @Test
  public void testDrawCard() {
    assertThat(gm.activeSide.hand.size()).isEqualTo(0);
    gm.drawCard();
    assertThat(gm.activeSide.hand.size()).isEqualTo(1);
  }

  @Test
  public void testOverdraw() {
    assertThat(DECK_SIZE).isGreaterThan(HAND_SIZE);
    while (!gm.activeSide.deck.isEmpty()) {
      gm.drawCard();
    }
    assertThat(gm.activeSide.hand.size()).isEqualTo(HAND_SIZE);
  }

  @Test
  public void testFatigue() {
    while (!gm.activeSide.deck.isEmpty()) {
      gm.drawCard();
    }

    assertThat(gm.activeSide.hero.healthLoss()).isEqualTo(0);
    int damage = 0;
    final int repeat = 10;
    for (int i = 1; i <= repeat; ++i) {
      final int healthBeforeDrawCard = gm.activeSide.hero.health().value();
      gm.drawCard();
      final int healthAfterDrawCard = gm.activeSide.hero.health().value();
      assertThat(healthBeforeDrawCard - healthAfterDrawCard).isEqualTo(i);
      damage += i;
    }

    assertThat(gm.activeSide.hero.healthLoss()).isEqualTo(damage);
    assertThat(gm.activeSide.hero.isDead()).isTrue();
  }

  @Test
  public void testPlayMinionCardWithProperCrystal() {
    gm.drawCard();

    assertThat(gm.activeSide.hand.get(0) instanceof Minion).isTrue();
    assertThat(gm.activeSide.hand.get(0).cardName()).isEqualTo(MINION.toString());
    assertThat(gm.activeSide.board.size()).isEqualTo(0);

    final Card card = gm.activeSide.hand.get(0);
    final int requiredCrystalCost = card.manaCost().value();

    while (gm.activeSide.hero.manaCrystal().getCrystal() < requiredCrystalCost) {
      try {
        gm.activeSide.replay.startTurn();
        gm.playCard(0);
      } catch (IllegalArgumentException expected) {
        assertThat(expected).hasMessage("Not enough mana for: " + card.cardName());
      }

      gm.activeSide.hero.manaCrystal().startTurn();
    }

    gm.activeSide.replay.startTurn();
    gm.playCard(0);
    assertThat(gm.activeSide.board.size()).isEqualTo(1);
    assertThat(gm.activeSide.board.get(0).cardName()).isEqualTo(MINION.toString());
  }

  @Test
  public void testUseHeroPower() {
    final int damage = 2;
    gm.inactiveSide.hero.takeDamage(damage);
    assertThat(gm.inactiveSide.hero.healthLoss()).isEqualTo(damage);
    gm.useHeroPower(gm.inactiveSide.hero);
    assertThat(gm.inactiveSide.hero.healthLoss()).isEqualTo(0);

    try {
      gm.useHeroPower(gm.inactiveSide.hero);
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage(HeroFactory.HERO_POWER_ERROR_MESSAGE);
    }
  }

  @Test
  public void testSwitchTurn() {
    final Side previousMySide = gm.activeSide;
    final Side previousOpponentSide = gm.inactiveSide;

    gm.switchTurn();

    assertThat(gm.activeSide).isEqualTo(previousOpponentSide);
    assertThat(gm.inactiveSide).isEqualTo(previousMySide);
  }

  @Test
  public void testGenerateCommandNodes() throws IOException {
    jumpIntoRoundFour();
    final int numOfMyMinions = 2;
    final int numOfOpponentMinions = 1;
    populateBoardWithMinions(numOfMyMinions, numOfOpponentMinions);

    assertThat(gm.activeSide.board.size()).isEqualTo(numOfMyMinions);
    assertThat(gm.inactiveSide.board.size()).isEqualTo(numOfOpponentMinions);

    final CommandLine.CommandNode myRoot = CommandLine.yieldCommands(gm.activeBattlefield);
    checkCommands(myRoot, numOfMyMinions);

    // Switch side.
    gm.switchTurn();

    final CommandLine.CommandNode opponentRoot = CommandLine.yieldCommands(gm.activeBattlefield);
    checkCommands(opponentRoot, numOfOpponentMinions);
  }

  private void jumpIntoRoundFour() {
    // At least 4 crystals so YETI can be played and show up as options.
    for (int i = 0; i < 8; ++i) {
      gm.drawCard();
      gm.activeSide.hero.manaCrystal().startTurn();
      gm.switchTurn();
    }
  }

  private void populateBoardWithMinions(final int numOfOwnMinions, final int numOfOpponentMinions) {
    // Directly move minions from deck to board to avoid waiting the crystals growing one by one.
    gm.startTurn();
    for (int i = 0; i < numOfOwnMinions; ++i) {
      final Minion minion = (Minion) gm.activeSide.deck.top();
      gm.playCard(minion);
    }

    gm.switchTurn();
    for (int i = 0; i < numOfOpponentMinions; ++i) {
      final Minion minion = (Minion) gm.inactiveSide.deck.top();
      gm.playCard(minion);
    }
    gm.switchTurn();
  }

  private void checkCommands(CommandLine.CommandNode root, final int numOfMinions) {
    assertThat(root.childOptions.size()).isEqualTo(4);
    List<String> childOptions = root.childOptions.stream().map(option -> option.option).collect(Collectors.toList());
    assertThat(childOptions).containsExactly(ConstCommand.END_TURN.toString(), ConstCommand.MINION_ATTACK.toString(), ConstCommand.PLAY_CARD.toString(), ConstCommand.USE_HERO_POWER.toString());

    for (CommandLine.CommandNode node : root.childOptions) {
      final String optionName = node.option;
      if (optionName.equals(ConstCommand.END_TURN.toString())) {
        assertThat(node.childOptions.size()).isEqualTo(0);
      } else if (optionName.equals(ConstCommand.USE_HERO_POWER.toString())) {
        // 1(own hero) + 2(own minions) + 1(opponent hero) + 1(opponent minion) = 5
        assertThat(node.childOptions.size()).isEqualTo(5);
      } else if (optionName.equals(ConstCommand.MINION_ATTACK)) {
        assertThat(node.childOptions.size()).isEqualTo(numOfMinions);
      } else if (optionName.equals(ConstCommand.PLAY_CARD)) {
        assertThat(node.childOptions.size()).isEqualTo(0);
      }
    }
  }

  @Test
  public void testCommandNodes() {
    final int numOfMyMinions = 2;
    final int numOfOpponentMinions = 1;
    populateBoardWithMinions(numOfMyMinions, numOfOpponentMinions);

    jumpIntoRoundFour();

    final CommandLine.CommandNode myRoot = CommandLine.yieldCommands(gm.activeBattlefield);
    // Choose option 1 which is play card.
    final InputStream playCardInput = new ByteArrayInputStream("1\n1".getBytes());
    final CommandLine.CommandNode playCardLeaf = CommandLine.run(myRoot, playCardInput);
    assertThat(playCardLeaf.getParentType()).isEqualTo(ConstCommand.PLAY_CARD.toString());
    assertThat(playCardLeaf.option).isEqualTo(gm.activeSide.hand.get(0).view().toString());

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
}