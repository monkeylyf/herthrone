package com.herthrone.base;

import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.factory.AttackFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.game.Battlefield;
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

  private Battlefield battlefield1;
  private Battlefield battlefield2;
  private Minion minion1;
  private Minion minion2;
  private MinionConfig yetiConfig;
  private ConstMinion minionName;
  private GameManager gm;

  @Before
  public void setUp() {
    this.gm = new GameManager(ConstHero.GULDAN, ConstHero.GULDAN, Collections.emptyList(), Collections.emptyList());
    this.battlefield1 = gm.battlefield1;
    this.battlefield2 = gm.battlefield2;
    this.minionName = ConstMinion.CHILLWIND_YETI;

    this.minion1 = MinionFactory.createMinionByName(minionName);
    minion1.getBinder().bind(battlefield1.mySide);
    this.minion2 = MinionFactory.createMinionByName(minionName);
    minion2.getBinder().bind(battlefield2.mySide);

    this.yetiConfig = ConfigLoader.getMinionConfigByName(minionName);
  }

  @Test
  public void testMinionStats() {
    MinionConfig config = ConfigLoader.getMinionConfigByName(minionName);
    assertEquals(config.getHealth(), minion1.getHealthAttr().getVal());
    assertEquals(config.getHealth(), minion2.getHealthAttr().getVal());
    assertFalse(minion1.isDead());
    assertFalse(minion2.isDead());
  }

  @Test
  public void testMinionAttack() {
    final int health = yetiConfig.getHealth();
    final int attack = yetiConfig.getAttack();
    attackEachOther();
    assertThat(minion1.getHealthAttr().getVal()).isEqualTo(health - attack);
    assertThat(minion2.getHealthAttr().getVal()).isEqualTo(health - attack);
    attackEachOther();
    assertThat(minion1.getHealthAttr().getVal()).isEqualTo(health - 2 * attack);
    assertThat(minion2.getHealthAttr().getVal()).isEqualTo(health - 2 * attack);

    assertThat(minion1.isDead()).isTrue();
    assertThat(minion2.isDead()).isTrue();
  }

  private void attackEachOther() {
    AttackFactory.getPhysicalDamageAction(minion1, minion2);
  }
}


