package com.herthrone.base;

import com.google.common.base.Optional;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMechanic;
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
 * Created by yifengliu on 5/10/16.
 */
public class MechanicTest extends TestCase {

  private Hero hero1;
  private Hero hero2;
  private Battlefield battlefield1;
  private Battlefield battlefield2;

  private MinionFactory minionFactory1;
  private MinionFactory minionFactory2;
  private EffectFactory effectFactory1;
  private EffectFactory effectFactory2;

  private MinionConfig yetiConfig;
  private Minion minion;
  private GameManager gm;

  @Before
  public void setUp() {
    this.gm = new GameManager(ConstHero.GARROSH_HELLSCREAM, ConstHero.GARROSH_HELLSCREAM, Collections.emptyList(), Collections.emptyList());
    this.hero1 = gm.battlefield1.mySide.hero;
    this.hero2 = gm.battlefield1.opponentSide.hero;
    this.battlefield1 = gm.battlefield1;
    this.battlefield2 = gm.battlefield2;

    this.minionFactory1 = gm.factory1.minionFactory;
    this.minionFactory2 = gm.factory2.minionFactory;
    this.effectFactory1 = gm.factory1.effectFactory;
    this.effectFactory2 = gm.factory2.effectFactory;
  }

  @Test
  public void testCharge() {
    final ConstMinion minionName = ConstMinion.WOLFRIDER;
    final MinionConfig config = ConfigLoader.getMinionConfigByName(minionName);
    final Optional<MechanicConfig> mechanic = config.getMechanic(ConstMechanic.CHARGE);
    assertThat(mechanic.isPresent()).isTrue();
    final Minion minion = minionFactory1.createMinionByName(minionName);
    assertThat(minion.getMovePoints().getVal()).isGreaterThan(0);
  }

  @Test
  public void testBattlecry() {
    MinionConfig config = ConfigLoader.getMinionConfigByName(ConstMinion.GNOMISH_INVENTOR);
    ConstMechanic.lowerValueOf("charge");
  }
}