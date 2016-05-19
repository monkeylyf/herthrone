package com.herthrone.game;

import com.herthrone.base.Minion;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by yifengliu on 5/17/16.
 */
public class GameManagerTest {

  private static ConstMinion MINION = ConstMinion.CHILLWIND_YETI;

  private GameManager gameManager;
  private ConstHero hero1 = ConstHero.ANDUIN_WRYNN;
  private ConstHero hero2 = ConstHero.JAINA_PROUDMOORE;
  private final int deckSize = 10;

  private Side mySide;
  private Side opponentSide;

  @Before
  public void setUp() {
    List<String> cards1 = Collections.nCopies(deckSize, MINION.toString());
    List<String> cards2 = Collections.nCopies(deckSize, MINION.toString());

    gameManager = new GameManager(ConstHero.ANDUIN_WRYNN, ConstHero.JAINA_PROUDMOORE, cards1, cards2);

    mySide = gameManager.battlefield1.mySide;
    opponentSide = gameManager.battlefield1.opponentSide;
  }

  @Test
  public void testInitDeckSize() {
    assertThat(mySide.deck.size()).isEqualTo(deckSize);
    assertThat(opponentSide.deck.size()).isEqualTo(deckSize);
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
  public void testPlayMinionCard() {
    gameManager.drawCard();

    assertThat(mySide.hand.get(0) instanceof Minion).isTrue();
    assertThat(mySide.hand.get(0).getCardName()).isEqualTo(MINION.toString());
    assertThat(mySide.board.size()).isEqualTo(0);
    gameManager.playCard(0);
    assertThat(mySide.board.size()).isEqualTo(1);
    assertThat(mySide.board.get(0).getCardName()).isEqualTo(MINION.toString());
  }
}