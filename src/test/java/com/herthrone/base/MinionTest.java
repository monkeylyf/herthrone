package com.herthrone.base;

import com.herthrone.card.factory.EffectFactory;
import com.herthrone.card.factory.MinionFactory;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.game.Battlefield;
import com.herthrone.game.Container;
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
    this.battlefield1 = this.gm.battlefield1;
    this.battlefield2 = this.gm.battlefield2;

    this.minionFactory1 = this.gm.factory1.minionFactory;
    this.minionFactory2 = this.gm.factory2.minionFactory;
    this.effectFactory1 = this.gm.factory1.effectFactory;
    this.effectFactory2 = this.gm.factory2.effectFactory;

    this.minion1 = this.minionFactory1.createMinionByName(ConstMinion.CHILLWIND_YETI);
    this.minion2 = this.minionFactory1.createMinionByName(ConstMinion.CHILLWIND_YETI);

    this.yetiConfig = ConfigLoader.getMinionConfigByName(ConstMinion.CHILLWIND_YETI);
  }

  @Test
  public void testMinionStats() {
    MinionConfig config = ConfigLoader.getMinionConfigByName(ConstMinion.CHILLWIND_YETI);
    assertEquals(config.getHealth(), this.minion1.getHealthAttr().getVal());
    assertEquals(config.getHealth(), this.minion2.getHealthAttr().getVal());
    assertFalse(this.minion1.isDead());
    assertFalse(this.minion2.isDead());
  }

  @Test
  public void testMinionAttack() {
    attackEachOther();
    assertEquals(this.yetiConfig.getHealth() - this.yetiConfig.getAttack(), this.minion1.getHealthAttr().getVal());
    assertEquals(this.yetiConfig.getHealth() - this.yetiConfig.getAttack(), this.minion2.getHealthAttr().getVal());
    attackEachOther();
    assertEquals(this.yetiConfig.getHealth() - this.yetiConfig.getAttack() * 2, this.minion1.getHealthAttr().getVal());
    assertEquals(this.yetiConfig.getHealth() - this.yetiConfig.getAttack() * 2, this.minion2.getHealthAttr().getVal());

    assertThat(this.minion1.isDead()).isTrue();
    assertThat(this.minion2.isDead()).isTrue();
  }

  private void attackEachOther() {
    this.gm.factory1.attackFactory.getPhysicalDamageAction(this.minion1, this.minion2).act();
  }
}


