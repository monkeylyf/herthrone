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

  @BeforeClass
  public static void beforeClass() {
    List<String> cards1 = Collections.nCopies(deckSize, ConstMinion.CHILLWIND_YETI.toString());
    List<String> cards2 = Collections.nCopies(deckSize, ConstMinion.CHILLWIND_YETI.toString());

    gameManager = new GameManager(ConstHero.ANDUIN_WRYNN, ConstHero.JAINA_PROUDMOORE, cards1, cards2);
  }

  @Test
  public void testConstructor() {
    assertThat(gameManager.battlefield1.mySide.deck.size()).isEqualTo(deckSize);
    assertThat(gameManager.battlefield1.opponentSide.deck.size()).isEqualTo(deckSize);

    assertThat(gameManager.battlefield1.mySide.hero.getCardName()).isEqualTo(hero1.toString());
    assertThat(gameManager.battlefield1.opponentSide.hero.getCardName()).isEqualTo(hero2.toString());
  }

}