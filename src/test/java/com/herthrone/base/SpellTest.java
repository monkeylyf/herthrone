package com.herthrone.base;

import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstHero;
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

/**
 * Created by yifeng on 4/20/16.
 */
@RunWith(JUnit4.class)
public class SpellTest extends TestCase {

  private Hero hero1;
  private Hero hero2;
  private MinionConfig yetiConfig;
  private Minion minion;
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

    this.yetiConfig = ConfigLoader.getMinionConfigByName(ConstMinion.CHILLWIND_YETI);
    this.minion = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(minion);
  }

  @Test
  public void testFireBall() {
    final Spell fireBall = createSpellAndBind(ConstSpell.FIRE_BALL);
    EffectFactory.pipeEffects(fireBall, minion);
    assertThat(minion.health().value()).isEqualTo(yetiConfig.health + fireBall.getEffects().get(0).value);
    assertThat(minion.isDead()).isTrue();
  }

  @Test
  public void testArmorUp() {
    assertThat(hero1.armor().value()).isEqualTo(0);
    final Spell armorUp = createHeroPowerAndBind(ConstSpell.ARMOR_UP);
    EffectFactory.pipeEffects(armorUp, hero1);
    assertThat(hero1.armor().value()).isEqualTo(armorUp.getEffects().get(0).value);
  }

  @Test
  public void testLesserHeal() {
    final Spell lesserHeal = createHeroPowerAndBind(ConstSpell.LESSER_HEAL);
    assertThat(hero1.healthLoss()).isEqualTo(0);
    final int largeDamage = 5;
    final int healVol = lesserHeal.getEffects().get(0).value;
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
    final int damage = fireBlast.getEffects().get(0).value;
    assertThat(hero2.healthLoss()).isEqualTo(0);

    EffectFactory.pipeEffects(fireBlast, hero2);
    assertThat(hero2.healthLoss()).isEqualTo(-damage);

    EffectFactory.pipeEffects(fireBlast, hero2);
    assertThat(hero2.healthLoss()).isEqualTo(-damage * 2);
  }

  @Test
  public void testSteadyShot() {
    final Spell steadyShot = createHeroPowerAndBind(ConstSpell.STEADY_SHOT);
    final int damage = steadyShot.getEffects().get(0).value;
    assertThat(hero2.healthLoss()).isEqualTo(0);

    EffectFactory.pipeEffects(steadyShot, hero2);
    assertThat(hero2.healthLoss()).isEqualTo(-damage);

    EffectFactory.pipeEffects(steadyShot, hero2);
    assertThat(hero2.healthLoss()).isEqualTo(-damage * 2);
  }

  @Test
  public void testShapeshift() {
    final Spell shapeshift = createHeroPowerAndBind(ConstSpell.SHAPESHIFT);
    final int attack = shapeshift.getEffects().get(0).value;
    final int armor = shapeshift.getEffects().get(1).value;

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
    assertThat(daggerMastery.getEffects().get(0).value).isEqualTo(hero2.healthLoss());
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
    final int size = totemicCall.getEffects().get(0).choices.size();
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
    final int damage = -lifeTap.getEffects().get(0).value;

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
    assertThat(gm.activeSide.hero.manaCrystal().getCrystalUpperBound())
        .isEqualTo(manaCrystalCount + 1);
  }

  @Test
  public void testInnervate() {
    final Spell innervate = createSpellAndBind(ConstSpell.INNERVATE);
    final int manaCrystalCount = gm.activeSide.hero.manaCrystal().getCrystal();
    gm.playCard(innervate);
    assertThat(gm.activeSide.hero.manaCrystal().getCrystal()).isEqualTo(manaCrystalCount + 2);
  }
}
