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
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.AttackFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.WeaponFactory;
import com.herthrone.game.Container;
import com.herthrone.game.GameManager;
import com.herthrone.game.Side;
import com.herthrone.object.BooleanAttribute;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
  private int initialBoardSize;

  @Before
  public void setUp() {
    this.gm = new GameManager(ConstHero.GARROSH_HELLSCREAM, ConstHero.GARROSH_HELLSCREAM, Collections.emptyList(), Collections.emptyList());
    this.hero = gm.activeSide.hero;
    this.activeSide = gm.activeSide;
    this.inactiveSide = gm.inactiveSide;

    activeSide.startTurn();
    this.yeti = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    gm.playCard(yeti);
    this.waterElemental = MinionFactory.create(ConstMinion.WATER_ELEMENTAL);
    gm.playCard(waterElemental);
    this.scarletCrusader = MinionFactory.create(ConstMinion.SCARLET_CRUSADER);
    gm.playCard(scarletCrusader);

    this.initialBoardSize = activeSide.board.size();
    activeSide.endTurn();
    activeSide.startTurn();
  }

  @Test
  public void testCharge() {
    final ConstMinion minionName = ConstMinion.WOLFRIDER;
    final MinionConfig config = ConfigLoader.getMinionConfigByName(minionName);
    final Optional<MechanicConfig> mechanic = config.getMechanic(ConstMechanic.CHARGE);
    assertThat(mechanic.isPresent()).isTrue();
    final Minion minion = MinionFactory.create(minionName);
    assertThat(minion.attackMovePoints().value()).isGreaterThan(0);
  }

  @Test
  public void testBattlecryDrawCardWithFatigue() {
    assertThat(activeSide.deck.size()).isEqualTo(0);
    assertThat(activeSide.hand.size()).isEqualTo(0);
    assertThat(activeSide.hero.healthLoss()).isEqualTo(0);

    final ConstMinion minionName = ConstMinion.GNOMISH_INVENTOR;
    final Minion minion = MinionFactory.create(minionName);

    gm.playCard(minion);

    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1);
    assertThat(activeSide.board.get(initialBoardSize).cardName()).isEqualTo(minionName.toString());
    assertThat(activeSide.deck.size()).isEqualTo(0);
    assertThat(activeSide.hand.size()).isEqualTo(0);
    // Battlecry draw card causing fatigue damage.
    assertThat(activeSide.hero.healthLoss()).isEqualTo(1);
  }

  @Test
  public void testElusive() {
    final Minion faerieDragon = MinionFactory.create(ConstMinion.FAERIE_DRAGON);
    assertThat(GameManager.isMinionTargetable(faerieDragon, activeSide.board, ConstType.SPELL)).isFalse();

    assertThat(GameManager.isMinionTargetable(yeti, activeSide.board, ConstType.SPELL)).isTrue();
  }

  @Test
  public void testTaunt() {
    final Minion senjin = MinionFactory.create(ConstMinion.SENJIN_SHIELDMASTA);
    final Minion grizzly = MinionFactory.create(ConstMinion.IRONFUR_GRIZZLY);
    final Minion junglePanther = MinionFactory.create(ConstMinion.JUNGLE_PANTHER);
    // Let jungle panther be both stealth and taunt.
    junglePanther.booleanMechanics().initialize(ConstMechanic.TAUNT);

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
    assertThat(scarletCrusader.booleanMechanics().get(ConstMechanic.DIVINE_SHIELD).isPresent()).isTrue();
    final BooleanAttribute divineShield = scarletCrusader.booleanMechanics().get(ConstMechanic.DIVINE_SHIELD).get();
    assertThat(divineShield.isOn()).isTrue();

    AttackFactory.getPhysicalDamageAction(yeti, scarletCrusader);

    // Yeti takes damage. Crusader takes no damage because of divine shield.
    assertThat(divineShield.isOn()).isFalse();
    assertThat(scarletCrusader.healthLoss()).isEqualTo(0);
    assertThat(yeti.healthLoss()).isGreaterThan(0);

    AttackFactory.getPhysicalDamageAction(yeti, scarletCrusader);

    // Crusader has no more divine shield and takes damage.
    assertThat(scarletCrusader.isDead()).isTrue();
    assertThat(yeti.isDead()).isTrue();
  }

  @Test
  public void testStealth() {
    final Minion stoneclawTotem = MinionFactory.create(ConstMinion.STONECLAW_TOTEM);
    gm.playCard(stoneclawTotem);
    final Minion worgenInfiltrator = MinionFactory.create(ConstMinion.WORGEN_INFILTRATOR);
    gm.playCard(worgenInfiltrator);

    final Optional<BooleanAttribute> stealth = worgenInfiltrator.booleanMechanics().get(ConstMechanic.STEALTH);
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
    assertThat(scarletCrusader.healthLoss()).isEqualTo(0);
    assertThat(scarletCrusader.booleanMechanics().get(ConstMechanic.FROZEN).isPresent()).isFalse();

    // Yeti takes damage and gets frozen.
    AttackFactory.getPhysicalDamageAction(waterElemental, yeti);
    final Optional<BooleanAttribute> frozen = yeti.booleanMechanics().get(ConstMechanic.FROZEN);
    assertThat(yeti.healthLoss()).isGreaterThan(0);
    assertThat(frozen.isPresent()).isTrue();
    assertThat(frozen.get().isOn()).isTrue();
  }

  @Test
  public void testFrozen() {
    AttackFactory.getPhysicalDamageAction(yeti, waterElemental);
    final Optional<BooleanAttribute> frozen = yeti.booleanMechanics().get(ConstMechanic.FROZEN);
    assertThat(frozen.isPresent()).isTrue();
    assertThat(frozen.get().isOn()).isTrue();

    AttackFactory.getPhysicalDamageAction(waterElemental, hero);

    final Optional<BooleanAttribute> heroFrozen = yeti.booleanMechanics().get(ConstMechanic.FROZEN);

    assertThat(heroFrozen.isPresent()).isTrue();

    // TODO: next round the frozen bool attribute should be unset(when startRound).
  }

  @Test
  public void testPoison() {
    Minion emperorCobra = MinionFactory.create(ConstMinion.EMPEROR_COBRA);
    gm.playCard(emperorCobra);

    AttackFactory.getPhysicalDamageAction(emperorCobra, hero);

    // Poison does not trigger destroy on Hero.
    assertThat(emperorCobra.healthLoss()).isEqualTo(0);
    assertThat(hero.isDead()).isFalse();

    // Point triggers destroy on Minion when minion is damaged.
    AttackFactory.getPhysicalDamageAction(emperorCobra, yeti);
    assertThat(emperorCobra.isDead()).isTrue();
    assertThat(yeti.healthLoss()).isGreaterThan(0);
    assertThat(yeti.isDead()).isTrue();

    emperorCobra = MinionFactory.create(ConstMinion.EMPEROR_COBRA);
    gm.playCard(emperorCobra);
    AttackFactory.getPhysicalDamageAction(emperorCobra, scarletCrusader);
    assertThat(emperorCobra.isDead()).isTrue();
    assertThat(scarletCrusader.healthLoss()).isEqualTo(0);
  }

  @Test
  public void testImmune() {
    // No minions so far has default immune mechanic yet.
    // Init IMMUNE for Yeti.
    yeti.booleanMechanics().initialize(ConstMechanic.IMMUNE);
    assertThat(GameManager.isMinionTargetable(yeti, activeSide.board, ConstType.ATTACK)).isFalse();
    assertThat(GameManager.isMinionTargetable(yeti, activeSide.board, ConstType.SPELL)).isFalse();

    // Test Hero immune.
    hero.booleanMechanics().initialize(ConstMechanic.IMMUNE);
    assertThat(GameManager.isHeroTargetable(hero, activeSide.board, ConstType.ATTACK)).isFalse();
    assertThat(GameManager.isHeroTargetable(hero, activeSide.board, ConstType.SPELL)).isFalse();
  }

  @Test
  public void testForgetful() {
    final Minion ogreBrute = MinionFactory.create(ConstMinion.OGRE_BRUTE);
    gm.playCard(ogreBrute);
    final int attackVal = ogreBrute.attack().value();
    final int minionNum = 5;
    final int total = 10000;
    final int buffHealth = total * 10;
    for (int i = 0; i < minionNum; ++i) {
      // Buff Yeti health enough so that it doesn't die and gets removed from board.
      final Minion yeti = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
      yeti.health().buff.temporaryBuff.increase(buffHealth);
      inactiveSide.board.add(yeti);
    }
    // TODO: find another way to test randomness or not to test it at all.
    final double jitter = .10;
    final double forgetfulFactor = .5;
    ogreBrute.health().buff.temporaryBuff.increase(buffHealth);

    for (int i = 0; i < total; ++i) {
      AttackFactory.getPhysicalDamageAction(ogreBrute, inactiveSide.hero);
    }
    Range<Double> mainTargetGotAttackedNumRange = Range.closed(total * forgetfulFactor * (1 - jitter), total * forgetfulFactor * (1 + jitter));
    Range<Double> otherTargetsGotAttackedNumRange = Range.closed(total * forgetfulFactor * (1 - jitter) / minionNum, total * forgetfulFactor * (1 + jitter) / minionNum);
    final double numOfHeroGotAttacked = inactiveSide.hero.healthLoss() / attackVal;
    assertThat(mainTargetGotAttackedNumRange.contains(numOfHeroGotAttacked)).isTrue();
    for (int i = 0; i < minionNum; ++i) {
      final double numGetAttacked = (buffHealth + inactiveSide.board.get(i).healthLoss()) / attackVal;
      assertThat(otherTargetsGotAttackedNumRange.contains(numGetAttacked)).isTrue();
    }
  }

  @Test
  public void testWindFury() {
    final Minion harpy = MinionFactory.create(ConstMinion.WINDFURY_HARPY);
    harpy.attackMovePoints().reset();
    assertThat(harpy.attackMovePoints().value()).isEqualTo(2);
  }

  @Test
  public void testBattlecry() {
    final ConstMinion minionInDeck = ConstMinion.FAERIE_DRAGON;
    activeSide.deck.add(MinionFactory.create(minionInDeck));

    assertThat(activeSide.deck.size()).isEqualTo(1);
    assertThat(activeSide.hand.size()).isEqualTo(0);

    final ConstMinion minionName = ConstMinion.GNOMISH_INVENTOR;
    final Minion minion = MinionFactory.create(minionName);

    gm.playCard(minion);

    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1);
    assertThat(activeSide.board.get(initialBoardSize).cardName()).isEqualTo(minionName.toString());
    assertThat(activeSide.deck.size()).isEqualTo(0);
    assertThat(activeSide.hand.size()).isEqualTo(1);
    assertThat(activeSide.hand.get(0).cardName()).isEqualTo(minionInDeck.toString());
  }

  @Test
  public void testDeathrattle() {
    final ConstMinion minionInDeck = ConstMinion.FAERIE_DRAGON;
    activeSide.deck.add(MinionFactory.create(minionInDeck));

    final Minion lootHoarder = MinionFactory.create(ConstMinion.LOOT_HOARDER);
    gm.playCard(lootHoarder);
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1);

    assertThat(activeSide.deck.size()).isEqualTo(1);
    assertThat(activeSide.hand.size()).isEqualTo(0);

    lootHoarder.takeDamage(1);
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize);
    assertThat(activeSide.hand.size()).isEqualTo(1);
    assertThat(activeSide.hand.get(0).cardName()).isEqualTo(minionInDeck.toString());
  }

  @Test
  public void testCombo() {
    final Minion defiasRingleader1 = MinionFactory.create(ConstMinion.DEFIAS_RINGLEADER);
    final Minion defiasRingleader2 = MinionFactory.create(ConstMinion.DEFIAS_RINGLEADER);
    gm.playCard(defiasRingleader1);
    // First play should not trigger combo effect hence add onl one minion to the board.
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1);

    gm.playCard(defiasRingleader2);

    // Second play should trigger combo effect hence summoning DEFIAS_BANDIT.
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 3);
    assertThat(activeSide.board.get(activeSide.board.size() - 1).cardName()).isEqualTo(ConstMinion.DEFIAS_BANDIT.toString());
  }

  @Test
  public void testOverload() {
    // Turn 1.
    activeSide.manaCrystal.startTurn();
    assertThat(activeSide.manaCrystal.getCrystal()).isEqualTo(1);
    // Turn 2.
    activeSide.manaCrystal.startTurn();
    assertThat(activeSide.manaCrystal.getCrystal()).isEqualTo(2);

    final Weapon stormforgedAxe = WeaponFactory.create(ConstWeapon.STORMFORGED_AXE);

    assertThat(hero.canDamage()).isFalse();
    hero.playToEquip(stormforgedAxe);
    assertThat(hero.canDamage()).isTrue();

    // Turn 3.
    activeSide.manaCrystal.startTurn();
    assertThat(activeSide.manaCrystal.getCrystal()).isEqualTo(1);
  }

  @Test
  public void testDealDamage() {
    final Minion knifeJuggler = MinionFactory.create(ConstMinion.KNIFE_JUGGLER);

    gm.playCard(knifeJuggler);

    final int numOfYetiToSummon = 5;
    for (int i = 0; i < numOfYetiToSummon; ++i) {
      gm.playCard(MinionFactory.create(ConstMinion.CHILLWIND_YETI));
      assertThat(inactiveSide.hero.healthLoss()).isEqualTo(i + 1);
    }
  }

  @Test
  public void testTakeControl() {
    gm.switchTurn();
    final int threshold = 4;
    for (int i = 0; i < threshold; ++i) {
      gm.playCard(MinionFactory.create(ConstMinion.CHILLWIND_YETI));
    }
    final List<Minion> opponentMinions = new ArrayList<>(inactiveSide.board.asList());
    // Test take control effect triggered because it satisfies the condition.
    gm.switchTurn();
    gm.playCard(MinionFactory.create(ConstMinion.MIND_CONTROL_TECH));

    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 2);
    assertThat(inactiveSide.board.size()).isEqualTo(threshold - 1);
    // Test the right-most minion is stolen from opponent board.
    assertThat(activeSide.board.get(activeSide.board.size() - 1)).isIn(opponentMinions);

    gm.playCard(MinionFactory.create(ConstMinion.MIND_CONTROL_TECH));
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 2 + 1);
    // Test control effect not triggered because of opponent has less than 4 minions.
    assertThat(inactiveSide.board.size()).isEqualTo(threshold - 1);
  }

  @Test
  public void testInspire() {
    final Minion recruiter = MinionFactory.create(ConstMinion.RECRUITER);
    gm.playCard(recruiter);

    assertThat(activeSide.hand.size()).isEqualTo(0);
    hero.useHeroPower(hero);

    assertThat(activeSide.hand.size()).isEqualTo(1);
  }

  @Test
  public void testReturnToHand() {
    final Minion youthfulBrewmaster = MinionFactory.create(ConstMinion.YOUTHFUL_BREWMASTER);

    assertThat(activeSide.hand.size()).isEqualTo(0);
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize);
    gm.playCard(youthfulBrewmaster, yeti);
    // Play one minion and another minion got returned.
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1 - 1);
    assertThat(activeSide.hand.size()).isEqualTo(1);
    assertThat(activeSide.hand.get(0).cardName()).isEqualTo(ConstMinion.CHILLWIND_YETI.toString());
  }
}
