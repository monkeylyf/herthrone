package com;

import static com.google.common.truth.Truth.assertThat;

import com.herthrone.base.BaseCard;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.card.factory.Action;
import com.herthrone.card.factory.EffectFactory;
import com.herthrone.card.factory.MinionFactory;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstHeroPower;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSpell;
import com.herthrone.game.Battlefield;
import com.herthrone.game.Container;
import com.herthrone.game.GameManager;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

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
    this.hero1 = this.gm.battlefield1.mySide.hero;
    this.hero2 = this.gm.battlefield1.opponentSide.hero;
    this.battlefield1 = this.gm.battlefield1;
    this.battlefield2 = this.gm.battlefield2;

    this.minionFactory1 = this.gm.factory1.minionFactory;
    this.minionFactory2 = this.gm.factory2.minionFactory;
    this.effectFactory1 = this.gm.factory1.effectFactory;
    this.effectFactory2 = this.gm.factory2.effectFactory;

    this.yetiConfig = ConfigLoader.getMinionConfigByName(ConstMinion.CHILLWIND_YETI);
    this.minion = this.minionFactory1.createMinionByName(ConstMinion.CHILLWIND_YETI);
  }

  @Test
  public void testFireBall() {
    Spell fireBall = this.gm.factory1.spellFactory.createSpellByName(ConstSpell.FIRE_BALL);

    this.effectFactory1.getActionsByConfig(fireBall, this.minion).stream().forEach(Action::act);
    assertThat(this.minion.getHealthAttr().getVal()).isEqualTo(this.yetiConfig.getHealth() + fireBall.getEffects().get(0).getValue());
    assertTrue(this.minion.isDead());
  }

  @Test
  public void testArmorUp() {
    assertThat(this.hero1.getArmorAttr().getVal()).isEqualTo(0);
    Spell armorUp = this.gm.factory1.spellFactory.createHeroPowerByName(ConstHeroPower.ARMOR_UP);

    this.effectFactory1.getActionsByConfig(armorUp, this.hero1).stream().forEach(Action::act);
    assertThat(this.hero1.getArmorAttr().getVal()).isEqualTo(armorUp.getEffects().get(0).getValue());
  }

  @Test
  public void testLesserHeal() {
    Spell lesserHeal = this.gm.factory1.spellFactory.createHeroPowerByName(ConstHeroPower.LESSER_HEAL);
    assertThat(this.hero1.getHealthLoss()).isEqualTo(0);
    final int largeDamage = 5;
    final int healVol = lesserHeal.getEffects().get(0).getValue();
    this.hero1.takeDamage(largeDamage);
    assertThat(this.hero1.getHealthLoss()).isEqualTo(largeDamage);

    this.effectFactory1.getActionsByConfig(lesserHeal, this.hero1).stream().forEach(Action::act);
    assertThat(this.hero1.getHealthLoss()).isEqualTo(largeDamage - healVol);
    this.effectFactory1.getActionsByConfig(lesserHeal, this.hero1).stream().forEach(Action::act);
    assertThat(this.hero1.getHealthLoss()).isEqualTo(largeDamage - healVol * 2);
    // Healing cannot exceed the health upper bound.
    this.effectFactory1.getActionsByConfig(lesserHeal, this.hero1).stream().forEach(Action::act);
    assertThat(this.hero1.getHealthLoss()).isEqualTo(0);
  }

  @Test
  public void testFireBlast() {
    Spell fireBlast = this.gm.factory1.spellFactory.createHeroPowerByName(ConstHeroPower.FIRE_BLAST);
    final int damage = fireBlast.getEffects().get(0).getValue();
    assertEquals(0, this.hero2.getHealthLoss());

    this.effectFactory1.getActionsByConfig(fireBlast, this.hero2).stream().forEach(Action::act);
    assertEquals(-damage, this.hero2.getHealthLoss());

    this.effectFactory1.getActionsByConfig(fireBlast, this.hero2).stream().forEach(Action::act);
    assertEquals(-damage * 2, this.hero2.getHealthLoss());
  }

  @Test
  public void testSteadyShot() {
    Spell steadyShot = this.gm.factory1.spellFactory.createHeroPowerByName(ConstHeroPower.STEADY_SHOT);

    final int damage = steadyShot.getEffects().get(0).getValue();
    assertEquals(0, this.hero2.getHealthLoss());

    this.effectFactory1.getActionsByConfig(steadyShot, this.hero2).stream().forEach(Action::act);
    assertEquals(-damage, this.hero2.getHealthLoss());

    this.effectFactory1.getActionsByConfig(steadyShot, this.hero2).stream().forEach(Action::act);
    assertEquals(-damage * 2, this.hero2.getHealthLoss());
  }

  @Test
  public void testShapeshift() {
    Spell shapeshift = this.gm.factory1.spellFactory.createHeroPowerByName(ConstHeroPower.SHAPESHIFT);
    final int attack = shapeshift.getEffects().get(0).getValue();
    final int armor = shapeshift.getEffects().get(1).getValue();

    assertEquals(0, this.hero1.getAttackAttr().getVal());
    assertEquals(0, this.hero1.getArmorAttr().getVal());

    this.effectFactory1.getActionsByConfig(shapeshift, this.hero1).stream().forEach(Action::act);

    assertEquals(attack, this.hero1.getAttackAttr().getVal());
    assertEquals(armor, this.hero1.getArmorAttr().getVal());

    this.hero1.getAttackAttr().nextRound();
    // TODO:
    //assertEquals(0, this.hero1.getAttackAttr().getVal());
  }

  @Test
  public void testDaggerMastery() {
    Spell daggerMastery = this.gm.factory1.spellFactory.createHeroPowerByName(ConstHeroPower.DAGGER_MASTERY);
    assertFalse(this.hero1.canDamage());
    this.effectFactory1.getActionsByConfig(daggerMastery, this.hero1).stream().forEach(Action::act);
    assertTrue(this.hero1.canDamage());

    this.gm.factory1.attackFactory.getPhysicalDamageAction(this.hero1, this.hero2).act();
    assertEquals(daggerMastery.getEffects().get(0).getValue(), this.hero2.getHealthLoss());
  }

  @Test
  public void testReinforce() {
    Spell reinforce = this.gm.factory1.spellFactory.createHeroPowerByName(ConstHeroPower.REINFORCE);
    assertEquals(0, this.battlefield1.mySide.minions.size());
    this.effectFactory1.getActionsByConfig(reinforce, this.hero1).stream().forEach(Action::act);
    assertEquals(1, this.battlefield1.mySide.minions.size());
  }

  @Test
  public void testTotemicCall() {
    Spell totemicCall = this.gm.factory1.spellFactory.createHeroPowerByName(ConstHeroPower.TOTEMIC_CALL);
    final int size = totemicCall.getEffects().get(0).getTarget().size();

    for (int i = 0; i < size; ++i) {
      this.effectFactory1.getActionsByConfig(totemicCall, this.hero1).stream().forEach(Action::act);
      assertEquals(i + 1, this.battlefield1.mySide.minions.size());
    }

    Set<String> totems = this.battlefield1.mySide.minions.stream().map(minion -> minion.getCardName()).collect(Collectors.toSet());
    assertEquals(size, totems.size());
  }

  @Test
  public void testLifeTap() {
    final Spell lifeTap = this.gm.factory1.spellFactory.createHeroPowerByName(ConstHeroPower.LIFE_TAP);
    final Minion yeti = this.gm.factory1.minionFactory.createMinionByName(ConstMinion.CHILLWIND_YETI);
    final int damage = -lifeTap.getEffects().get(0).getValue();

    final Container<BaseCard> hand = this.battlefield1.mySide.hand;
    final Container<BaseCard> deck = this.battlefield1.mySide.deck;

    assertEquals(0, deck.size());
    deck.add(yeti);
    assertEquals(1, deck.size());

    assertEquals(0, hand.size());
    assertEquals(0, this.hero1.getHealthLoss());
    this.effectFactory1.getActionsByConfig(lifeTap, this.hero1).stream().forEach(Action::act);

    assertEquals(1, hand.size());
    assertEquals(0, deck.size());
    assertEquals(damage, this.hero1.getHealthLoss());
  }
}
