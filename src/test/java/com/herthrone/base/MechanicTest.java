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
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.game.Battlefield;
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
  private Side side;
  private AttackFactory attackFactory;
  private Battlefield battlefield;
  private MinionFactory minionFactory;
  private EffectFactory effectFactory;

  private GameManager gm;

  private Minion yeti;

  @Before
  public void setUp() {
    this.gm = new GameManager(ConstHero.GARROSH_HELLSCREAM, ConstHero.GARROSH_HELLSCREAM, Collections.emptyList(), Collections.emptyList());
    this.side = gm.battlefield1.mySide;
    this.hero = side.hero;
    this.battlefield = gm.battlefield1;
    this.attackFactory = gm.factory1.attackFactory;
    this.minionFactory = gm.factory1.minionFactory;
    this.effectFactory = gm.factory1.effectFactory;

    this.yeti = minionFactory.createMinionByName(ConstMinion.CHILLWIND_YETI);
  }

  @Test
  public void testCharge() {
    final ConstMinion minionName = ConstMinion.WOLFRIDER;
    final MinionConfig config = ConfigLoader.getMinionConfigByName(minionName);
    final Optional<MechanicConfig> mechanic = config.getMechanic(ConstMechanic.CHARGE);
    assertThat(mechanic.isPresent()).isTrue();
    final Minion minion = minionFactory.createMinionByName(minionName);
    assertThat(minion.getAttackMovePoints().getVal()).isGreaterThan(0);
  }

  @Test
  public void testBattlecryDrawCardWithFatigue() {
    assertThat(side.deck.size()).isEqualTo(0);
    assertThat(side.board.size()).isEqualTo(0);
    assertThat(side.hand.size()).isEqualTo(0);
    assertThat(side.hero.getHealthLoss()).isEqualTo(0);

    final ConstMinion minionName = ConstMinion.GNOMISH_INVENTOR;
    final Minion minion = minionFactory.createMinionByName(ConstMinion.GNOMISH_INVENTOR);

    gm.playCard(minion);

    assertThat(side.board.get(0).getCardName()).isEqualTo(minionName.toString());
    assertThat(side.deck.size()).isEqualTo(0);
    assertThat(side.board.size()).isEqualTo(1);
    assertThat(side.hand.size()).isEqualTo(0);
    // Battlecry draw card causing fatigue damage.
    assertThat(side.hero.getHealthLoss()).isEqualTo(1);
  }

  @Test
  public void testElusive() {
    final Minion faerieDragon = minionFactory.createMinionByName(ConstMinion.FAERIE_DRAGON);
    assertThat(GameManager.isMinionTargetable(faerieDragon, side.board, ConstType.SPELL)).isFalse();

    assertThat(GameManager.isMinionTargetable(yeti, side.board, ConstType.SPELL)).isTrue();
  }

  @Test
  public void testTaunt() {
    final Minion senjin = minionFactory.createMinionByName(ConstMinion.SENJIN_SHIELDMASTA);
    final Minion grizzly = minionFactory.createMinionByName(ConstMinion.IRONFUR_GRIZZLY);
    side.board.add(yeti);
    side.board.add(senjin);
    side.board.add(grizzly);

    assertThat(GameManager.isMinionTargetable(yeti, side.board, ConstType.ATTACK)).isFalse();
    assertThat(GameManager.isHeroTargetable(hero, side.board, ConstType.ATTACK)).isFalse();
    assertThat(GameManager.isMinionTargetable(senjin, side.board, ConstType.ATTACK)).isTrue();
    assertThat(GameManager.isMinionTargetable(grizzly, side.board, ConstType.ATTACK)).isTrue();
  }

  @Test
  public void testDivineShield() {
    final Minion yeti = minionFactory.createMinionByName(ConstMinion.CHILLWIND_YETI);
    final Minion scarletCrusader = minionFactory.createMinionByName(ConstMinion.SCARLET_CRUSADER);

    assertThat(scarletCrusader.getBooleanMechanics().get(ConstMechanic.DIVINE_SHIELD).isPresent()).isTrue();
    final BooleanAttribute divineShield = scarletCrusader.getBooleanMechanics().get(ConstMechanic
        .DIVINE_SHIELD).get();
    assertThat(divineShield.isOn()).isTrue();

    attackFactory.getPhysicalDamageAction(yeti, scarletCrusader);

    // Yeti takes damage. Crusader takes no damage because of divine shield.
    assertThat(divineShield.isOn()).isFalse();
    assertThat(scarletCrusader.getHealthLoss()).isEqualTo(0);
    assertThat(yeti.getHealthLoss()).isGreaterThan(0);

    attackFactory.getPhysicalDamageAction(yeti, scarletCrusader);

    // Crusader has no more divine shield and takes damage.
    assertThat(scarletCrusader.isDead()).isTrue();
    assertThat(yeti.isDead()).isTrue();
  }

  @Test
  public void testStealth() {
    final Minion stoneclawTotem = minionFactory.createMinionByName(ConstMinion.STONECLAW_TOTEM);
    final Minion worgenInfiltrator = minionFactory.createMinionByName(ConstMinion
        .WORGEN_INFILTRATOR);

    final Optional<BooleanAttribute> stealth = worgenInfiltrator.getBooleanMechanics().get
        (ConstMechanic.STEALTH);
    assertThat(stealth.isPresent()).isTrue();
    assertThat(stealth.get().isOn()).isTrue();

    attackFactory.getPhysicalDamageAction(worgenInfiltrator, stoneclawTotem);

    // Stealth deactivated after attack.
    assertThat(stealth.isPresent()).isTrue();
    assertThat(stealth.get().isOn()).isFalse();
  }

  @Test
  public void testFreeze() {
    final Minion waterElemental = minionFactory.createMinionByName(ConstMinion.WATER_ELEMENTAL);
    final Minion scarletCrusader = minionFactory.createMinionByName(ConstMinion.SCARLET_CRUSADER);

    // Scarlet crusader has divine shield so take no damage. No damage no frozen.
    attackFactory.getPhysicalDamageAction(waterElemental, scarletCrusader);
    assertThat(scarletCrusader.getHealthLoss()).isEqualTo(0);
    assertThat(scarletCrusader.getBooleanMechanics().get(ConstMechanic.FROZEN).isPresent()).isFalse();

    // Yeti takes damage and gets frozen.
    attackFactory.getPhysicalDamageAction(waterElemental, yeti);
    final Optional<BooleanAttribute> frozen = yeti.getBooleanMechanics().get(ConstMechanic.FROZEN);
    assertThat(yeti.getHealthLoss()).isGreaterThan(0);
    assertThat(frozen.isPresent()).isTrue();
    assertThat(frozen.get().isOn()).isTrue();
  }

  @Test
  public void testFrozen() {
    final Minion waterElemental = minionFactory.createMinionByName(ConstMinion.WATER_ELEMENTAL);

    attackFactory.getPhysicalDamageAction(yeti, waterElemental);
    final Optional<BooleanAttribute> frozen = yeti.getBooleanMechanics().get(ConstMechanic.FROZEN);
    assertThat(frozen.isPresent()).isTrue();
    assertThat(frozen.get().isOn()).isTrue();

    attackFactory.getPhysicalDamageAction(waterElemental, hero);

    final Optional<BooleanAttribute> heroFrozen = yeti.getBooleanMechanics().get(ConstMechanic
        .FROZEN);

    assertThat(heroFrozen.isPresent()).isTrue();

    // TODO: next round the frozen bool attribute should be unset(when startRound).
  }

  @Test
  public void testPoison() {
    Minion emperorCobra = minionFactory.createMinionByName(ConstMinion.EMPEROR_COBRA);
    attackFactory.getPhysicalDamageAction(emperorCobra, hero);

    // Poison does not trigger destroy on Hero.
    assertThat(emperorCobra.getHealthLoss()).isEqualTo(0);
    assertThat(hero.isDead()).isFalse();

    // Point triggers destroy on Minion when minion is damaged.
    attackFactory.getPhysicalDamageAction(emperorCobra, yeti);
    assertThat(emperorCobra.isDead()).isTrue();
    assertThat(yeti.getHealthLoss()).isGreaterThan(0);
    assertThat(yeti.isDead()).isTrue();

    final Minion scarletCrusader = minionFactory.createMinionByName(ConstMinion.SCARLET_CRUSADER);
    emperorCobra = minionFactory.createMinionByName(ConstMinion.EMPEROR_COBRA);
    attackFactory.getPhysicalDamageAction(emperorCobra, scarletCrusader);
    assertThat(emperorCobra.isDead()).isTrue();
    assertThat(scarletCrusader.getHealthLoss()).isEqualTo(0);
  }

  @Test
  public void testImmune() {
    // No minions so far has default immune mechanic yet.
    // Init IMMUNE for Yeti.
    yeti.getBooleanMechanics().initialize(ConstMechanic.IMMUNE);
    assertThat(GameManager.isMinionTargetable(yeti, gm.battlefield1.mySide.board, ConstType.ATTACK))
        .isFalse();
    assertThat(GameManager.isMinionTargetable(yeti, gm.battlefield1.mySide.board, ConstType.SPELL))
        .isFalse();

    // Test Hero immune.
    hero.getBooleanMechanics().initialize(ConstMechanic.IMMUNE);
    assertThat(GameManager.isHeroTargetable(hero, gm.battlefield1.mySide.board, ConstType.ATTACK))
        .isFalse();
    assertThat(GameManager.isHeroTargetable(hero, gm.battlefield1.mySide.board, ConstType.SPELL))
        .isFalse();
  }

  @Test
  public void testForgetful() {
    final Minion ogreBrute = minionFactory.createMinionByName(ConstMinion.OGRE_BRUTE);
    final int attackVal = ogreBrute.getAttackAttr().getVal();
    final int minionNum = 5;
    final Side opponentSide = battlefield.opponentSide;
    for (int i = 0; i < minionNum; ++i) {
      opponentSide.board.add(minionFactory.createMinionByName(
          ConstMinion.CHILLWIND_YETI));
    }
    final int total = 10000;
    // TODO: find another way to test randomness or not to test it at all.
    final double jitter = .10;
    final double forgetfulFactor = .5;

    for (int i = 0; i < total; ++i) {
      attackFactory.getPhysicalDamageAction(ogreBrute, opponentSide.hero);
    }
    Range<Double> mainTargetGotAttackedNumRange = Range.closed(
        total * forgetfulFactor * (1 - jitter),
        total * forgetfulFactor * (1 + jitter));
    Range<Double> otherTargetsGotAttackedNumRange = Range.closed(
        total * forgetfulFactor * (1 - jitter) / minionNum,
        total * forgetfulFactor * (1 + jitter) / minionNum);
    final double numOfHeroGotAttacked = opponentSide.hero.getHealthLoss() / attackVal;
    assertThat(mainTargetGotAttackedNumRange.contains(numOfHeroGotAttacked)).isTrue();
    for (int i = 0; i < minionNum; ++i) {
      final double numGetAttacked = opponentSide.board.get(i).getHealthLoss() / attackVal;
      assertThat(otherTargetsGotAttackedNumRange.contains(numGetAttacked)).isTrue();
    }
  }

  @Test
  public void testWindFury() {
    final Minion harpy = minionFactory.createMinionByName(ConstMinion.WINDFURY_HARPY);
    harpy.getAttackMovePoints().reset();
    assertThat(harpy.getAttackMovePoints().getVal()).isEqualTo(2);
  }

  @Test
  public void testBattlecry() {
    side.deck.add(yeti);

    assertThat(side.deck.size()).isEqualTo(1);
    assertThat(side.board.size()).isEqualTo(0);
    assertThat(side.hand.size()).isEqualTo(0);

    final ConstMinion minionName = ConstMinion.GNOMISH_INVENTOR;
    final Minion minion = minionFactory.createMinionByName(minionName);

    gm.playCard(minion);

    assertThat(side.board.get(0).getCardName()).isEqualTo(minionName.toString());
    assertThat(side.deck.size()).isEqualTo(0);
    assertThat(side.board.size()).isEqualTo(1);
    assertThat(side.hand.size()).isEqualTo(1);
  }

  @Test
  public void testDeathrattle() {

  }
}
