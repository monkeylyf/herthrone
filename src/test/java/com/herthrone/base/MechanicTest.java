package com.herthrone.base;

import com.google.common.base.Optional;
import com.google.common.collect.Range;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstType;
import com.herthrone.factory.AttackFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.game.Battlefield;
import com.herthrone.game.Container;
import com.herthrone.game.GameManager;
import com.herthrone.game.Side;
import com.herthrone.stats.BooleanAttribute;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by yifengliu on 5/10/16.
 */
public class MechanicTest extends TestCase {

  private Hero hero;
  private Minion yeti;
  private Minion waterElemental;
  private Minion scarletCrusader;
  private GameManager gm;
  private Side activeSide;
  private Side inactiveSide;


  @Before
  public void setUp() {
    this.gm = new GameManager(ConstHero.GARROSH_HELLSCREAM, ConstHero.GARROSH_HELLSCREAM,
        Collections.emptyList(), Collections.emptyList());
    this.hero = gm.activeSide.hero;
    this.activeSide = gm.activeSide;
    this.inactiveSide = gm.inactiveSide;

    this.yeti = MinionFactory.createMinionByName(ConstMinion.CHILLWIND_YETI, activeSide);
    this.waterElemental = MinionFactory.createMinionByName(ConstMinion.WATER_ELEMENTAL, activeSide);
    this.scarletCrusader = MinionFactory.createMinionByName(ConstMinion.SCARLET_CRUSADER, activeSide);
  }

  @Test
  public void testCharge() {
    final ConstMinion minionName = ConstMinion.WOLFRIDER;
    final MinionConfig config = ConfigLoader.getMinionConfigByName(minionName);
    final Optional<MechanicConfig> mechanic = config.getMechanic(ConstMechanic.CHARGE);
    assertThat(mechanic.isPresent()).isTrue();
    final Minion minion = MinionFactory.createMinionByName(minionName);
    assertThat(minion.getAttackMovePoints().getVal()).isGreaterThan(0);
  }

  @Test
  public void testBattlecryDrawCardWithFatigue() {
    assertThat(activeSide.deck.size()).isEqualTo(0);
    assertThat(activeSide.board.size()).isEqualTo(0);
    assertThat(activeSide.hand.size()).isEqualTo(0);
    assertThat(activeSide.hero.getHealthLoss()).isEqualTo(0);

    final ConstMinion minionName = ConstMinion.GNOMISH_INVENTOR;
    final Minion minion = MinionFactory.createMinionByName(minionName, activeSide);

    gm.playCard(minion);

    assertThat(activeSide.board.get(0).getCardName()).isEqualTo(minionName.toString());
    assertThat(activeSide.deck.size()).isEqualTo(0);
    assertThat(activeSide.board.size()).isEqualTo(1);
    assertThat(activeSide.hand.size()).isEqualTo(0);
    // Battlecry draw card causing fatigue damage.
    assertThat(activeSide.hero.getHealthLoss()).isEqualTo(1);
  }

  @Test
  public void testElusive() {
    final Minion faerieDragon = MinionFactory.createMinionByName(ConstMinion.FAERIE_DRAGON);
    assertThat(GameManager.isMinionTargetable(faerieDragon, activeSide.board, ConstType.SPELL))
        .isFalse();

    assertThat(GameManager.isMinionTargetable(yeti, activeSide.board, ConstType.SPELL)).isTrue();
  }

  @Test
  public void testTaunt() {
    final Minion senjin = MinionFactory.createMinionByName(ConstMinion.SENJIN_SHIELDMASTA);
    final Minion grizzly = MinionFactory.createMinionByName(ConstMinion.IRONFUR_GRIZZLY);
    final Minion junglePanther = MinionFactory.createMinionByName(ConstMinion.JUNGLE_PANTHER);
    // Let jungle panther be both stealth and taunt.
    junglePanther.getBooleanMechanics().initialize(ConstMechanic.TAUNT);

    final Container<Minion> board = activeSide.board;

    board.add(yeti);
    board.add(senjin);
    board.add(grizzly);

    assertThat(GameManager.isMinionTargetable(yeti, board, ConstType.ATTACK)).isFalse();
    assertThat(GameManager.isHeroTargetable(hero, board, ConstType.ATTACK)).isFalse();
    assertThat(GameManager.isMinionTargetable(senjin, board, ConstType.ATTACK)).isTrue();
    assertThat(GameManager.isMinionTargetable(grizzly, board, ConstType.ATTACK)).isTrue();

    board.remove(senjin);
    board.remove(grizzly);
    board.add(junglePanther);

    // Yeti and another minion with both stealth and taunt on board. Yeti should be targetable
    // because stealth prevents taunt prevents Yeti being targeted.
    assertThat(GameManager.isMinionTargetable(yeti, board, ConstType.ATTACK)).isTrue();
    assertThat(GameManager.isMinionTargetable(junglePanther, board, ConstType.ATTACK)).isFalse();
  }

  @Test
  public void testDivineShield() {
    assertThat(scarletCrusader.getBooleanMechanics().get(ConstMechanic.DIVINE_SHIELD).isPresent())
        .isTrue();
    final BooleanAttribute divineShield = scarletCrusader.getBooleanMechanics().get(
        ConstMechanic.DIVINE_SHIELD).get();
    assertThat(divineShield.isOn()).isTrue();

    AttackFactory.getPhysicalDamageAction(yeti, scarletCrusader);

    // Yeti takes damage. Crusader takes no damage because of divine shield.
    assertThat(divineShield.isOn()).isFalse();
    assertThat(scarletCrusader.getHealthLoss()).isEqualTo(0);
    assertThat(yeti.getHealthLoss()).isGreaterThan(0);

    AttackFactory.getPhysicalDamageAction(yeti, scarletCrusader);

    // Crusader has no more divine shield and takes damage.
    assertThat(scarletCrusader.isDead()).isTrue();
    assertThat(yeti.isDead()).isTrue();
  }

  @Test
  public void testStealth() {
    final Minion stoneclawTotem = MinionFactory.createMinionByName(
        ConstMinion.STONECLAW_TOTEM, activeSide);
    final Minion worgenInfiltrator = MinionFactory.createMinionByName(
        ConstMinion.WORGEN_INFILTRATOR, activeSide);

    final Optional<BooleanAttribute> stealth = worgenInfiltrator.getBooleanMechanics().get
        (ConstMechanic.STEALTH);
    assertThat(stealth.isPresent()).isTrue();
    assertThat(stealth.get().isOn()).isTrue();

    AttackFactory.getPhysicalDamageAction(worgenInfiltrator, stoneclawTotem);

    // Stealth deactivated after attack.
    assertThat(stealth.isPresent()).isTrue();
    assertThat(stealth.get().isOn()).isFalse();
  }

  @Test
  public void testFreeze() {
    // Scarlet crusader has divine shield so take no damage. No damage no frozen.
    AttackFactory.getPhysicalDamageAction(waterElemental, scarletCrusader);
    assertThat(scarletCrusader.getHealthLoss()).isEqualTo(0);
    assertThat(scarletCrusader.getBooleanMechanics().get(ConstMechanic.FROZEN).isPresent()).isFalse();

    // Yeti takes damage and gets frozen.
    AttackFactory.getPhysicalDamageAction(waterElemental, yeti);
    final Optional<BooleanAttribute> frozen = yeti.getBooleanMechanics().get(ConstMechanic.FROZEN);
    assertThat(yeti.getHealthLoss()).isGreaterThan(0);
    assertThat(frozen.isPresent()).isTrue();
    assertThat(frozen.get().isOn()).isTrue();
  }

  @Test
  public void testFrozen() {
    AttackFactory.getPhysicalDamageAction(yeti, waterElemental);
    final Optional<BooleanAttribute> frozen = yeti.getBooleanMechanics().get(ConstMechanic.FROZEN);
    assertThat(frozen.isPresent()).isTrue();
    assertThat(frozen.get().isOn()).isTrue();

    AttackFactory.getPhysicalDamageAction(waterElemental, hero);

    final Optional<BooleanAttribute> heroFrozen = yeti.getBooleanMechanics()
        .get(ConstMechanic.FROZEN);

    assertThat(heroFrozen.isPresent()).isTrue();

    // TODO: next round the frozen bool attribute should be unset(when startRound).
  }

  @Test
  public void testPoison() {
    Minion emperorCobra = MinionFactory.createMinionByName(ConstMinion.EMPEROR_COBRA, activeSide);
    AttackFactory.getPhysicalDamageAction(emperorCobra, hero);

    // Poison does not trigger destroy on Hero.
    assertThat(emperorCobra.getHealthLoss()).isEqualTo(0);
    assertThat(hero.isDead()).isFalse();

    // Point triggers destroy on Minion when minion is damaged.
    AttackFactory.getPhysicalDamageAction(emperorCobra, yeti);
    assertThat(emperorCobra.isDead()).isTrue();
    assertThat(yeti.getHealthLoss()).isGreaterThan(0);
    assertThat(yeti.isDead()).isTrue();

    emperorCobra = MinionFactory.createMinionByName(ConstMinion.EMPEROR_COBRA, activeSide);
    AttackFactory.getPhysicalDamageAction(emperorCobra, scarletCrusader);
    assertThat(emperorCobra.isDead()).isTrue();
    assertThat(scarletCrusader.getHealthLoss()).isEqualTo(0);
  }

  @Test
  public void testImmune() {
    // No minions so far has default immune mechanic yet.
    // Init IMMUNE for Yeti.
    yeti.getBooleanMechanics().initialize(ConstMechanic.IMMUNE);
    assertThat(GameManager.isMinionTargetable(yeti, activeSide.board, ConstType.ATTACK)).isFalse();
    assertThat(GameManager.isMinionTargetable(yeti, activeSide.board, ConstType.SPELL)).isFalse();

    // Test Hero immune.
    hero.getBooleanMechanics().initialize(ConstMechanic.IMMUNE);
    assertThat(GameManager.isHeroTargetable(hero, activeSide.board, ConstType.ATTACK)).isFalse();
    assertThat(GameManager.isHeroTargetable(hero, activeSide.board, ConstType.SPELL)).isFalse();
  }

  @Test
  public void testForgetful() {
    final Minion ogreBrute = MinionFactory.createMinionByName(ConstMinion.OGRE_BRUTE, activeSide);
    final int attackVal = ogreBrute.getAttackAttr().getVal();
    final int minionNum = 5;
    for (int i = 0; i < minionNum; ++i) {
      inactiveSide.board.add(
          MinionFactory.createMinionByName(ConstMinion.CHILLWIND_YETI, inactiveSide));
    }
    final int total = 10000;
    // TODO: find another way to test randomness or not to test it at all.
    final double jitter = .10;
    final double forgetfulFactor = .5;

    for (int i = 0; i < total; ++i) {
      AttackFactory.getPhysicalDamageAction(ogreBrute, inactiveSide.hero);
    }
    Range<Double> mainTargetGotAttackedNumRange = Range.closed(
        total * forgetfulFactor * (1 - jitter),
        total * forgetfulFactor * (1 + jitter));
    Range<Double> otherTargetsGotAttackedNumRange = Range.closed(
        total * forgetfulFactor * (1 - jitter) / minionNum,
        total * forgetfulFactor * (1 + jitter) / minionNum);
    final double numOfHeroGotAttacked = inactiveSide.hero.getHealthLoss() / attackVal;
    assertThat(mainTargetGotAttackedNumRange.contains(numOfHeroGotAttacked)).isTrue();
    for (int i = 0; i < minionNum; ++i) {
      final double numGetAttacked = inactiveSide.board.get(i).getHealthLoss() / attackVal;
      assertThat(otherTargetsGotAttackedNumRange.contains(numGetAttacked)).isTrue();
    }
  }

  @Test
  public void testWindFury() {
    final Minion harpy = MinionFactory.createMinionByName(ConstMinion.WINDFURY_HARPY, activeSide);
    harpy.getAttackMovePoints().reset();
    assertThat(harpy.getAttackMovePoints().getVal()).isEqualTo(2);
  }

  @Test
  public void testBattlecry() {
    activeSide.deck.add(yeti);

    assertThat(activeSide.deck.size()).isEqualTo(1);
    assertThat(activeSide.board.size()).isEqualTo(0);
    assertThat(activeSide.hand.size()).isEqualTo(0);

    final ConstMinion minionName = ConstMinion.GNOMISH_INVENTOR;
    final Minion minion = MinionFactory.createMinionByName(minionName, activeSide);

    gm.playCard(minion);

    assertThat(activeSide.board.get(0).getCardName()).isEqualTo(minionName.toString());
    assertThat(activeSide.deck.size()).isEqualTo(0);
    assertThat(activeSide.board.size()).isEqualTo(1);
    assertThat(activeSide.hand.size()).isEqualTo(1);
  }

  @Test
  public void testDeathrattle() {

  }
}
