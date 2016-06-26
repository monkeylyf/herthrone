package com.herthrone.base;

import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.factory.AttackFactory;
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
    this.gm = new GameManager(ConstHero.GULDAN, ConstHero.GULDAN,
        Collections.emptyList(), Collections.emptyList());
    this.yeti1 = MinionFactory.create(ConstMinion.CHILLWIND_YETI, gm.activeSide);
    gm.activeSide.board.add(yeti1);
    this.yeti2 = MinionFactory.create(ConstMinion.CHILLWIND_YETI, gm.inactiveSide);
    gm.inactiveSide.board.add(yeti2);

    this.yetiConfig = ConfigLoader.getMinionConfigByName(ConstMinion.CHILLWIND_YETI);
  }

  @Test
  public void testMinionStats() {
    assertEquals(yetiConfig.getHealth(), yeti1.getHealthAttr().getVal());
    assertEquals(yetiConfig.getHealth(), yeti2.getHealthAttr().getVal());
    assertFalse(yeti1.isDead());
    assertFalse(yeti2.isDead());
  }

  @Test
  public void testMinionAttack() {
    final int health = yetiConfig.getHealth();
    final int attack = yetiConfig.getAttack();
    attackEachOther();
    assertThat(yeti1.getHealthAttr().getVal()).isEqualTo(health - attack);
    assertThat(yeti2.getHealthAttr().getVal()).isEqualTo(health - attack);
    attackEachOther();
    assertThat(yeti1.getHealthAttr().getVal()).isEqualTo(health - 2 * attack);
    assertThat(yeti2.getHealthAttr().getVal()).isEqualTo(health - 2 * attack);

    assertThat(yeti1.isDead()).isTrue();
    assertThat(yeti2.isDead()).isTrue();
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

  private void attackEachOther() {
    AttackFactory.getPhysicalDamageAction(yeti1, yeti2);
  }
}


