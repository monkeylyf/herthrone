package com.herthrone.base;

import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.factory.EffectFactory;
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

  private EffectFactory effectFactory1;
  private EffectFactory effectFactory2;
  private MinionFactory minionFactory1;
  private MinionFactory minionFactory2;

  private Minion minion1;
  private Minion minion2;

  private MinionConfig yetiConfig;

  private GameManager gm;

  @Before
  public void setUp() {
    this.gm = new GameManager(ConstHero.GULDAN, ConstHero.GULDAN, Collections.emptyList(), Collections.emptyList());
    this.battlefield1 = gm.battlefield1;
    this.battlefield2 = gm.battlefield2;

    this.minionFactory1 = gm.factory1.minionFactory;
    this.minionFactory2 = gm.factory2.minionFactory;
    this.effectFactory1 = gm.factory1.effectFactory;
    this.effectFactory2 = gm.factory2.effectFactory;

    this.minion1 = minionFactory1.createMinionByName(ConstMinion.CHILLWIND_YETI);
    this.minion2 = minionFactory1.createMinionByName(ConstMinion.CHILLWIND_YETI);

    this.yetiConfig = ConfigLoader.getMinionConfigByName(ConstMinion.CHILLWIND_YETI);
  }

  @Test
  public void testMinionStats() {
    MinionConfig config = ConfigLoader.getMinionConfigByName(ConstMinion.CHILLWIND_YETI);
    assertEquals(config.getHealth(), minion1.getHealthAttr().getVal());
    assertEquals(config.getHealth(), minion2.getHealthAttr().getVal());
    assertFalse(minion1.isDead());
    assertFalse(minion2.isDead());
  }

  @Test
  public void testMinionAttack() {
    attackEachOther();
    assertEquals(yetiConfig.getHealth() - yetiConfig.getAttack(), minion1.getHealthAttr().getVal());
    assertEquals(yetiConfig.getHealth() - yetiConfig.getAttack(), minion2.getHealthAttr().getVal());
    attackEachOther();
    assertEquals(yetiConfig.getHealth() - yetiConfig.getAttack() * 2, minion1.getHealthAttr().getVal());
    assertEquals(yetiConfig.getHealth() - yetiConfig.getAttack() * 2, minion2.getHealthAttr().getVal());

    assertThat(minion1.isDead()).isTrue();
    assertThat(minion2.isDead()).isTrue();
  }

  private void attackEachOther() {
    gm.factory1.attackFactory.getPhysicalDamageAction(minion1, minion2);
  }
}


