package com.herthrone.game;

import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by yifengliu on 5/17/16.
 */
public class GameManagerTest {

  private static GameManager gameManager;
  private static ConstHero hero1 = ConstHero.ANDUIN_WRYNN;
  private static ConstHero hero2 = ConstHero.JAINA_PROUDMOORE;
  private static final int deckSize = 10;

  private static Side mySide;
  private static Side opponentSide;

  @BeforeClass
  public static void beforeClass() {
    List<String> cards1 = Collections.nCopies(deckSize, ConstMinion.CHILLWIND_YETI.toString());
    List<String> cards2 = Collections.nCopies(deckSize, ConstMinion.CHILLWIND_YETI.toString());

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
}