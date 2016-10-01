package com.herthrone.base;

import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.game.Game;
import com.herthrone.service.BoardSide;
import com.herthrone.service.Command;
import com.herthrone.service.CommandType;
import com.herthrone.service.ContainerType;
import com.herthrone.service.Entity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class MinionTest {

  private Minion yeti1;
  private Minion yeti2;
  private MinionConfig yetiConfig;
  private Game game;

  private void addCardToHandAndPlayItOnOwnBoard(final Card card) {
    game.activeSide.hand.add(0, card);
    final Command playCardCommand = Command.newBuilder()
        .setType(CommandType.PLAY_CARD)
        .setDoer(Entity.newBuilder()
            .setSide(BoardSide.OWN)
            .setContainerType(ContainerType.HAND)
            .setPosition(0))
        .build();
    game.command(playCardCommand);
  }

  private Minion createAndBindMinion(final ConstMinion minionName) {
    final Minion minion = MinionFactory.create(minionName);
    game.activeSide.bind(minion);
    return minion;
  }

  @Before
  public void setUp() {
    this.game = new Game("gameId", ConstHero.GULDAN, ConstHero.GULDAN,
        Collections.emptyList(), Collections.emptyList());
    this.yeti1 = createAndBindMinion(ConstMinion.CHILLWIND_YETI);
    game.startTurn();
    addCardToHandAndPlayItOnOwnBoard(yeti1);
    game.switchTurn();
    this.yeti2 = createAndBindMinion(ConstMinion.CHILLWIND_YETI);
    addCardToHandAndPlayItOnOwnBoard(yeti2);
    game.switchTurn();

    this.yetiConfig = ConfigLoader.getMinionConfigByName(ConstMinion.CHILLWIND_YETI);
  }

  @Test
  public void testMinionStats() {
    assertThat(yetiConfig.health).isEqualTo(yeti1.health().value());
    assertThat(yetiConfig.health).isEqualTo(yeti2.health().value());
    assertThat(yeti1.isDead()).isFalse();
    assertThat(yeti2.isDead()).isFalse();
  }

  @Test
  public void testMinionAttack() {
    attackEachOther();
    assertThat(yeti1.health().value()).isEqualTo(yetiConfig.health - yetiConfig.attack);
    assertThat(yeti2.health().value()).isEqualTo(yetiConfig.health - yetiConfig.attack);
    attackEachOther();
    assertThat(yeti1.health().value()).isEqualTo(yetiConfig.health - 2 * yetiConfig.attack);
    assertThat(yeti2.health().value()).isEqualTo(yetiConfig.health - 2 * yetiConfig.attack);

    assertThat(yeti1.isDead()).isTrue();
    assertThat(yeti2.isDead()).isTrue();
  }

  private void attackEachOther() {
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(yeti1, yeti2);
  }

  @Test
  public void testMinionDeath() {
    // Before attack starts, both side has one minion on its board.
    assertThat(game.activeSide.board.size()).isEqualTo(1);
    assertThat(game.inactiveSide.board.size()).isEqualTo(1);
    attackEachOther();
    // After one attack, both side still has one minion on its board because Yeti should have one
    // health left.
    assertThat(game.activeSide.board.size()).isEqualTo(1);
    assertThat(game.inactiveSide.board.size()).isEqualTo(1);
    attackEachOther();
    // Both Yeti should be death and removed from its board.
    assertThat(yeti1.isDead()).isTrue();
    assertThat(yeti2.isDead()).isTrue();
    assertThat(game.activeSide.board.size()).isEqualTo(0);
    assertThat(game.inactiveSide.board.size()).isEqualTo(0);
  }
}


