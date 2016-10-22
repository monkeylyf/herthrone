package com.herthrone.game;

import com.herthrone.BaseGame;
import com.herthrone.base.Card;
import com.herthrone.base.Minion;
import com.herthrone.constant.ConstCommand;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.factory.HeroFactory;
import com.herthrone.object.ManaCrystal;
import com.herthrone.service.BoardSide;
import com.herthrone.service.ContainerType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class GameManagerTest extends BaseGame {

  private ConstHero anduin = ConstHero.ANDUIN_WRYNN;
  private ConstHero jaina = ConstHero.JAINA_PROUDMOORE;

  @Before
  public void setUp() {
    setUpGame(anduin, jaina);
    CommandLine.turnOffStdout();
  }

  @Test
  public void testInitDeckSize() {
    assertThat(game.activeSide.deck.size()).isEqualTo(DECK_SIZE);
    assertThat(game.inactiveSide.deck.size()).isEqualTo(DECK_SIZE);
  }

  @Test
  public void testInitCardsInDeck() {
    assertThat(game.activeSide.deck.top() instanceof Minion).isTrue();
  }

  @Test
  public void testInitHero() {
    assertThat(game.activeSide.hero.cardName()).isEqualTo(anduin.toString());
    assertThat(game.inactiveSide.hero.cardName()).isEqualTo(jaina.toString());
  }

  @Test
  public void testInitCrystal() {
    final ManaCrystal manaCrystal1 = game.activeSide.hero.manaCrystal();
    final ManaCrystal manaCrystal2 = game.inactiveSide.hero.manaCrystal();

    assertThat(manaCrystal1.getCrystal()).isEqualTo(0);
    assertThat(manaCrystal2.getCrystal()).isEqualTo(0);

    manaCrystal1.startTurn();
    assertThat(manaCrystal1.getCrystal()).isEqualTo(1);

    manaCrystal2.startTurn();
    assertThat(manaCrystal2.getCrystal()).isEqualTo(1);
  }

  @Test
  public void testInitHandSize() {
    assertThat(game.activeSide.hand.size()).isEqualTo(0);
    assertThat(game.inactiveSide.hand.size()).isEqualTo(0);
  }

  @Test
  public void testInitBoardSize() {
    assertThat(game.activeSide.board.size()).isEqualTo(0);
    assertThat(game.inactiveSide.board.size()).isEqualTo(0);
  }

  @Test
  public void testInitSecretSize() {
    assertThat(game.activeSide.secrets.size()).isEqualTo(0);
    assertThat(game.inactiveSide.secrets.size()).isEqualTo(0);
  }

  @Test
  public void testInitHeroPowerMovePoints() {
    assertThat(game.activeSide.hero.heroPowerMovePoints().value()).isEqualTo(1);
    assertThat(game.inactiveSide.hero.heroPowerMovePoints().value()).isEqualTo(1);
  }

  @Test
  public void testDrawCard() {
    assertThat(game.activeSide.hand.size()).isEqualTo(0);
    game.activeSide.drawCard();
    assertThat(game.activeSide.hand.size()).isEqualTo(1);
  }

  @Test
  public void testOverdraw() {
    assertThat(DECK_SIZE).isGreaterThan(HAND_SIZE);
    while (!game.activeSide.deck.isEmpty()) {
      game.activeSide.drawCard();
    }
    assertThat(game.activeSide.hand.size()).isEqualTo(HAND_SIZE);
  }

  @Test
  public void testFatigue() {
    while (!game.activeSide.deck.isEmpty()) {
      game.activeSide.drawCard();
    }

    assertThat(game.activeSide.hero.healthLoss()).isEqualTo(0);
    int damage = 0;
    final int repeat = 10;
    for (int i = 1; i <= repeat; ++i) {
      final int healthBeforeDrawCard = game.activeSide.hero.health().value();
      game.activeSide.drawCard();
      final int healthAfterDrawCard = game.activeSide.hero.health().value();
      assertThat(healthBeforeDrawCard - healthAfterDrawCard).isEqualTo(i);
      damage += i;
    }

    assertThat(game.activeSide.hero.healthLoss()).isEqualTo(damage);
    assertThat(game.activeSide.hero.isDead()).isTrue();
  }

  @Test
  public void testPlayMinionCardWithProperCrystal() {
    // TODO: check crystal cost is disabled for now so this test voids.
    game.activeSide.drawCard();

    assertThat(game.activeSide.hand.get(0) instanceof Minion).isTrue();
    assertThat(game.activeSide.hand.get(0).cardName()).isEqualTo(YETI.toString());
    assertThat(game.activeSide.board.size()).isEqualTo(0);

    final Card card = game.activeSide.hand.get(0);
    final int requiredCrystalCost = card.manaCost().value();

    while (game.activeSide.hero.manaCrystal().getCrystal() < requiredCrystalCost) {
      game.activeSide.replay.startTurn();
      game.activeSide.hero.manaCrystal().startTurn();
    }

    game.activeSide.replay.startTurn();
    minion.addToHandAndPlay(ConstMinion.CHILLWIND_YETI);
    assertThat(game.activeSide.board.size()).isEqualTo(1);
    assertThat(game.activeSide.board.get(0).cardName()).isEqualTo(YETI.toString());
  }

  @Test
  public void testUseHeroPower() {
    final int damage = 2;
    game.inactiveSide.hero.takeDamage(damage);
    assertThat(game.inactiveSide.hero.healthLoss()).isEqualTo(damage);
    heroPower.use(BoardSide.FOE, ContainerType.HERO, 0);
    assertThat(game.inactiveSide.hero.healthLoss()).isEqualTo(0);

    try {
      heroPower.use(BoardSide.FOE, ContainerType.HERO, 0);
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage(HeroFactory.HERO_POWER_ERROR_MESSAGE);
    }
  }

  @Test
  public void testSwitchTurn() {
    final Side previousMySide = game.activeSide;
    final Side previousFoeSide = game.inactiveSide;

    game.switchTurn();

    assertThat(game.activeSide).isEqualTo(previousFoeSide);
    assertThat(game.inactiveSide).isEqualTo(previousMySide);
  }

  @Test
  public void testGenerateCommandNodes() throws IOException {
    jumpIntoRoundFour();
    final int numOfMyMinions = 2;
    final int numOfFoeMinions = 1;
    populateBoardWithMinions(numOfMyMinions, numOfFoeMinions);
    // populateBoardWithMinions actually have its own turn and now it's turn #5.

    assertThat(game.activeSide.board.size()).isEqualTo(numOfMyMinions);
    assertThat(game.inactiveSide.board.size()).isEqualTo(numOfFoeMinions);

    final CommandLine.CommandNode myRoot = CommandLine.yieldCommands(game.activeSide);
    checkCommands(myRoot, numOfMyMinions, game.activeSide.hand.size());

    // Switch side.
    game.endTurn();
    game.switchTurn();
    game.startTurn();

    final CommandLine.CommandNode foeRoot = CommandLine.yieldCommands(game.activeSide);
    checkCommands(foeRoot, numOfFoeMinions, game.activeSide.hand.size());
  }

  private void jumpIntoRoundFour() {
    // At least 4 crystals so YETI can be played and show up as options.
    for (int i = 0; i < 8; ++i) {
      //game.activeSide.drawCard();
      game.activeSide.hero.manaCrystal().startTurn();
      game.switchTurn();
    }
  }

  private void populateBoardWithMinions(final int numOfOwnMinions, final int numOfFoeMinions) {
    // Directly move minions from deck to board to avoid waiting the crystals growing one by one.
    game.startTurn();
    for (int i = 0; i < numOfOwnMinions; ++i) {
      minion.addToHandAndPlay((Minion) game.activeSide.deck.top());
    }

    game.switchTurn();
    for (int i = 0; i < numOfFoeMinions; ++i) {
      minion.addToHandAndPlay((Minion) game.activeSide.deck.top());
    }
    game.switchTurn();
  }

  private void checkCommands(final CommandLine.CommandNode root, final int numOfMinions,
                             final int numOfCardsInHand) {
    assertThat(root.childOptions.size()).isEqualTo(4);
    final List<String> childOptions = root.childOptions.stream()
        .map(option -> option.option)
        .collect(Collectors.toList());
    assertThat(childOptions).containsExactly(
        ConstCommand.END_TURN.toString(), ConstCommand.MINION_ATTACK.toString(),
        ConstCommand.PLAY_CARD.toString(), ConstCommand.USE_HERO_POWER.toString());

    for (CommandLine.CommandNode node : root.childOptions) {
      if (node.option.equals(ConstCommand.END_TURN.toString())) {
        assertThat(node.childOptions.size()).isEqualTo(0);
      } else if (node.option.equals(ConstCommand.USE_HERO_POWER.toString())) {
        // 1(own hero) + 2(own minions) + 1(foe hero) + 1(foe minion) = 5
        assertThat(node.childOptions.size()).isEqualTo(5);
      } else if (node.option.equals(ConstCommand.MINION_ATTACK.toString())) {
        assertThat(node.childOptions.size()).isEqualTo(numOfMinions);
      } else if (node.option.equals(ConstCommand.PLAY_CARD.toString())) {
        assertThat(node.childOptions.size()).isEqualTo(numOfCardsInHand);
      }
    }
  }

  @Test
  public void testCommandNodes() {
    final int numOfMyMinions = 2;
    final int numOfFoeMinions = 1;
    populateBoardWithMinions(numOfMyMinions, numOfFoeMinions);

    jumpIntoRoundFour();

    final CommandLine.CommandNode myRoot = CommandLine.yieldCommands(game.activeSide);
    // Choose option 1 which is play card.
    final InputStream playCardInput = new ByteArrayInputStream("1\n1".getBytes());
    final CommandLine.CommandNode playCardLeaf = CommandLine.run(
        myRoot, playCardInput, Game.SINGLE_COMMAND).get(0);
    assertThat(playCardLeaf.getParentType()).isEqualTo(ConstCommand.PLAY_CARD.toString());
    assertThat(playCardLeaf.option).isEqualTo(game.activeSide.hand.get(0).view().toString());

    // Choose option 2 which is move minion.
    final InputStream moveMinionInput = new ByteArrayInputStream("2\n1\n1".getBytes());
    final CommandLine.CommandNode moveMinionLeaf = CommandLine.run(
        myRoot, moveMinionInput, Game.SINGLE_COMMAND).get(0);
    assertThat(moveMinionLeaf.getParentType()).isEqualTo(ConstMinion.CHILLWIND_YETI.toString());
    assertThat(moveMinionLeaf.index).isEqualTo(0); // TODO: 1 points to first minion, which index is 0...

    // Choose option 3 which is use hero power.
    final InputStream useHeroPowerInput = new ByteArrayInputStream("3\n1".getBytes());
    final CommandLine.CommandNode heroPowerLeafNode = CommandLine.run(myRoot, useHeroPowerInput,
        Game.SINGLE_COMMAND).get(0);
    assertThat(heroPowerLeafNode.getParentType()).isEqualTo(ConstCommand.USE_HERO_POWER.toString());
    assertThat(heroPowerLeafNode.option).startsWith("{hero=");
    assertThat(heroPowerLeafNode.index).isEqualTo(-1); // TODO: 1 points to own hero, which index is -1...

    // Choose option 4 which is end turn.
    final InputStream endTurnInput = new ByteArrayInputStream("4".getBytes());
    CommandLine.CommandNode endTurnLeafNode = CommandLine.run(
        myRoot, endTurnInput, Game.SINGLE_COMMAND).get(0);
    assertThat(endTurnLeafNode.option).isEqualTo(ConstCommand.END_TURN.toString());
  }
}