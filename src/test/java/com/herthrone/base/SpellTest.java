package com.herthrone.base;

import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSpell;
import com.herthrone.factory.AttackFactory;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.SpellFactory;
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
  private MinionConfig yetiConfig;
  private Minion minion;
  private GameManager gm;

  @Before
  public void setUp() {
    this.gm = new GameManager(ConstHero.GARROSH_HELLSCREAM, ConstHero.GARROSH_HELLSCREAM,
        Collections.emptyList(), Collections.emptyList());
    this.hero1 = gm.activeSide.hero;
    this.hero2 = gm.inactiveSide.hero;

    this.yetiConfig = ConfigLoader.getMinionConfigByName(ConstMinion.CHILLWIND_YETI);
    this.minion = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
  }

  @Test
  public void testFireBall() {
    final Spell fireBall = SpellFactory.create(ConstSpell.FIRE_BALL);

    EffectFactory.getActionsByConfig(fireBall, minion).stream().forEach(Effect::act);
    assertThat(minion.getHealthAttr().getVal()).isEqualTo(
        yetiConfig.getHealth() + fireBall.getEffects().get(0).getValue());
    assertThat(minion.isDead()).isTrue();
  }

  @Test
  public void testArmorUp() {
    assertThat(hero1.getArmorAttr().getVal()).isEqualTo(0);
    final Spell armorUp = SpellFactory.createHeroPowerByName(ConstSpell.ARMOR_UP);

    EffectFactory.getActionsByConfig(armorUp, hero1).stream().forEach(Effect::act);
    assertThat(hero1.getArmorAttr().getVal()).isEqualTo(armorUp.getEffects().get(0).getValue());
  }

  @Test
  public void testLesserHeal() {
    final Spell lesserHeal = SpellFactory.createHeroPowerByName(ConstSpell.LESSER_HEAL);
    assertThat(hero1.getHealthLoss()).isEqualTo(0);
    final int largeDamage = 5;
    final int healVol = lesserHeal.getEffects().get(0).getValue();
    hero1.takeDamage(largeDamage);
    assertThat(hero1.getHealthLoss()).isEqualTo(largeDamage);

    EffectFactory.getActionsByConfig(lesserHeal, hero1).stream().forEach(Effect::act);
    assertThat(hero1.getHealthLoss()).isEqualTo(largeDamage - healVol);
    EffectFactory.getActionsByConfig(lesserHeal, hero1).stream().forEach(Effect::act);
    assertThat(hero1.getHealthLoss()).isEqualTo(largeDamage - healVol * 2);
    // Healing cannot exceed the health upper bound.
    EffectFactory.getActionsByConfig(lesserHeal, hero1).stream().forEach(Effect::act);
    assertThat(hero1.getHealthLoss()).isEqualTo(0);
  }

  @Test
  public void testFireBlast() {
    final Spell fireBlast = SpellFactory.createHeroPowerByName(ConstSpell.FIRE_BLAST);
    final int damage = fireBlast.getEffects().get(0).getValue();
    assertThat(hero2.getHealthLoss()).isEqualTo(0);

    EffectFactory.getActionsByConfig(fireBlast, hero2).stream().forEach(Effect::act);
    assertThat(hero2.getHealthLoss()).isEqualTo(-damage);

    EffectFactory.getActionsByConfig(fireBlast, hero2).stream().forEach(Effect::act);
    assertThat(hero2.getHealthLoss()).isEqualTo(-damage * 2);
  }

  @Test
  public void testSteadyShot() {
    final Spell steadyShot = SpellFactory.createHeroPowerByName(ConstSpell.STEADY_SHOT);

    final int damage = steadyShot.getEffects().get(0).getValue();
    assertThat(hero2.getHealthLoss()).isEqualTo(0);

    EffectFactory.getActionsByConfig(steadyShot, hero2).stream().forEach(Effect::act);
    assertThat(hero2.getHealthLoss()).isEqualTo(-damage);

    EffectFactory.getActionsByConfig(steadyShot, hero2).stream().forEach(Effect::act);
    assertThat(hero2.getHealthLoss()).isEqualTo(-damage * 2);
  }

  @Test
  public void testShapeshift() {
    final Spell shapeshift = SpellFactory.createHeroPowerByName(ConstSpell.SHAPESHIFT);
    final int attack = shapeshift.getEffects().get(0).getValue();
    final int armor = shapeshift.getEffects().get(1).getValue();

    assertThat(hero1.getAttackAttr().getVal()).isEqualTo(0);
    assertThat(hero1.getArmorAttr().getVal()).isEqualTo(0);

    EffectFactory.getActionsByConfig(shapeshift, hero1).stream().forEach(Effect::act);

    assertThat(hero1.getAttackAttr().getVal()).isEqualTo(attack);
    assertThat(hero1.getArmorAttr().getVal()).isEqualTo(armor);

    hero1.getAttackAttr().endTurn();
    // TODO:
    //assertEquals(0, hero1.getAttackAttr().getVal());
  }

  @Test
  public void testDaggerMastery() {
    final Spell daggerMastery = SpellFactory.createHeroPowerByName(ConstSpell.DAGGER_MASTERY);
    assertThat(hero1.canDamage()).isFalse();
    EffectFactory.getActionsByConfig(daggerMastery, hero1).stream().forEach(Effect::act);
    assertThat(hero1.canDamage()).isTrue();

    AttackFactory.getPhysicalDamageAction(hero1, hero2);
    assertThat(daggerMastery.getEffects().get(0).getValue()).isEqualTo(hero2.getHealthLoss());
  }

  @Test
  public void testReinforce() {
    final Spell reinforce = SpellFactory.createHeroPowerByName(ConstSpell.REINFORCE);
    assertThat(gm.activeSide.board.size()).isEqualTo(0);
    EffectFactory.getActionsByConfig(reinforce, hero1).stream().forEach(Effect::act);
    assertThat(gm.activeSide.board.size()).isEqualTo(1);

    final Minion minion = gm.activeSide.board.get(0);
    assertThat(minion.getCardName()).isEqualTo(ConstMinion.SILVER_HAND_RECRUIT.toString());
  }

  @Test
  public void testTotemicCall() {
    final Spell totemicCall = SpellFactory.createHeroPowerByName(ConstSpell.TOTEMIC_CALL);
    final int size = totemicCall.getEffects().get(0).getChoices().size();

    for (int i = 0; i < size; ++i) {
      EffectFactory.getActionsByConfig(totemicCall, hero1).stream().forEach(Effect::act);
      assertThat(gm.activeSide.board.size()).isEqualTo(i + 1);
    }

    Set<String> totems = gm.activeSide.board.stream().map(minion -> minion.getCardName()).collect
        (Collectors.toSet());
    assertThat(totems.size()).isEqualTo(size);
  }

  @Test
  public void testLifeTap() {
    final Spell lifeTap = SpellFactory.createHeroPowerByName(ConstSpell.LIFE_TAP);
    final Minion yeti = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    final int damage = -lifeTap.getEffects().get(0).getValue();

    assertThat(gm.activeSide.deck.size()).isEqualTo(0);
    gm.activeSide.deck.add(yeti);
    assertThat(gm.activeSide.deck.size()).isEqualTo(1);

    assertThat(gm.activeSide.hand.size()).isEqualTo(0);
    assertThat(hero1.getHealthLoss()).isEqualTo(0);
    EffectFactory.getActionsByConfig(lifeTap, hero1).stream().forEach(Effect::act);

    assertThat(gm.activeSide.hand.size()).isEqualTo(1);
    assertThat(gm.activeSide.deck.size()).isEqualTo(0);
    assertThat(hero1.getHealthLoss()).isEqualTo(damage);
  }
}
