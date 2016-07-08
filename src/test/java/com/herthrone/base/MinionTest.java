package com.herthrone.base;

import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.game.GameManager;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by yifeng on 4/15/16.
 */
public class MinionTest extends TestCase {

  private Minion yeti1;
  private Minion yeti2;
  private MinionConfig yetiConfig;
  private GameManager gm;

  @Before
  public void setUp() {
    this.gm = new GameManager(ConstHero.GULDAN, ConstHero.GULDAN, Collections.emptyList(), Collections.emptyList());
    this.yeti1 = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(yeti1);
    gm.startTurn();
    gm.playCard(yeti1);
    gm.switchTurn();
    this.yeti2 = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(yeti2);
    gm.playCard(yeti2);
    gm.switchTurn();

    this.yetiConfig = ConfigLoader.getMinionConfigByName(ConstMinion.CHILLWIND_YETI);
  }

  @Test
  public void testMinionStats() {
    assertEquals(yetiConfig.health, yeti1.health().value());
    assertEquals(yetiConfig.health, yeti2.health().value());
    assertFalse(yeti1.isDead());
    assertFalse(yeti2.isDead());
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
    EffectFactory.AttackFactory.getPhysicalDamageEffect(yeti1, yeti2);
  }

  @Test
  public void testMinionDeath() {
    // Before attack starts, both side has one minion on its board.
    assertThat(gm.activeSide.board.size()).isEqualTo(1);
    assertThat(gm.inactiveSide.board.size()).isEqualTo(1);
    attackEachOther();
    // After one attack, both side still has one minion on its board because Yeti should have one
    // health left.
    assertThat(gm.activeSide.board.size()).isEqualTo(1);
    assertThat(gm.inactiveSide.board.size()).isEqualTo(1);
    attackEachOther();
    // Both Yeti should be death and removed from its board.
    assertThat(yeti1.isDead()).isTrue();
    assertThat(yeti2.isDead()).isTrue();
    assertThat(gm.activeSide.board.size()).isEqualTo(0);
    assertThat(gm.inactiveSide.board.size()).isEqualTo(0);
  }
}


