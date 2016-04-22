package com;

import com.herthrone.Constants;
import com.herthrone.game.Battlefield;
import com.herthrone.game.GameManager;
import com.herthrone.base.*;
import com.herthrone.card.factory.EffectFactory;
import com.herthrone.card.factory.MinionFactory;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.exception.CardNotFoundException;
import com.herthrone.exception.SpellNotFoundException;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

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

  @Test
  public void testFireBall() throws FileNotFoundException, SpellNotFoundException {
    final String spellName = "FireBall";
    Spell fireBall = this.gm.factory1.spellFactory.createSpellByName(spellName);

    this.effectFactory1.getActionsByConfig(fireBall, this.minion).stream().forEach(action -> action.act());
    assertEquals(this.yetiConfig.getHealth() + fireBall.getEffects().get(0).getValue(), this.minion.getHealthAttr().getVal());
    assertTrue(this.minion.isDead());
  }

  @Test
  public void testArmorUp() throws FileNotFoundException, SpellNotFoundException {
    final String spellName = "ArmorUp";
    assertEquals(0, this.hero1.getArmorAttr().getVal());
    Spell armorUp = this.gm.factory1.spellFactory.createHeroPowerByName(spellName);

    assertEquals(0, this.hero1.getArmorAttr().getVal());
    this.effectFactory1.getActionsByConfig(armorUp, this.hero1).stream().forEach(action -> action.act());
    assertEquals(armorUp.getEffects().get(0).getValue(), this.hero1.getArmorAttr().getVal());
  }

  @Test
  public void testLesserHeal() throws FileNotFoundException, SpellNotFoundException {
    final String spellName = "LesserHeal";
    Spell lesserHeal = this.gm.factory1.spellFactory.createHeroPowerByName(spellName);
    assertEquals(0, this.hero1.getHealthLoss());
    final int largeDamage = 5;
    final int healVol = lesserHeal.getEffects().get(0).getValue();
    this.hero1.takeDamage(largeDamage);
    assertEquals(largeDamage, this.hero1.getHealthLoss());

    this.effectFactory1.getActionsByConfig(lesserHeal, this.hero1).stream().forEach(action -> action.act());
    assertEquals(largeDamage - healVol, this.hero1.getHealthLoss());
    this.effectFactory1.getActionsByConfig(lesserHeal, this.hero1).stream().forEach(action -> action.act());
    assertEquals(largeDamage - healVol * 2, this.hero1.getHealthLoss());
    // Healing cannot exceed the health upper bound.
    this.effectFactory1.getActionsByConfig(lesserHeal, this.hero1).stream().forEach(action -> action.act());
    assertEquals(0, this.hero1.getHealthLoss());
  }

  @Test
  public void testFireBlast() throws FileNotFoundException, SpellNotFoundException {
    final String spellName = "FireBlast";
    Spell fireBlast = this.gm.factory1.spellFactory.createHeroPowerByName(spellName);
    final int damage = fireBlast.getEffects().get(0).getValue();
    assertEquals(0, this.hero2.getHealthLoss());

    this.effectFactory1.getActionsByConfig(fireBlast, this.hero2).stream().forEach(action -> action.act());
    assertEquals(-damage, this.hero2.getHealthLoss());

    this.effectFactory1.getActionsByConfig(fireBlast, this.hero2).stream().forEach(action -> action.act());
    assertEquals(-damage * 2, this.hero2.getHealthLoss());
  }

  @Test
  public void testSteadyShot() throws FileNotFoundException, SpellNotFoundException {
    final String spellName = "SteadyShot";
    Spell steadyShot = this.gm.factory1.spellFactory.createHeroPowerByName(spellName);

    final int damage = steadyShot.getEffects().get(0).getValue();
    assertEquals(0, this.hero2.getHealthLoss());

    this.effectFactory1.getActionsByConfig(steadyShot, this.hero2).stream().forEach(action -> action.act());
    assertEquals(-damage, this.hero2.getHealthLoss());

    this.effectFactory1.getActionsByConfig(steadyShot, this.hero2).stream().forEach(action -> action.act());
    assertEquals(-damage * 2, this.hero2.getHealthLoss());
  }

  @Test
  public void testShapeshift() throws FileNotFoundException, SpellNotFoundException {
    final String spellName = "Shapeshift";
    Spell shapeshift = this.gm.factory1.spellFactory.createHeroPowerByName(spellName);
    final int attack = shapeshift.getEffects().get(0).getValue();
    final int armor = shapeshift.getEffects().get(1).getValue();

    assertEquals(0, this.hero1.getAttackAttr().getVal());
    assertEquals(0, this.hero1.getArmorAttr().getVal());

    this.effectFactory1.getActionsByConfig(shapeshift, this.hero1).stream().forEach(action -> action.act());

    assertEquals(attack, this.hero1.getAttackAttr().getVal());
    assertEquals(armor, this.hero1.getArmorAttr().getVal());

    this.hero1.getAttackAttr().nextRound();
    //assertEquals(0, this.hero1.getAttackAttr().getVal());
  }
}
