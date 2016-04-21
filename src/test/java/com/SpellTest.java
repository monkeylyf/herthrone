package com;

import com.herthrone.Constants;
import com.herthrone.GameManager;
import com.herthrone.base.*;
import com.herthrone.card.factory.EffectFactory;
import com.herthrone.card.factory.MinionFactory;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.exception.CardNotFoundException;
import com.herthrone.exception.SpellNotFoundException;
import junit.framework.TestCase;
import org.junit.Before;

import java.io.FileNotFoundException;
import java.util.Collections;

/**
 * Created by yifeng on 4/20/16.
 */
public class SpellTest extends TestCase{

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
  public void setUp() throws FileNotFoundException, CardNotFoundException {
    this.gm = new GameManager(Constants.Hero.GARROSH_HELLSCREAM, Constants.Hero.GARROSH_HELLSCREAM, Collections.emptyList(), Collections.emptyList());
    this.hero1 = this.gm.getHero1();
    this.hero2 = this.gm.getHero2();
    this.battlefield1 = this.gm.getBattlefield1();
    this.battlefield2 = this.gm.getBattlefield2();

    this.minionFactory1 = new MinionFactory(this.battlefield1);
    this.minionFactory2 = new MinionFactory(this.battlefield2);
    this.effectFactory1 = new EffectFactory(this.minionFactory1, this.battlefield1);
    this.effectFactory2 = new EffectFactory(this.minionFactory2, this.battlefield2);

    this.yetiConfig = ConfigLoader.getMinionConfigByName(Constants.Minion.CHILLWIND_YETI);
    this.minion = this.minionFactory1.createMinionByName(Constants.Minion.CHILLWIND_YETI);
  }

  public void testFireBall() throws FileNotFoundException, SpellNotFoundException {
    final String spellName = "FireBall";
    SpellConfig fireBallConfig = ConfigLoader.getSpellConfigByName(spellName);
    Spell fireBall = this.gm.factory1.spellFactory.createSpellByName(spellName);

    this.effectFactory1.getActionsByConfig(fireBallConfig.getEffects().get(0), this.minion).act();
    assertEquals(this.yetiConfig.getHealth() + fireBallConfig.getEffects().get(0).getValue(), this.minion.getHealthAttr().getVal());
    assertTrue(this.minion.isDead());
  }
}
