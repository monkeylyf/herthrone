package com.herthrone.base;

import com.google.common.collect.Range;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.SpellFactory;
import com.herthrone.factory.TargetFactory;
import com.herthrone.factory.WeaponFactory;
import com.herthrone.game.Container;
import com.herthrone.game.Game;
import com.herthrone.game.Side;
import com.herthrone.object.ManaCrystal;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class MechanicTest extends TestCase {

  private Hero hero;
  private Minion yeti;
  private Minion waterElemental;
  private Minion scarletCrusader;
  private Game gm;
  private Side activeSide;
  private Side inactiveSide;
  private int initialBoardSize;

  @Before
  public void setUp() {
    //final List<Enum> cards = Collections.nCopies(30, ConstMinion.CHILLWIND_YETI);
    final List<Enum> cards = Collections.emptyList();
    this.gm = new Game("gameId", ConstHero.GULDAN, ConstHero.GULDAN, cards, cards);
    this.hero = gm.activeSide.hero;
    this.activeSide = gm.activeSide;
    this.inactiveSide = gm.inactiveSide;

    activeSide.startTurn();
    this.yeti = createAndBindMinion(ConstMinion.CHILLWIND_YETI);
    gm.playCard(yeti);
    this.waterElemental = createAndBindMinion(ConstMinion.WATER_ELEMENTAL);
    gm.playCard(waterElemental);
    this.scarletCrusader = createAndBindMinion(ConstMinion.SCARLET_CRUSADER);
    gm.playCard(scarletCrusader);

    this.initialBoardSize = activeSide.board.size();
    activeSide.endTurn();
    activeSide.startTurn();
  }

  private Minion createAndBindMinion(final ConstMinion minionName) {
    final Minion minion = MinionFactory.create(minionName);
    gm.activeSide.bind(minion);
    return minion;
  }

  @Test
  public void testCharge() {
    assertThat(MinionFactory.create(ConstMinion.WORGEN_INFILTRATOR).attackMovePoints().value())
        .isEqualTo(0);
    final ConstMinion minionName = ConstMinion.WOLFRIDER;
    final Minion wolfrider = createAndBindMinion(minionName);
    gm.playCard(wolfrider);
    assertThat(wolfrider.attackMovePoints().value()).isGreaterThan(0);
  }

  @Test
  public void testBattlecryDrawCardWithFatigue() {
    assertThat(activeSide.deck.size()).isEqualTo(0);
    assertThat(activeSide.hand.size()).isEqualTo(0);
    assertThat(activeSide.hero.healthLoss()).isEqualTo(0);

    final ConstMinion minionName = ConstMinion.GNOMISH_INVENTOR;
    final Minion minion = createAndBindMinion(minionName);

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
    final Minion faerieDragon = createAndBindMinion(ConstMinion.FAERIE_DRAGON);
    assertThat(TargetFactory.isMinionTargetable(faerieDragon, activeSide.board, ConstType.SPELL)).isFalse();

    assertThat(TargetFactory.isMinionTargetable(yeti, activeSide.board, ConstType.SPELL)).isTrue();
  }

  @Test
  public void testTaunt() {
    final Minion senjin = createAndBindMinion(ConstMinion.SENJIN_SHIELDMASTA);
    final Minion grizzly = createAndBindMinion(ConstMinion.IRONFUR_GRIZZLY);
    final Minion junglePanther = createAndBindMinion(ConstMinion.JUNGLE_PANTHER);
    // Let jungle panther be both stealth and taunt.
    junglePanther.booleanMechanics().initialize(ConstMechanic.TAUNT);

    final Container<Minion> board = activeSide.board;

    board.add(yeti);
    board.add(senjin);
    board.add(grizzly);

    assertThat(TargetFactory.isMinionTargetable(yeti, board, ConstType.ATTACK)).isFalse();
    assertThat(TargetFactory.isHeroTargetable(hero, board, ConstType.ATTACK)).isFalse();
    assertThat(TargetFactory.isMinionTargetable(senjin, board, ConstType.ATTACK)).isTrue();
    assertThat(TargetFactory.isMinionTargetable(grizzly, board, ConstType.ATTACK)).isTrue();

    board.remove(senjin);
    board.remove(grizzly);
    board.add(junglePanther);

    // Yeti and another minion with both stealth and taunt on board. Yeti should be targetable
    // because stealth prevents taunt prevents Yeti being targeted.
    assertThat(TargetFactory.isMinionTargetable(yeti, board, ConstType.ATTACK)).isTrue();
    assertThat(TargetFactory.isMinionTargetable(junglePanther, board, ConstType.ATTACK)).isFalse();
  }

  @Test
  public void testDivineShield() {
    assertThat(scarletCrusader.booleanMechanics().isOn(ConstMechanic.DIVINE_SHIELD)).isTrue();

    EffectFactory.AttackFactory.pipePhysicalDamageEffect(yeti, scarletCrusader);

    // Yeti takes damage. Crusader takes no damage because of divine shield.
    assertThat(scarletCrusader.booleanMechanics().isOn(ConstMechanic.DIVINE_SHIELD)).isFalse();
    assertThat(scarletCrusader.healthLoss()).isEqualTo(0);
    assertThat(yeti.healthLoss()).isGreaterThan(0);

    EffectFactory.AttackFactory.pipePhysicalDamageEffect(yeti, scarletCrusader);

    // Crusader has no more divine shield and takes damage.
    assertThat(scarletCrusader.isDead()).isTrue();
    assertThat(yeti.isDead()).isTrue();
  }

  @Test
  public void testStealth() {
    final Minion stoneclawTotem = createAndBindMinion(ConstMinion.STONECLAW_TOTEM);
    gm.playCard(stoneclawTotem);
    final Minion worgenInfiltrator = createAndBindMinion(ConstMinion.WORGEN_INFILTRATOR);
    gm.playCard(worgenInfiltrator);

    assertThat(worgenInfiltrator.booleanMechanics().isOn(ConstMechanic.STEALTH)).isTrue();

    EffectFactory.AttackFactory.pipePhysicalDamageEffect(worgenInfiltrator, stoneclawTotem);

    // Stealth deactivated after attack.
    assertThat(worgenInfiltrator.booleanMechanics().isOn(ConstMechanic.STEALTH)).isFalse();
  }

  @Test
  public void testFreeze() {
    // Scarlet crusader has divine shield so take no damage. No damage no frozen.
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(waterElemental, scarletCrusader);
    assertThat(scarletCrusader.healthLoss()).isEqualTo(0);
    assertThat(scarletCrusader.booleanMechanics().isOn(ConstMechanic.FROZEN)).isFalse();

    // Yeti takes damage and gets frozen.
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(waterElemental, yeti);
    assertThat(yeti.healthLoss()).isGreaterThan(0);
    assertThat(yeti.booleanMechanics().isOn(ConstMechanic.FROZEN)).isTrue();
  }

  @Test
  public void testFrozen() {
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(yeti, waterElemental);
    assertThat(yeti.booleanMechanics().isOn(ConstMechanic.FROZEN)).isTrue();
    yeti.startTurn();
    assertThat(yeti.booleanMechanics().isOn(ConstMechanic.FROZEN)).isFalse();

    EffectFactory.AttackFactory.pipePhysicalDamageEffect(waterElemental, hero);

    assertThat(hero.booleanMechanics().isOn(ConstMechanic.FROZEN)).isTrue();

    // Test that next round the negative status will be reset including frozen.
    hero.startTurn();
    assertThat(hero.booleanMechanics().isOn(ConstMechanic.FROZEN)).isFalse();
  }

  @Test
  public void testPoison() {
    Minion emperorCobra = createAndBindMinion(ConstMinion.EMPEROR_COBRA);
    gm.playCard(emperorCobra);

    EffectFactory.AttackFactory.pipePhysicalDamageEffect(emperorCobra, hero);

    // Poison does not trigger destroy on Hero.
    assertThat(emperorCobra.healthLoss()).isEqualTo(0);
    assertThat(hero.isDead()).isFalse();

    // Point triggers destroy on Minion when minion is damaged.
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(emperorCobra, yeti);
    assertThat(emperorCobra.isDead()).isTrue();
    assertThat(yeti.healthLoss()).isGreaterThan(0);
    assertThat(yeti.isDead()).isTrue();

    emperorCobra = createAndBindMinion(ConstMinion.EMPEROR_COBRA);
    gm.playCard(emperorCobra);
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(emperorCobra, scarletCrusader);
    assertThat(emperorCobra.isDead()).isTrue();
    assertThat(scarletCrusader.healthLoss()).isEqualTo(0);
    assertThat(activeSide.board.contains(emperorCobra)).isFalse();
  }

  @Test
  public void testImmune() {
    // No minions so far has default immune mechanic yet.
    // Init IMMUNE for Yeti.
    yeti.booleanMechanics().initialize(ConstMechanic.IMMUNE);
    assertThat(TargetFactory.isMinionTargetable(yeti, activeSide.board, ConstType.ATTACK)).isFalse();
    assertThat(TargetFactory.isMinionTargetable(yeti, activeSide.board, ConstType.SPELL)).isFalse();

    // Test Hero immune.
    hero.booleanMechanics().initialize(ConstMechanic.IMMUNE);
    assertThat(TargetFactory.isHeroTargetable(hero, activeSide.board, ConstType.ATTACK)).isFalse();
    assertThat(TargetFactory.isHeroTargetable(hero, activeSide.board, ConstType.SPELL)).isFalse();
  }

  @Test
  public void testForgetful() {
    final Minion ogreBrute = createAndBindMinion(ConstMinion.OGRE_BRUTE);
    activeSide.bind(ogreBrute);
    gm.playCard(ogreBrute);
    final int attackVal = ogreBrute.attack().value();
    final int minionNum = 5;
    final int total = 10000;
    final int buffHealth = total * 10;
    for (int i = 0; i < minionNum; ++i) {
      // Buff Yeti health enough so that it doesn't die and gets removed from board.
      final Minion yeti = createAndBindMinion(ConstMinion.CHILLWIND_YETI);
      yeti.health().getTemporaryBuff().increase(buffHealth);
      yeti.maxHealth().getTemporaryBuff().increase(buffHealth);
      inactiveSide.board.add(yeti);
    }
    // TODO: find another way to test randomness or not to test it at all.
    final double jitter = .10;
    final double forgetfulFactor = .5;
    ogreBrute.health().getTemporaryBuff().increase(buffHealth);
    ogreBrute.maxHealth().getTemporaryBuff().increase(buffHealth);

    for (int i = 0; i < total; ++i) {
      EffectFactory.AttackFactory.pipePhysicalDamageEffect(ogreBrute, inactiveSide.hero);
    }
    Range<Double> mainTargetGotAttackedNumRange = Range.closed(
        total * forgetfulFactor * (1 - jitter),
        total * forgetfulFactor * (1 + jitter));
    Range<Double> otherTargetsGotAttackedNumRange = Range.closed(
        total * forgetfulFactor * (1 - jitter) / minionNum,
        total * forgetfulFactor * (1 + jitter) / minionNum);
    final double numOfHeroGotAttacked = inactiveSide.hero.healthLoss() / attackVal;
    assertThat(mainTargetGotAttackedNumRange.contains(numOfHeroGotAttacked)).isTrue();
    for (int i = 0; i < minionNum; ++i) {
      final double numGetAttacked = (inactiveSide.board.get(i).healthLoss()) / attackVal;
      assertThat(otherTargetsGotAttackedNumRange.contains(numGetAttacked)).isTrue();
    }
  }

  @Test
  public void testWindFury() {
    final Minion harpy = createAndBindMinion(ConstMinion.WINDFURY_HARPY);
    harpy.attackMovePoints().reset();
    assertThat(harpy.attackMovePoints().value()).isEqualTo(2);
  }

  @Test
  public void testBattlecry() {
    final ConstMinion minionInDeck = ConstMinion.FAERIE_DRAGON;
    activeSide.deck.add(createAndBindMinion(minionInDeck));

    assertThat(activeSide.deck.size()).isEqualTo(1);
    assertThat(activeSide.hand.size()).isEqualTo(0);

    final ConstMinion minionName = ConstMinion.GNOMISH_INVENTOR;
    final Minion minion = createAndBindMinion(minionName);

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
    activeSide.deck.add(createAndBindMinion(minionInDeck));

    final Minion lootHoarder = createAndBindMinion(ConstMinion.LOOT_HOARDER);
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
    final Minion defiasRingleader1 = createAndBindMinion(ConstMinion.DEFIAS_RINGLEADER);
    activeSide.bind(defiasRingleader1);
    gm.playCard(defiasRingleader1);
    // First play should not trigger combo effect hence add onl one minion to the board.
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1);

    final Minion defiasRingleader2 = createAndBindMinion(ConstMinion.DEFIAS_RINGLEADER);
    gm.playCard(defiasRingleader2);

    // Second play should trigger combo effect hence summoning DEFIAS_BANDIT.
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 3);
    assertThat(activeSide.board.get(activeSide.board.size() - 1).cardName())
        .isEqualTo(ConstMinion.DEFIAS_BANDIT.toString());
  }

  @Test
  public void testOverload() {
    final ManaCrystal manaCrystal = activeSide.hero.manaCrystal();
    // Turn 1.
    manaCrystal.startTurn();
    assertThat(manaCrystal.getCrystal()).isEqualTo(1);
    // Turn 2.
    manaCrystal.startTurn();
    assertThat(manaCrystal.getCrystal()).isEqualTo(2);

    final Weapon stormforgedAxe = WeaponFactory.create(ConstWeapon.STORMFORGED_AXE);
    gm.activeSide.bind(stormforgedAxe);

    assertThat(hero.canDamage()).isFalse();
    hero.playToEquip(stormforgedAxe);
    assertThat(hero.canDamage()).isTrue();

    // Turn 3.
    manaCrystal.startTurn();
    assertThat(manaCrystal.getCrystal()).isEqualTo(1);
  }

  @Test
  public void testDealDamage() {
    final Minion knifeJuggler = createAndBindMinion(ConstMinion.KNIFE_JUGGLER);

    gm.playCard(knifeJuggler);

    final int numOfYetiToSummon = 5;
    for (int i = 0; i < numOfYetiToSummon; ++i) {
      gm.playCard(createAndBindMinion(ConstMinion.CHILLWIND_YETI));
      assertThat(inactiveSide.hero.healthLoss()).isEqualTo(i + 1);
    }
  }

  @Test
  public void testTakeControl() {
    gm.switchTurn();
    final int threshold = 4;
    for (int i = 0; i < threshold; ++i) {
      final Minion yeti = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
      inactiveSide.bind(yeti);
      gm.playCard(yeti);
    }
    final List<Minion> opponentMinionsBackup = new ArrayList<>(inactiveSide.board.asList());
    // Test take control effect triggered because it satisfies the condition.
    gm.switchTurn();
    gm.playCard(createAndBindMinion(ConstMinion.MIND_CONTROL_TECH));

    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 2);
    assertThat(inactiveSide.board.size()).isEqualTo(threshold - 1);
    // Test the right-most minion is stolen from opponent board.
    assertThat(activeSide.board.get(activeSide.board.size() - 1)).isIn(opponentMinionsBackup);

    gm.playCard(createAndBindMinion(ConstMinion.MIND_CONTROL_TECH));
    assertThat(inactiveSide.board.size()).isEqualTo(threshold - 1);
    // Test control effect not triggered because of opponent has less than 4 minions.
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 2 + 1);
  }

  @Test
  public void testInspire() {
    final Minion recruiter = createAndBindMinion(ConstMinion.RECRUITER);
    gm.playCard(recruiter);

    assertThat(activeSide.hand.size()).isEqualTo(0);
    hero.useHeroPower(hero);

    assertThat(activeSide.hand.size()).isEqualTo(1);
  }

  @Test
  public void testReturnToHandWithTarget() {
    final Minion youthfulBrewmaster = createAndBindMinion(ConstMinion.YOUTHFUL_BREWMASTER);

    assertThat(activeSide.hand.size()).isEqualTo(0);
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize);
    gm.playCard(youthfulBrewmaster, yeti);
    // Play one minion and yeti got returned to hand.
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1 - 1);
    assertThat(activeSide.hand.size()).isEqualTo(1);
    assertThat(activeSide.hand.get(0).cardName()).isEqualTo(ConstMinion.CHILLWIND_YETI.toString());
  }

  @Test
  public void testReturnToHandWithNoTarget() {
    final Minion youthfulBrewmaster = createAndBindMinion(ConstMinion.YOUTHFUL_BREWMASTER);
    activeSide.bind(youthfulBrewmaster);
    assertThat(activeSide.hand.size()).isEqualTo(0);
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize);
    gm.playCard(youthfulBrewmaster);

    // Play one minion without specifying return target. Mechanic should not be triggered.
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1);
    assertThat(activeSide.hand.size()).isEqualTo(0);
  }

  @Test
  public void testAura() {
    final Minion stormwindChampion = createAndBindMinion(ConstMinion.STORMWIND_CHAMPION);
    final int gain = 1;

    final int yetiAttack = yeti.attack().value();
    final int yetiHealth = yeti.health().value();
    final int yetiMaxHealth = yeti.maxHealth().value();

    final int scarletCrusaderAttack = scarletCrusader.attack().value();
    final int scarletCrusaderHealth = scarletCrusader.health().value();
    final int scarletCrusaderMaxHealth = scarletCrusader.maxHealth().value();

    final int waterElementalAttack = waterElemental.attack().value();
    final int waterElementalHealth = waterElemental.health().value();
    final int waterElementalMaxHealth = waterElemental.maxHealth().value();

    gm.playCard(stormwindChampion);

    checkHealthAttackMaxHealth(yeti, yetiHealth + gain, yetiMaxHealth + gain, yetiAttack + gain);

    checkHealthAttackMaxHealth(scarletCrusader, scarletCrusaderHealth + gain,
        scarletCrusaderMaxHealth + gain, scarletCrusaderAttack + gain);

    checkHealthAttackMaxHealth(waterElemental, waterElementalHealth + gain,
        waterElementalMaxHealth + gain, waterElementalAttack + gain);

    // Test minion put onto the board later also benefits from the aura effect.
    final Minion worgenInfiltrator = createAndBindMinion(ConstMinion.WORGEN_INFILTRATOR);
    final int worgenInfiltratorAttack = worgenInfiltrator.attack().value();
    final int worgenInfiltratorHealth = worgenInfiltrator.health().value();
    final int worgenInfiltratorMaxHealth = worgenInfiltrator.maxHealth().value();
    gm.playCard(worgenInfiltrator);
    checkHealthAttackMaxHealth(worgenInfiltrator, worgenInfiltratorHealth + gain,
        worgenInfiltratorMaxHealth + gain, worgenInfiltratorAttack + gain);

    worgenInfiltrator.attack().reset();
    assertThat(worgenInfiltrator.attack().value()).isEqualTo(worgenInfiltratorAttack);

    yeti.takeDamage(yeti.health().value() - 1);
    stormwindChampion.death();

    // Test that the aura removal "heals" minion when it's damaged before aura is removed.
    checkHealthAttackMaxHealth(yeti, 1, yetiMaxHealth, yetiAttack);

    checkHealthAttackMaxHealth(scarletCrusader, scarletCrusaderHealth, scarletCrusaderMaxHealth,
        scarletCrusaderAttack);

    checkHealthAttackMaxHealth(waterElemental, waterElementalHealth, waterElementalMaxHealth,
        waterElementalAttack);

    checkHealthAttackMaxHealth(worgenInfiltrator, worgenInfiltratorHealth, worgenInfiltratorMaxHealth,
        worgenInfiltratorAttack);
  }

  private void checkHealthAttackMaxHealth(final Minion minion, final int expectedHealth,
                                          final int expectedMaxHealth, final int expectedAttack) {
    assertThat(minion.attack().value()).isEqualTo(expectedAttack);
    assertThat(minion.health().value()).isEqualTo(expectedHealth);
    assertThat(minion.maxHealth().value()).isEqualTo(expectedMaxHealth);
  }

  @Test
  public void testDealDamageAsBattlecryWithoutTarget() {
    final Minion nightblade = createAndBindMinion(ConstMinion.NIGHTBLADE);
    gm.playCard(nightblade);
    assertThat(inactiveSide.hero.healthLoss()).isAtLeast(1);
  }

  @Test
  public void testEndingTurnMechanics() {
    final Minion healingTotem = createAndBindMinion(ConstMinion.HEALING_TOTEM);
    activeSide.bind(healingTotem);
    gm.playCard(healingTotem);

    final int damage = 2;
    final int healing = 1;
    yeti.takeDamage(damage);
    waterElemental.takeDamage(damage);
    gm.endTurn();

    assertThat(yeti.healthLoss()).isEqualTo(damage - healing);
    assertThat(waterElemental.healthLoss()).isEqualTo(damage - healing);

    gm.endTurn();

    assertThat(yeti.healthLoss()).isEqualTo(0);
    assertThat(waterElemental.healthLoss()).isEqualTo(0);
  }

  @Test
  public void testDestroyWeapon() {
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);

    gm.switchTurn();
    final Weapon fieryWinAxe = WeaponFactory.create(ConstWeapon.FIERY_WAR_AXE);
    gm.activeSide.bind(fieryWinAxe);
    gm.playCard(fieryWinAxe);

    gm.switchTurn();
    assertThat(inactiveSide.hero.getWeapon().isPresent()).isTrue();
    gm.playCard(ooze);
    assertThat(inactiveSide.hero.getWeapon().isPresent()).isFalse();

    // Play another ooze and no weapon to destroy.
    gm.playCard(createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE));
    assertThat(inactiveSide.hero.getWeapon().isPresent()).isFalse();
  }

  @Test
  public void testSpellDamage() {
    Spell fireball = SpellFactory.create(ConstSpell.FIRE_BALL);
    final int damage = 6;
    activeSide.bind(fireball);
    activeSide.hand.add(fireball);

    final Minion archmage = createAndBindMinion(ConstMinion.ARCHMAGE);
    gm.playCard(archmage);
    gm.playCard(fireball, yeti);

    // Test that spell is held in hand then add spell damage minion onto board, it should be
    // reflected on that spell in the hand by adding the buff.
    assertThat(yeti.healthLoss()).isEqualTo(damage + 1);

    fireball = SpellFactory.create(ConstSpell.FIRE_BALL);
    activeSide.bind(fireball);
    activeSide.hand.add(fireball);
    fireball.refresh();

    gm.playCard(fireball, yeti);
    // Test that spell is added to hand after playing spell damage minion onto board, it should be
    // reflected on that spell in the hand by adding the buff.
    assertThat(yeti.healthLoss()).isEqualTo(2 * damage + 2);

    // Test that spell is held in hand then remove the spell damage minion, it should be
    // reflected on that spell in the hand by removing the buff.
    fireball = SpellFactory.create(ConstSpell.FIRE_BALL);
    activeSide.bind(fireball);
    activeSide.hand.add(fireball);
    fireball.refresh();
    archmage.death();

    gm.playCard(fireball, waterElemental);
    assertThat(waterElemental.healthLoss()).isEqualTo(damage);
  }

  @Test
  public void testAuraWithSelectiveBeneficiary() {
    final Minion grimscaleOracle = createAndBindMinion(ConstMinion.GRIMSCALE_ORACLE);
    final Minion blueGillWarrior = createAndBindMinion(ConstMinion.BLUEGILL_WARRIOR);
    gm.playCard(blueGillWarrior);
    gm.playCard(grimscaleOracle);
    assertThat(yeti.attack().value()).isEqualTo(4);
    assertThat(scarletCrusader.attack().value()).isEqualTo(3);
    assertThat(waterElemental.attack().value()).isEqualTo(3);
  }

  @Test
  public void testAoeHeal() {
    final Minion darkscaleHealer = createAndBindMinion(ConstMinion.DARKSCALE_HEALER);

    final int damage = 3;
    yeti.takeDamage(3);
    waterElemental.takeDamage(3);
    scarletCrusader.takeDamage(3);

    gm.playCard(darkscaleHealer);

    assertThat(yeti.healthLoss()).isEqualTo(damage - 2);
    assertThat(waterElemental.healthLoss()).isEqualTo(damage - 2);
    assertThat(scarletCrusader.healthLoss()).isEqualTo(0);
  }

  @Test
  public void testDealDamageAsBattlecryWithTarget() {
    final Minion elvenArcher = createAndBindMinion(ConstMinion.ELVEN_ARCHER);
    gm.playCard(elvenArcher, gm.inactiveSide.hero);

    assertThat(gm.inactiveSide.hero.healthLoss()).isEqualTo(1);
  }

  @Test
  public void testEffectDependingOnBoardSize() {
    final Minion frostWolfWarlord = createAndBindMinion(ConstMinion.FROSTWOLF_WARLORD);
    gm.playCard(frostWolfWarlord);

    assertThat(frostWolfWarlord.attack().value()).isEqualTo(4 + initialBoardSize);
    assertThat(frostWolfWarlord.health().value()).isEqualTo(4 + initialBoardSize);
  }

  @Test
  public void testTakeDamage() {
    final Minion gurubashiBerserker = createAndBindMinion(ConstMinion.GURUBASHI_BERSERKER);
    final int attack = gurubashiBerserker.attack().value();
    gm.playCard(gurubashiBerserker);

    for (int i = 1; i <= gurubashiBerserker.health().value(); ++i) {
      gurubashiBerserker.takeDamage(1);
      assertThat(gurubashiBerserker.attack().value()).isEqualTo(attack + 3 * i);
    }
  }

  @Test
  public void testBuffAsBattlecry() {
    final Minion sunCleric = createAndBindMinion(ConstMinion.SHATTERED_SUN_CLERIC);

    final int attack = yeti.attack().value();
    final int health = yeti.health().value();
    gm.playCard(sunCleric, yeti);

    assertThat(yeti.attack().value()).isEqualTo(attack + 1);
    assertThat(yeti.health().value()).isEqualTo(health + 1);
  }

  @Test
  public void testSelectiveBuff() {
    final Minion houndmaster = createAndBindMinion(ConstMinion.HOUNDMASTER);
    final Minion boar = createAndBindMinion(ConstMinion.BOAR);
    final int attack  = boar.attack().value();
    final int health = boar.health().value();
    final int maxHealth = boar.maxHealth().value();
    assertThat(boar.booleanMechanics().isOff(ConstMechanic.TAUNT));

    gm.playCard(houndmaster, boar);
    assertThat(boar.attack().value()).isEqualTo(attack + 2);
    assertThat(boar.health().value()).isEqualTo(health + 2);
    assertThat(boar.maxHealth().value()).isEqualTo(maxHealth + 2);
    assertThat(boar.booleanMechanics().isOn(ConstMechanic.TAUNT));
  }

  @Test
  public void testStarvingBuzzard() {
    final int deckSize = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    for (int i = 0; i < deckSize; ++i) {
      gm.activeSide.deck.add(MinionFactory.create(ConstMinion.CHILLWIND_YETI));
    }
    final int initHandSize = gm.activeSide.hand.size();
    final Minion starvingBuzzard = createAndBindMinion(ConstMinion.STARVING_BUZZARD);
    gm.playCard(starvingBuzzard);

    // Test that putting a non-beast minion on board does not trigger draw card effect.
    gm.playCard(createAndBindMinion(ConstMinion.WOLFRIDER));
    assertThat(gm.activeSide.deck.size()).isEqualTo(deckSize);
    assertThat(gm.activeSide.hand.size()).isEqualTo(initHandSize);

    // Test that putting a beast minion on board triggers draw card effect.
    gm.playCard(createAndBindMinion(ConstMinion.TIMBER_WOLF));
    assertThat(gm.activeSide.deck.size()).isEqualTo(deckSize - 1);
    assertThat(gm.activeSide.hand.size()).isEqualTo(initHandSize + 1);
  }

  @Test
  public void testTundraRhino() {
    final Minion warGolem = createAndBindMinion(ConstMinion.WAR_GOLEM);
    final Minion rhino = createAndBindMinion(ConstMinion.TUNDRA_RHINO);
    final Minion boar = createAndBindMinion(ConstMinion.BOAR);
    final Minion timberWolf = createAndBindMinion(ConstMinion.TIMBER_WOLF);
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    gm.playCard(warGolem);
    gm.playCard(boar);

    assertThat(warGolem.canMove()).isFalse();
    assertThat(boar.canMove()).isFalse();
    // Test after putting rhino on board, yeti still cannot move but board can.
    gm.playCard(rhino);
    assertThat(warGolem.canMove()).isFalse();
    assertThat(boar.canMove()).isTrue();
    // Test adding a beast on board with rhino present, the beast has charge.
    gm.playCard(timberWolf);
    assertThat(timberWolf.canMove()).isTrue();
    // Test adding a non-beast on board with rhino present, it cannot move.
    gm.playCard(ooze);
    assertThat(ooze.canMove()).isFalse();
  }

  @Test
  public void testGuardianOfKings() {
    final Minion guardianOfKings = createAndBindMinion(ConstMinion.GUARDIAN_OF_KINGS);
    gm.activeSide.hero.takeDamage(6);
    gm.playCard(guardianOfKings);
    assertThat(gm.activeSide.hero.healthLoss()).isEqualTo(0);
  }

  @Test
  public void testTruesilverChampion() {
    // TODO: create weapon test suite.
    final Weapon truesilverChampion = WeaponFactory.create(ConstWeapon.TRUESILVER_CHAMPION);
    gm.activeSide.hero.takeDamage(2);
    gm.activeSide.hero.equip(truesilverChampion);
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(gm.activeSide.hero, yeti);
    assertThat(yeti.healthLoss()).isEqualTo(4);
    // Heal for 2 first, which has no effect because hero has full health already then take 4 damage
    // from yeti. Should have 26 health left.
    assertThat(gm.activeSide.hero.healthLoss()).isEqualTo(4);

    EffectFactory.AttackFactory.pipePhysicalDamageEffect(gm.activeSide.hero, yeti);
    // Heal for 2 first when take 4 from yeti. Should have 24 health left.
    assertThat(gm.activeSide.hero.healthLoss()).isEqualTo(4 - 2 + 4);
    assertThat(gm.activeSide.hero.getWeapon().isPresent()).isFalse();
  }

  @Test
  public void testTriggerOnHealMinion() {
    // Fill both deck.
    final int deckSize = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    for (int i = 0; i < deckSize; ++i) {
      gm.activeSide.deck.add(MinionFactory.create(ConstMinion.CHILLWIND_YETI));
      gm.inactiveSide.deck.add(MinionFactory.create(ConstMinion.CHILLWIND_YETI));
    }
    final Minion northshireCleric = createAndBindMinion(ConstMinion.NORTHSHIRE_CLERIC);
    gm.playCard(northshireCleric);
    final int handSize = gm.activeSide.hand.size();

    // Wound own minion and heal it.
    yeti.takeDamage(2);
    gm.playCard(createAndBindMinion(ConstMinion.DARKSCALE_HEALER));

    assertThat(yeti.healthLoss()).isEqualTo(0);
    assertThat(gm.activeSide.deck.size()).isEqualTo(deckSize - 1);
    assertThat(gm.activeSide.hand.size()).isEqualTo(handSize + 1);

    // Put northshire on opponent's board.
    gm.switchTurn();
    gm.playCard(createAndBindMinion(ConstMinion.NORTHSHIRE_CLERIC));
    gm.switchTurn();

    // Wound two minion and heal it.
    yeti.takeDamage(2);
    northshireCleric.takeDamage(2);
    gm.playCard(createAndBindMinion(ConstMinion.DARKSCALE_HEALER));

    // With two northshire on board, should draw 2 x 2 = 4 cards.
    assertThat(yeti.healthLoss()).isEqualTo(0);
    assertThat(gm.activeSide.deck.size()).isEqualTo(deckSize - 1 - 2 * 2);
    assertThat(gm.activeSide.hand.size()).isEqualTo(handSize + 1 + 2 * 2);
  }

  @Test
  public void testFlametongueTotem() {
    final Minion flametongueTotem = createAndBindMinion(ConstMinion.FLAMETONGUE_TOTEM);
    final int flametongueTotemAttack = flametongueTotem.attack().value();
    final int yetiAttack = yeti.attack().value();
    final int waterElementalAttack = waterElemental.attack().value();
    final int scarletCrusaderAttack = scarletCrusader.attack().value();
    assertThat(gm.activeSide.board.get(0)).isEqualTo(yeti);
    assertThat(gm.activeSide.board.get(1)).isEqualTo(waterElemental);
    assertThat(gm.activeSide.board.get(2)).isEqualTo(scarletCrusader);
    gm.playCard(flametongueTotem);
    assertThat(gm.activeSide.board.get(3)).isEqualTo(flametongueTotem);

    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    final int oozeAttack = ooze.attack().value();
    gm.playCard(ooze);
    assertThat(gm.activeSide.board.get(4)).isEqualTo(ooze);

    // Test that only adjacent minion gets affected by aura.
    assertThat(yeti.attack().value()).isEqualTo(yetiAttack);
    assertThat(waterElemental.attack().value()).isEqualTo(waterElementalAttack);
    assertThat(scarletCrusader.attack().value()).isEqualTo(scarletCrusaderAttack + 2);
    assertThat(flametongueTotem.attack().value()).isEqualTo(flametongueTotemAttack);
    assertThat(ooze.attack().value()).isEqualTo(oozeAttack + 2);
  }

  @Test
  public void testSuccubus() {
    final Minion succubus = createAndBindMinion(ConstMinion.SUCCUBUS);
    gm.activeSide.hand.add(createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE));
    final int handSize = gm.activeSide.hand.size();

    gm.playCard(succubus);
    assertThat(gm.activeSide.hand.size()).isEqualTo(handSize - 1);
  }

  @Test
  public void testDreadInfernal() {
    final Minion dreadInfernal = createAndBindMinion(ConstMinion.DREAD_INFERNAL);
    gm.playCard(dreadInfernal);
    assertThat(dreadInfernal.healthLoss()).isEqualTo(0);
    assertThat(yeti.healthLoss()).isEqualTo(1);
  }

  @Test
  public void testWarsongCommander() {
    final Minion commander = createAndBindMinion(ConstMinion.WARSONG_COMMANDER);
    final Minion rider = createAndBindMinion(ConstMinion.WOLFRIDER);
    assertThat(rider.booleanMechanics().isOff(ConstMechanic.CHARGE));

    final int riderAttack = rider.attack().value();
    final int yetiAttack = yeti.attack().value();
    final int waterElementalAttack = waterElemental.attack().value();
    final int scarletCrusaderAttack = scarletCrusader.attack().value();

    gm.playCard(rider);
    gm.playCard(commander);
    // Test that warsong commander only effects minion with charge.
    assertThat(yeti.attack().value()).isEqualTo(yetiAttack);
    assertThat(waterElemental.attack().value()).isEqualTo(waterElementalAttack);
    assertThat(scarletCrusader.attack().value()).isEqualTo(scarletCrusaderAttack);
    assertThat(rider.attack().value()).isEqualTo(riderAttack + 1);

    // Test that when commander is removed from the board, buff is gone as well.
    commander.takeDamage(10);
    assertThat(gm.activeSide.board.contains(commander)).isFalse();
    assertThat(rider.attack().value()).isEqualTo(riderAttack);
  }
}
