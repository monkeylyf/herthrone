package com.herthrone.base;

import com.herthrone.card.factory.EffectFactory;
import com.herthrone.card.factory.MinionFactory;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.game.Battlefield;
import com.herthrone.game.shit;
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
    this.gm = new GameManager(shit.Hero.GARROSH_HELLSCREAM, shit.Hero.GARROSH_HELLSCREAM, Collections.emptyList(), Collections.emptyList());
    this.hero1 = this.gm.getHero1();
    this.hero2 = this.gm.getHero2();
    this.battlefield1 = this.gm.getBattlefield1();
    this.battlefield2 = this.gm.getBattlefield2();

    this.minionFactory1 = this.gm.factory1.minionFactory;
    this.minionFactory2 = this.gm.factory2.minionFactory;
    this.effectFactory1 = this.gm.factory1.effectFactory;
    this.effectFactory2 = this.gm.factory2.effectFactory;

  }

  @Test
  public void testCharge() throws FileNotFoundException {
    System.out.println("hello world");

    MinionConfig config = ConfigLoader.getMinionConfigByName(shit.Minion.WOLFRIDER);
    final Minion minion = this.minionFactory1.createMinionByName(shit.Minion.WOLFRIDER);
  }
}
