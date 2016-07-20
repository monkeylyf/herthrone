package com.herthrone.base;

import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSpell;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.HeroPowerFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.SpellFactory;
import com.herthrone.game.GameManager;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class SpellTest extends TestCase {

  private Hero hero1;
  private Hero hero2;
  private Minion yeti;
  private GameManager gm;

  private Spell createSpellAndBind(final ConstSpell spellName) {
    final Spell spell = SpellFactory.create(spellName);
    gm.activeSide.bind(spell);
    return spell;
  }

  private Spell createHeroPowerAndBind(final ConstSpell spellName) {
    final Spell spell = HeroPowerFactory.create(spellName);
    gm.activeSide.bind(spell);
    return spell;
  }

  @Before
  public void setUp() {
    this.gm = new GameManager(ConstHero.GARROSH_HELLSCREAM, ConstHero.GARROSH_HELLSCREAM,
        Collections.emptyList(), Collections.emptyList());
    this.hero1 = gm.activeSide.hero;
    this.hero2 = gm.inactiveSide.hero;

    this.yeti = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(yeti);
  }

  @Test
  public void testFireBall() {
    final Spell fireBall = createSpellAndBind(ConstSpell.FIRE_BALL);
    final int health = yeti.health().value();
    EffectFactory.pipeEffects(fireBall, yeti);
    assertThat(yeti.health().value()).isEqualTo(health - 6);
    assertThat(yeti.isDead()).isTrue();
  }

  @Test
  public void testArmorUp() {
    assertThat(hero1.armor().value()).isEqualTo(0);
    final Spell armorUp = createHeroPowerAndBind(ConstSpell.ARMOR_UP);
    EffectFactory.pipeEffects(armorUp, hero1);
    assertThat(hero1.armor().value()).isEqualTo(2);
  }

  @Test
  public void testLesserHeal() {
    final Spell lesserHeal = createHeroPowerAndBind(ConstSpell.LESSER_HEAL);
    assertThat(hero1.healthLoss()).isEqualTo(0);
    final int largeDamage = 5;
    final int healVol = 2;
    hero1.takeDamage(largeDamage);
    assertThat(hero1.healthLoss()).isEqualTo(largeDamage);

    EffectFactory.pipeEffects(lesserHeal, hero1);
    assertThat(hero1.healthLoss()).isEqualTo(largeDamage - healVol);
    EffectFactory.pipeEffects(lesserHeal, hero1);
    assertThat(hero1.healthLoss()).isEqualTo(largeDamage - healVol * 2);
    // Healing cannot exceed the health upper bound.
    EffectFactory.pipeEffects(lesserHeal, hero1);
    assertThat(hero1.healthLoss()).isEqualTo(0);
  }

  @Test
  public void testFireBlast() {
    final Spell fireBlast = createHeroPowerAndBind(ConstSpell.FIRE_BLAST);
    final int damage = 1;
    assertThat(hero2.healthLoss()).isEqualTo(0);

    EffectFactory.pipeEffects(fireBlast, hero2);
    assertThat(hero2.healthLoss()).isEqualTo(damage);

    EffectFactory.pipeEffects(fireBlast, hero2);
    assertThat(hero2.healthLoss()).isEqualTo(damage * 2);
  }

  @Test
  public void testSteadyShot() {
    final Spell steadyShot = createHeroPowerAndBind(ConstSpell.STEADY_SHOT);
    final int damage = 2;
    assertThat(hero2.healthLoss()).isEqualTo(0);

    EffectFactory.pipeEffects(steadyShot, hero2);
    assertThat(hero2.healthLoss()).isEqualTo(damage);

    EffectFactory.pipeEffects(steadyShot, hero2);
    assertThat(hero2.healthLoss()).isEqualTo(damage * 2);
  }

  @Test
  public void testShapeshift() {
    final Spell shapeshift = createHeroPowerAndBind(ConstSpell.SHAPESHIFT);
    final int attack = 1;
    final int armor = 1;

    assertThat(hero1.attack().value()).isEqualTo(0);
    assertThat(hero1.armor().value()).isEqualTo(0);

    EffectFactory.pipeEffects(shapeshift, hero1);

    assertThat(hero1.attack().value()).isEqualTo(attack);
    assertThat(hero1.armor().value()).isEqualTo(armor);

    hero1.endTurn();
    assertThat(hero1.attack().value()).isEqualTo(0);
  }

  @Test
  public void testDaggerMastery() {
    final Spell daggerMastery = createHeroPowerAndBind(ConstSpell.DAGGER_MASTERY);
    assertThat(hero1.canDamage()).isFalse();
    EffectFactory.pipeEffects(daggerMastery, hero1);
    assertThat(hero1.canDamage()).isTrue();

    EffectFactory.AttackFactory.pipePhysicalDamageEffect(hero1, hero2);
    assertThat(hero2.healthLoss()).isEqualTo(1);
  }

  @Test
  public void testReinforce() {
    final Spell reinforce = createHeroPowerAndBind(ConstSpell.REINFORCE);
    assertThat(gm.activeSide.board.size()).isEqualTo(0);
    EffectFactory.pipeEffects(reinforce, hero1);
    assertThat(gm.activeSide.board.size()).isEqualTo(1);

    final Minion minion = gm.activeSide.board.get(0);
    assertThat(minion.cardName()).isEqualTo(ConstMinion.SILVER_HAND_RECRUIT.toString());
  }

  @Test
  public void testTotemicCall() {
    final Spell totemicCall = createHeroPowerAndBind(ConstSpell.TOTEMIC_CALL);
    final int size = 4;
    for (int i = 0; i < size; ++i) {
      EffectFactory.pipeEffects(totemicCall, hero1);
      assertThat(gm.activeSide.board.size()).isEqualTo(i + 1);
    }

    final int totemCount = gm.activeSide.board.stream()
        .map(minion -> minion.cardName())
        .collect(Collectors.toSet()).size();
    assertThat(totemCount).isEqualTo(size);
  }

  @Test
  public void testLifeTap() {
    final Spell lifeTap = createHeroPowerAndBind(ConstSpell.LIFE_TAP);
    final Minion yeti = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    final int damage = 2;

    assertThat(gm.activeSide.deck.size()).isEqualTo(0);
    gm.activeSide.deck.add(yeti);
    assertThat(gm.activeSide.deck.size()).isEqualTo(1);

    assertThat(gm.activeSide.hand.size()).isEqualTo(0);
    assertThat(hero1.healthLoss()).isEqualTo(0);

    EffectFactory.pipeEffects(lifeTap, hero1);

    assertThat(gm.activeSide.hand.size()).isEqualTo(1);
    assertThat(gm.activeSide.deck.size()).isEqualTo(0);
    assertThat(hero1.healthLoss()).isEqualTo(damage);
  }

  @Test
  public void testWildGrowth() {
    final Spell wildGrowth = createSpellAndBind(ConstSpell.WILD_GROWTH);
    final int manaCrystalCount = gm.activeSide.hero.manaCrystal().getCrystalUpperBound();
    gm.playCard(wildGrowth);
    assertThat(hero1.manaCrystal().getCrystalUpperBound()).isEqualTo(manaCrystalCount + 1);
  }

  @Test
  public void testInnervate() {
    final Spell innervate = createSpellAndBind(ConstSpell.INNERVATE);
    final int manaCrystalCount = hero1.manaCrystal().getCrystal();
    gm.playCard(innervate);
    assertThat(hero1.manaCrystal().getCrystal()).isEqualTo(manaCrystalCount + 2);
  }

  @Test
  public void testClaw() {
    final Spell claw = createSpellAndBind(ConstSpell.CLAW);
    final int attack = hero1.attack().value();
    final int armor = hero1.armor().value();

    gm.playCard(claw);

    assertThat(hero1.attack().value()).isEqualTo(attack + 2);
    assertThat(hero1.armor().value()).isEqualTo(armor + 2);

    hero1.endTurn();

    // Test that attack is valid only for one turn but armor gain is permanent.
    assertThat(hero1.attack().value()).isEqualTo(attack);
    assertThat(hero1.armor().value()).isEqualTo(armor + 2);
  }

  @Test
  public void testMarkOfTheWild() {
    final Spell markOfTheWild = createSpellAndBind(ConstSpell.MARK_OF_THE_WILD);
    final int attack = yeti.attack().value();
    final int health = yeti.health().value();
    final int maxHealth = yeti.maxHealth().value();
    assertThat(yeti.booleanMechanics().isOff(ConstMechanic.TAUNT));

    gm.playCard(markOfTheWild, yeti);

    assertThat(yeti.booleanMechanics().isOn(ConstMechanic.TAUNT));
    assertThat(yeti.attack().value()).isEqualTo(attack + 2);
    assertThat(yeti.health().value()).isEqualTo(health + 2);
    assertThat(yeti.maxHealth().value()).isEqualTo(maxHealth + 2);
  }

  @Test
  public void testSwipe() {
    final Spell swipe = createSpellAndBind(ConstSpell.SWIPE);

    gm.switchTurn();
    final Minion yeti1 = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(yeti1);
    gm.playCard(yeti1);
    final Minion yeti2 = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(yeti2);
    gm.playCard(yeti2);
    gm.switchTurn();

    gm.playCard(swipe, yeti1);

    assertThat(yeti1.healthLoss()).isEqualTo(4);
    assertThat(yeti2.healthLoss()).isEqualTo(1);
    assertThat(gm.inactiveSide.hero.healthLoss()).isEqualTo(1);
  }

  @Test
  public void testMultiShot() {
    final Spell multiShot = createSpellAndBind(ConstSpell.MULTI_SHOT);
    final int damage = 3;

    gm.switchTurn();
    final Minion yeti1 = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(yeti1);
    gm.playCard(yeti1);
    final Minion yeti2 = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(yeti2);
    gm.playCard(yeti2);
    gm.switchTurn();

    //gm.playCard(multiShot);
    //System.out.println(yeti1);
    //System.out.println(yeti2);
    //assertThat(yeti1.healthLoss()).isEqualTo(damage);
    //assertThat(yeti2.healthLoss()).isEqualTo(damage);
  }

  @Test
  public void testTracking() {
    // TODO: to be implemented
  }

  @Test
  public void testHuntersMark() {
    final Spell huntersMark = createSpellAndBind(ConstSpell.HUNTERS_MARK);
    final int value = 1;
    assertThat(yeti.health().value()).isGreaterThan(value);
    assertThat(yeti.maxHealth().value()).isGreaterThan(value);

    gm.playCard(huntersMark, yeti);

    assertThat(yeti.health().value()).isEqualTo(value);
    assertThat(yeti.maxHealth().value()).isEqualTo(value);
  }
}
