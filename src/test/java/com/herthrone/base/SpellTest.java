package com.herthrone.base;

import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSpell;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.game.Battlefield;
import com.herthrone.game.Container;
import com.herthrone.game.GameManager;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by yifeng on 4/20/16.
 */
public class SpellTest extends TestCase {

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

    this.yetiConfig = ConfigLoader.getMinionConfigByName(ConstMinion.CHILLWIND_YETI);
    this.minion = minionFactory1.createMinionByName(ConstMinion.CHILLWIND_YETI);
  }

  @Test
  public void testFireBall() {
    Spell fireBall = gm.factory1.spellFactory.createSpellByName(ConstSpell.FIRE_BALL);

    effectFactory1.getActionsByConfig(fireBall, minion).stream().forEach(Effect::act);
    assertThat(minion.getHealthAttr().getVal()).isEqualTo(yetiConfig.getHealth() + fireBall.getEffects().get(0).getValue());
    assertThat(minion.isDead()).isTrue();
  }

  @Test
  public void testArmorUp() {
    assertThat(hero1.getArmorAttr().getVal()).isEqualTo(0);
    Spell armorUp = gm.factory1.spellFactory.createHeroPowerByName(ConstSpell.ARMOR_UP);

    effectFactory1.getActionsByConfig(armorUp, hero1).stream().forEach(Effect::act);
    assertThat(hero1.getArmorAttr().getVal()).isEqualTo(armorUp.getEffects().get(0).getValue());
  }

  @Test
  public void testLesserHeal() {
    Spell lesserHeal = gm.factory1.spellFactory.createHeroPowerByName(ConstSpell.LESSER_HEAL);
    assertThat(hero1.getHealthLoss()).isEqualTo(0);
    final int largeDamage = 5;
    final int healVol = lesserHeal.getEffects().get(0).getValue();
    hero1.takeDamage(largeDamage);
    assertThat(hero1.getHealthLoss()).isEqualTo(largeDamage);

    effectFactory1.getActionsByConfig(lesserHeal, hero1).stream().forEach(Effect::act);
    assertThat(hero1.getHealthLoss()).isEqualTo(largeDamage - healVol);
    effectFactory1.getActionsByConfig(lesserHeal, hero1).stream().forEach(Effect::act);
    assertThat(hero1.getHealthLoss()).isEqualTo(largeDamage - healVol * 2);
    // Healing cannot exceed the health upper bound.
    effectFactory1.getActionsByConfig(lesserHeal, hero1).stream().forEach(Effect::act);
    assertThat(hero1.getHealthLoss()).isEqualTo(0);
  }

  @Test
  public void testFireBlast() {
    Spell fireBlast = gm.factory1.spellFactory.createHeroPowerByName(ConstSpell.FIRE_BLAST);
    final int damage = fireBlast.getEffects().get(0).getValue();
    assertEquals(0, hero2.getHealthLoss());

    effectFactory1.getActionsByConfig(fireBlast, hero2).stream().forEach(Effect::act);
    assertEquals(-damage, hero2.getHealthLoss());

    effectFactory1.getActionsByConfig(fireBlast, hero2).stream().forEach(Effect::act);
    assertEquals(-damage * 2, hero2.getHealthLoss());
  }

  @Test
  public void testSteadyShot() {
    Spell steadyShot = gm.factory1.spellFactory.createHeroPowerByName(ConstSpell.STEADY_SHOT);

    final int damage = steadyShot.getEffects().get(0).getValue();
    assertEquals(0, hero2.getHealthLoss());

    effectFactory1.getActionsByConfig(steadyShot, hero2).stream().forEach(Effect::act);
    assertEquals(-damage, hero2.getHealthLoss());

    effectFactory1.getActionsByConfig(steadyShot, hero2).stream().forEach(Effect::act);
    assertEquals(-damage * 2, hero2.getHealthLoss());
  }

  @Test
  public void testShapeshift() {
    Spell shapeshift = gm.factory1.spellFactory.createHeroPowerByName(ConstSpell.SHAPESHIFT);
    final int attack = shapeshift.getEffects().get(0).getValue();
    final int armor = shapeshift.getEffects().get(1).getValue();

    assertEquals(0, hero1.getAttackAttr().getVal());
    assertEquals(0, hero1.getArmorAttr().getVal());

    effectFactory1.getActionsByConfig(shapeshift, hero1).stream().forEach(Effect::act);

    assertEquals(attack, hero1.getAttackAttr().getVal());
    assertEquals(armor, hero1.getArmorAttr().getVal());

    hero1.getAttackAttr().endTurn();
    // TODO:
    //assertEquals(0, hero1.getAttackAttr().getVal());
  }

  @Test
  public void testDaggerMastery() {
    Spell daggerMastery = gm.factory1.spellFactory.createHeroPowerByName(ConstSpell.DAGGER_MASTERY);
    assertFalse(hero1.canDamage());
    effectFactory1.getActionsByConfig(daggerMastery, hero1).stream().forEach(Effect::act);
    assertThat(hero1.canDamage()).isTrue();

    gm.factory1.attackFactory.getPhysicalDamageAction(hero1, hero2).act();
    assertEquals(daggerMastery.getEffects().get(0).getValue(), hero2.getHealthLoss());
  }

  @Test
  public void testReinforce() {
    Spell reinforce = gm.factory1.spellFactory.createHeroPowerByName(ConstSpell.REINFORCE);
    assertEquals(0, battlefield1.mySide.board.size());
    effectFactory1.getActionsByConfig(reinforce, hero1).stream().forEach(Effect::act);
    assertEquals(1, battlefield1.mySide.board.size());
  }

  @Test
  public void testTotemicCall() {
    Spell totemicCall = gm.factory1.spellFactory.createHeroPowerByName(ConstSpell.TOTEMIC_CALL);
    final int size = totemicCall.getEffects().get(0).getChoices().size();

    for (int i = 0; i < size; ++i) {
      effectFactory1.getActionsByConfig(totemicCall, hero1).stream().forEach(Effect::act);
      assertEquals(i + 1, battlefield1.mySide.board.size());
    }

    Set<String> totems = battlefield1.mySide.board.stream().map(minion -> minion.getCardName()).collect(Collectors.toSet());
    assertEquals(size, totems.size());
  }

  @Test
  public void testLifeTap() {
    final Spell lifeTap = gm.factory1.spellFactory.createHeroPowerByName(ConstSpell.LIFE_TAP);
    final Minion yeti = gm.factory1.minionFactory.createMinionByName(ConstMinion.CHILLWIND_YETI);
    final int damage = -lifeTap.getEffects().get(0).getValue();

    final Container<Card> hand = battlefield1.mySide.hand;
    final Container<Card> deck = battlefield1.mySide.deck;

    assertEquals(0, deck.size());
    deck.add(yeti);
    assertEquals(1, deck.size());

    assertEquals(0, hand.size());
    assertEquals(0, hero1.getHealthLoss());
    effectFactory1.getActionsByConfig(lifeTap, hero1).stream().forEach(Effect::act);

    assertEquals(1, hand.size());
    assertEquals(0, deck.size());
    assertEquals(damage, hero1.getHealthLoss());
  }
}