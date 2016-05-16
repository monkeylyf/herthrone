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

import java.io.FileNotFoundException;
import java.util.Collections;

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
  public void setUp() throws FileNotFoundException {
    this.gm = new GameManager(ConstHero.GARROSH_HELLSCREAM, ConstHero.GARROSH_HELLSCREAM, Container.emptyContainer(), Container.emptyContainer());
    this.hero1 = this.gm.getBattlefield1().mySide.hero;
    this.hero2 = this.gm.getBattlefield1().opponentSide.hero;
    this.battlefield1 = this.gm.getBattlefield1();
    this.battlefield2 = this.gm.getBattlefield2();

    this.minionFactory1 = this.gm.factory1.minionFactory;
    this.minionFactory2 = this.gm.factory2.minionFactory;
    this.effectFactory1 = this.gm.factory1.effectFactory;
    this.effectFactory2 = this.gm.factory2.effectFactory;
  }

  @Test
  public void testCharge() throws FileNotFoundException {
    MinionConfig config = ConfigLoader.getMinionConfigByName(ConstMinion.WOLFRIDER);
    final Minion minion = this.minionFactory1.createMinionByName(ConstMinion.WOLFRIDER);
  }
}
