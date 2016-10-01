package com.herthrone.base;

import com.google.common.collect.Range;
import com.herthrone.BaseGame;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.TargetFactory;
import com.herthrone.factory.WeaponFactory;
import com.herthrone.game.Container;
import com.herthrone.game.Side;
import com.herthrone.object.ManaCrystal;
import com.herthrone.service.BoardSide;
import com.herthrone.service.Command;
import com.herthrone.service.CommandType;
import com.herthrone.service.ContainerType;
import com.herthrone.service.Entity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class MechanicTest extends BaseGame {

  private Hero hero;
  private Minion yeti;
  private Minion waterElemental;
  private Minion scarletCrusader;
  private Side activeSide;
  private Side inactiveSide;
  private int initialBoardSize;
  private int initialDeckSize;

  @Before
  public void setUp() {
    setUpGame(ConstHero.GULDAN, ConstHero.GULDAN);
    this.hero = game.activeSide.hero;
    this.activeSide = game.activeSide;
    this.inactiveSide = game.inactiveSide;

    activeSide.startTurn();
    this.scarletCrusader = minion.addToHandAndPlay(ConstMinion.SCARLET_CRUSADER);
    this.waterElemental = minion.addToHandAndPlay(ConstMinion.WATER_ELEMENTAL);
    this.yeti = minion.addToHandAndPlay(ConstMinion.CHILLWIND_YETI);

    this.initialBoardSize = activeSide.board.size();
    this.initialDeckSize = activeSide.deck.size();
    activeSide.endTurn();
    activeSide.startTurn();
  }

  private void addCardToHandAndPlayItOnOwnBoard(final Card card) {
    game.activeSide.hand.add(0, card);
    final Command playCardCommand = Command.newBuilder()
        .setType(CommandType.PLAY_CARD)
        .setDoer(Entity.newBuilder()
            .setSide(BoardSide.OWN)
            .setContainerType(ContainerType.HAND)
            .setPosition(0))
        .build();
    game.command(playCardCommand);
  }

  private void addCardToHandAndPlayItOnOwnBoard(final Card card, final BoardSide side,
                                                final ContainerType containerType, final int index) {
    game.activeSide.hand.add(0, card);
    final Command playCardCommand = Command.newBuilder()
        .setType(CommandType.PLAY_CARD)
        .setDoer(Entity.newBuilder()
            .setSide(BoardSide.OWN)
            .setContainerType(ContainerType.HAND)
            .setPosition(0))
        .setTarget(Entity.newBuilder()
            .setSide(side)
            .setContainerType(containerType)
            .setPosition(index))
        .build();
    game.command(playCardCommand);
  }

  @Test
  public void testCharge() {
    assertThat(MinionFactory.create(ConstMinion.WORGEN_INFILTRATOR).attackMovePoints().value())
        .isEqualTo(0);
    final ConstMinion minionName = ConstMinion.WOLFRIDER;
    final Minion wolfrider = minion.addToHandAndPlay(minionName);
    assertThat(wolfrider.attackMovePoints().value()).isGreaterThan(0);
  }

  @Test
  public void testBattlecryDrawCardWithFatigue() {
    // Empty deck so next draw triggers fatigue.
    while (!game.activeSide.deck.isEmpty()) {
      game.activeSide.deck.remove(0);
    }
    assertThat(activeSide.deck.size()).isEqualTo(0);
    final Minion gnomishInventor = minion.addToHandAndPlay(ConstMinion.GNOMISH_INVENTOR);
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1);
    assertThat(activeSide.board.get(0)).isEqualTo(gnomishInventor);
    assertThat(activeSide.deck.size()).isEqualTo(0);
    assertThat(activeSide.hand.size()).isEqualTo(0);
    // Battlecry draw card causing fatigue damage.
    assertThat(activeSide.hero.healthLoss()).isEqualTo(1);
  }

  @Test
  public void testElusive() {
    final Minion faerieDragon = minion.addToHandAndPlay(ConstMinion.FAERIE_DRAGON);
    assertThat(TargetFactory.isMinionTargetable(faerieDragon, activeSide.board, ConstType.SPELL)).isFalse();
    assertThat(TargetFactory.isMinionTargetable(yeti, activeSide.board, ConstType.SPELL)).isTrue();
  }

  @Test
  public void testTaunt() {
    final Minion senjin = minion.addToHandAndPlay(ConstMinion.SENJIN_SHIELDMASTA);
    final Minion grizzly = minion.addToHandAndPlay(ConstMinion.IRONFUR_GRIZZLY);
    final Minion junglePanther = minion.addToHandAndPlay(ConstMinion.JUNGLE_PANTHER);
    // Let jungle panther be both stealth and taunt.
    junglePanther.booleanMechanics().initialize(ConstMechanic.TAUNT);

    final Container<Minion> board = activeSide.board;
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
    final Minion stoneclawTotem = minion.addToHandAndPlay(ConstMinion.STONECLAW_TOTEM);
    final Minion worgenInfiltrator = minion.addToHandAndPlay(ConstMinion.WORGEN_INFILTRATOR);

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
    Minion emperorCobra = minion.addToHandAndPlay(ConstMinion.EMPEROR_COBRA);
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(emperorCobra, hero);

    // Poison does not trigger destroy on Hero.
    assertThat(emperorCobra.healthLoss()).isEqualTo(0);
    assertThat(hero.isDead()).isFalse();

    // Point triggers destroy on Minion when minion is damaged.
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(emperorCobra, yeti);
    assertThat(emperorCobra.isDead()).isTrue();
    assertThat(yeti.healthLoss()).isGreaterThan(0);
    assertThat(yeti.isDead()).isTrue();

    emperorCobra = minion.addToHandAndPlay(ConstMinion.EMPEROR_COBRA);
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
    final Minion ogreBrute = minion.addToHandAndPlay(ConstMinion.OGRE_BRUTE);
    final int attackVal = ogreBrute.attack().value();
    final int minionNum = 5;
    final int total = 10000;
    final int buffHealth = total * 10;
    for (int i = 0; i < minionNum; ++i) {
      // Buff Yeti health enough so that it doesn't die and gets removed from board.
      final Minion yeti = minion.addToHandAndPlay(ConstMinion.CHILLWIND_YETI);
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
    final Range<Double> mainTargetGotAttackedNumRange = Range.closed(
        total * forgetfulFactor * (1 - jitter),
        total * forgetfulFactor * (1 + jitter));
    final Range<Double> otherTargetsGotAttackedNumRange = Range.closed(
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
    final Minion harpy = minion.addToHandAndPlay(ConstMinion.WINDFURY_HARPY);
    harpy.attackMovePoints().reset();
    assertThat(harpy.attackMovePoints().value()).isEqualTo(2);
  }

  @Test
  public void testBattlecry() {
    final Minion gnomish = minion.addToHandAndPlay(ConstMinion.GNOMISH_INVENTOR);

    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1);
    assertThat(activeSide.board.get(0)).isEqualTo(gnomish);
    assertThat(activeSide.deck.size()).isEqualTo(this.initialDeckSize - 1);
    assertThat(activeSide.hand.size()).isEqualTo(1);
    assertThat(activeSide.hand.get(0).cardName()).isEqualTo(ConstMinion.CHILLWIND_YETI.toString());
  }

  @Test
  public void testDeathrattle() {
    final Minion lootHoarder = minion.addToHandAndPlay(ConstMinion.LOOT_HOARDER);
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1);

    assertThat(activeSide.deck.size()).isEqualTo(initialDeckSize);
    assertThat(activeSide.hand.size()).isEqualTo(0);

    lootHoarder.takeDamage(1);
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize);
    assertThat(activeSide.deck.size()).isEqualTo(initialDeckSize - 1);
    assertThat(activeSide.hand.size()).isEqualTo(1);
    assertThat(activeSide.hand.get(0).cardName()).isEqualTo(ConstMinion.CHILLWIND_YETI.toString());
  }

  @Test
  public void testCombo() {
    minion.addToHandAndPlay(ConstMinion.DEFIAS_RINGLEADER);
    // First play should not trigger combo effect hence add onl one minion to the board.
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1);

    minion.addToHandAndPlay(ConstMinion.DEFIAS_RINGLEADER);
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
    game.activeSide.bind(stormforgedAxe);

    assertThat(hero.canDamage()).isFalse();
    hero.playToEquip(stormforgedAxe);
    assertThat(hero.canDamage()).isTrue();

    // Turn 3.
    manaCrystal.startTurn();
    assertThat(manaCrystal.getCrystal()).isEqualTo(1);
  }

  @Test
  public void testDealDamage() {
    final Minion knifeJuggler = minion.addToHandAndPlay(ConstMinion.KNIFE_JUGGLER);

    final int numOfYetiToSummon = 5;
    for (int i = 0; i < numOfYetiToSummon; ++i) {
      minion.addToHandAndPlay(ConstMinion.CHILLWIND_YETI);
      assertThat(inactiveSide.hero.healthLoss()).isEqualTo(i + 1);
    }
  }

  @Test
  public void testTakeControl() {
    game.switchTurn();
    final int threshold = 4;
    for (int i = 0; i < threshold; ++i) {
      final Minion yeti = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
      inactiveSide.bind(yeti);
      minion.addToHandAndPlay(yeti);
    }
    final List<Minion> opponentMinionsBackup = new ArrayList<>(inactiveSide.board.asList());
    // Test take control effect triggered because it satisfies the condition.
    game.switchTurn();
    minion.addToHandAndPlay(ConstMinion.MIND_CONTROL_TECH);

    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 2);
    assertThat(inactiveSide.board.size()).isEqualTo(threshold - 1);
    // Test the right-most minion is stolen from opponent board.
    assertThat(activeSide.board.get(activeSide.board.size() - 1)).isIn(opponentMinionsBackup);

    minion.addToHandAndPlay(ConstMinion.MIND_CONTROL_TECH);
    assertThat(inactiveSide.board.size()).isEqualTo(threshold - 1);
    // Test control effect not triggered because of opponent has less than 4 minions.
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 2 + 1);
  }

  @Test
  public void testInspire() {
    minion.addToHandAndPlay(ConstMinion.RECRUITER);

    assertThat(activeSide.hand.size()).isEqualTo(0);
    hero.useHeroPower(hero);

    // Guldan hero power draw + inspire draw.
    assertThat(activeSide.hand.size()).isEqualTo(2);
  }

  @Test
  public void testReturnToHandWithTarget() {

    assertThat(activeSide.hand.size()).isEqualTo(0);
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize);
    minion.addToHandAndPlay(ConstMinion.YOUTHFUL_BREWMASTER, BoardSide.OWN, ContainerType.BOARD, 0);
    // Play one minion and yeti got returned to hand.
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1 - 1);
    assertThat(activeSide.hand.size()).isEqualTo(1);
    assertThat(activeSide.hand.get(0).cardName()).isEqualTo(ConstMinion.CHILLWIND_YETI.toString());
  }

  @Test
  public void testReturnToHandWithNoTarget() {
    assertThat(activeSide.hand.size()).isEqualTo(0);
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize);
    minion.addToHandAndPlay(ConstMinion.YOUTHFUL_BREWMASTER);
    // Play one minion without specifying return target. Mechanic should not be triggered.
    assertThat(activeSide.board.size()).isEqualTo(initialBoardSize + 1);
    assertThat(activeSide.hand.size()).isEqualTo(0);
  }

  @Test
  public void testAura() {
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

    final Minion stormwindChampion = minion.addToHandAndPlay(ConstMinion.STORMWIND_CHAMPION);

    checkHealthAttackMaxHealth(yeti, yetiHealth + gain, yetiMaxHealth + gain, yetiAttack + gain);
    checkHealthAttackMaxHealth(scarletCrusader, scarletCrusaderHealth + gain,
        scarletCrusaderMaxHealth + gain, scarletCrusaderAttack + gain);
    checkHealthAttackMaxHealth(waterElemental, waterElementalHealth + gain,
        waterElementalMaxHealth + gain, waterElementalAttack + gain);

    // Test minion put onto the board later also benefits from the aura effect.
    final Minion worgenInfiltrator = minion.create(ConstMinion.WORGEN_INFILTRATOR);
    final int worgenInfiltratorAttack = worgenInfiltrator.attack().value();
    final int worgenInfiltratorHealth = worgenInfiltrator.health().value();
    final int worgenInfiltratorMaxHealth = worgenInfiltrator.maxHealth().value();
    minion.addToHandAndPlay(worgenInfiltrator);
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
    minion.addToHandAndPlay(ConstMinion.NIGHTBLADE);
    assertThat(inactiveSide.hero.healthLoss()).isAtLeast(1);
  }

  @Test
  public void testEndingTurnMechanics() {
    minion.addToHandAndPlay(ConstMinion.HEALING_TOTEM);

    final int damage = 2;
    final int healing = 1;
    yeti.takeDamage(damage);
    waterElemental.takeDamage(damage);
    game.endTurn();

    assertThat(yeti.healthLoss()).isEqualTo(damage - healing);
    assertThat(waterElemental.healthLoss()).isEqualTo(damage - healing);

    game.endTurn();

    assertThat(yeti.healthLoss()).isEqualTo(0);
    assertThat(waterElemental.healthLoss()).isEqualTo(0);
  }

  @Test
  public void testDestroyWeapon() {
    game.switchTurn();
    final Weapon fieryWinAxe = WeaponFactory.create(ConstWeapon.FIERY_WAR_AXE);
    game.activeSide.bind(fieryWinAxe);
    game.activeSide.hero.equip(fieryWinAxe);

    game.switchTurn();
    assertThat(inactiveSide.hero.getWeapon().isPresent()).isTrue();
    minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    assertThat(inactiveSide.hero.getWeapon().isPresent()).isFalse();

    // Play another ooze and no weapon to destroy.
    minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    assertThat(inactiveSide.hero.getWeapon().isPresent()).isFalse();
  }

  @Test
  public void testSpellDamage() {
    Spell fireball = spell.create(ConstSpell.FIRE_BALL);
    game.activeSide.hand.add(fireball);
    final int damage = fireball.getTriggeringMechanics().get(ConstTrigger.ON_PLAY).get(0).value;

    final Minion archmage = minion.addToHandAndPlay(ConstMinion.ARCHMAGE);
    // Test that spell is added to hand when spell damage minion already present on board, it
    // should be reflected on that spell in the hand by adding the buff.
    assertThat(fireball.getTriggeringMechanics().get(ConstTrigger.ON_PLAY).get(0).value)
        .isEqualTo(damage + 1);
    spell.addToHandAndCast(fireball, BoardSide.OWN, ContainerType.BOARD, 1);
    assertThat(yeti.healthLoss()).isEqualTo(damage + 1);

    fireball = spell.create(ConstSpell.FIRE_BALL);
    activeSide.hand.add(fireball);
    fireball.refresh();
    assertThat(fireball.getTriggeringMechanics().get(ConstTrigger.ON_PLAY).get(0).value)
        .isEqualTo(damage + 1);

    spell.addToHandAndCast(fireball, BoardSide.OWN, ContainerType.BOARD, 1);
    // Test that spell is added to hand after playing spell damage minion onto board, it should be
    // reflected on that spell in the hand by adding the buff.
    assertThat(waterElemental.healthLoss()).isEqualTo(damage + 1);

    // Test that spell is held in hand then remove the spell damage minion, it should be
    // reflected on that spell in the hand by removing the buff.
    fireball = spell.create(ConstSpell.FIRE_BALL);
    activeSide.hand.add(fireball);
    fireball.refresh();
    archmage.death();

    assertThat(fireball.getTriggeringMechanics().get(ConstTrigger.ON_PLAY).get(0).value)
        .isEqualTo(damage);
  }

  @Test
  public void testAuraWithSelectiveBeneficiary() {
    minion.addToHandAndPlay(ConstMinion.GRIMSCALE_ORACLE);
    minion.addToHandAndPlay(ConstMinion.BLUEGILL_WARRIOR);
    assertThat(yeti.attack().value()).isEqualTo(4);
    assertThat(scarletCrusader.attack().value()).isEqualTo(3);
    assertThat(waterElemental.attack().value()).isEqualTo(3);
  }

  @Test
  public void testAoeHeal() {
    final int damage = 3;
    yeti.takeDamage(3);
    waterElemental.takeDamage(3);
    scarletCrusader.takeDamage(3);

    minion.addToHandAndPlay(ConstMinion.DARKSCALE_HEALER);

    assertThat(yeti.healthLoss()).isEqualTo(damage - 2);
    assertThat(waterElemental.healthLoss()).isEqualTo(damage - 2);
    assertThat(scarletCrusader.healthLoss()).isEqualTo(0);
  }

  @Test
  public void testDealDamageAsBattlecryWithTarget() {
    minion.addToHandAndPlay(ConstMinion.ELVEN_ARCHER, BoardSide.FOE, ContainerType.HERO, 0);
    assertThat(game.inactiveSide.hero.healthLoss()).isEqualTo(1);
  }

  @Test
  public void testEffectDependingOnBoardSize() {
    final Minion frostWolfWarlord = minion.addToHandAndPlay(ConstMinion.FROSTWOLF_WARLORD);
    assertThat(frostWolfWarlord.attack().value()).isEqualTo(4 + initialBoardSize);
    assertThat(frostWolfWarlord.health().value()).isEqualTo(4 + initialBoardSize);
  }

  @Test
  public void testTakeDamage() {
    final Minion gurubashiBerserker = minion.addToHandAndPlay(ConstMinion.GURUBASHI_BERSERKER);
    final int attack = gurubashiBerserker.attack().value();

    for (int i = 1; i <= gurubashiBerserker.health().value(); ++i) {
      gurubashiBerserker.takeDamage(1);
      assertThat(gurubashiBerserker.attack().value()).isEqualTo(attack + 3 * i);
    }
  }

  @Test
  public void testBuffAsBattlecry() {
    final int attack = yeti.attack().value();
    final int health = yeti.health().value();

    minion.addToHandAndPlay(ConstMinion.SHATTERED_SUN_CLERIC, BoardSide.OWN, ContainerType.BOARD, 0);

    assertThat(yeti.attack().value()).isEqualTo(attack + 1);
    assertThat(yeti.health().value()).isEqualTo(health + 1);
  }

  @Test
  public void testSelectiveBuff() {
    final Minion boar = minion.addToHandAndPlay(ConstMinion.BOAR);
    final int attack  = boar.attack().value();
    final int health = boar.health().value();
    final int maxHealth = boar.maxHealth().value();
    assertThat(boar.booleanMechanics().isOff(ConstMechanic.TAUNT));

    minion.addToHandAndPlay(ConstMinion.HOUNDMASTER, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(boar.attack().value()).isEqualTo(attack + 2);
    assertThat(boar.health().value()).isEqualTo(health + 2);
    assertThat(boar.maxHealth().value()).isEqualTo(maxHealth + 2);
    assertThat(boar.booleanMechanics().isOn(ConstMechanic.TAUNT));
  }

  @Test
  public void testStarvingBuzzard() {
    final int initHandSize = game.activeSide.hand.size();
    final Minion starvingBuzzard = minion.addToHandAndPlay(ConstMinion.STARVING_BUZZARD);

    // Test that putting a non-beast minion on board does not trigger draw card effect.
    minion.addToHandAndPlay(ConstMinion.WOLFRIDER);
    assertThat(game.activeSide.deck.size()).isEqualTo(DECK_SIZE);
    assertThat(game.activeSide.hand.size()).isEqualTo(initHandSize);

    // Test that putting a beast minion on board triggers draw card effect.
    minion.addToHandAndPlay(ConstMinion.TIMBER_WOLF);
    assertThat(game.activeSide.deck.size()).isEqualTo(DECK_SIZE - 1);
    assertThat(game.activeSide.hand.size()).isEqualTo(initHandSize + 1);
  }

  @Test
  public void testTundraRhino() {
    final Minion warGolem = minion.addToHandAndPlay(ConstMinion.WAR_GOLEM);
    minion.addToHandAndPlay(warGolem);
    assertThat(warGolem.canMove()).isFalse();
    final Minion boar = minion.addToHandAndPlay(ConstMinion.BOAR);
    minion.addToHandAndPlay(boar);
    assertThat(boar.canMove()).isFalse();
    // Test after putting rhino on board, yeti still cannot move but board can.
    minion.addToHandAndPlay(ConstMinion.TUNDRA_RHINO);
    assertThat(warGolem.canMove()).isFalse();
    assertThat(boar.canMove()).isTrue();
    // Test adding a beast on board with rhino present, the beast has charge.
    final Minion timberWolf = minion.addToHandAndPlay(ConstMinion.TIMBER_WOLF);
    minion.addToHandAndPlay(timberWolf);
    assertThat(timberWolf.canMove()).isTrue();
    // Test adding a non-beast on board with rhino present, it cannot move.
    final Minion ooze = minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    minion.addToHandAndPlay(ooze);
    assertThat(ooze.canMove()).isFalse();
  }

  @Test
  public void testGuardianOfKings() {
    game.activeSide.hero.takeDamage(6);
    minion.addToHandAndPlay(ConstMinion.GUARDIAN_OF_KINGS);
    assertThat(game.activeSide.hero.healthLoss()).isEqualTo(0);
  }

  @Test
  public void testTruesilverChampion() {
    // TODO: create weapon test suite.
    final Weapon truesilverChampion = WeaponFactory.create(ConstWeapon.TRUESILVER_CHAMPION);
    game.activeSide.hero.takeDamage(2);
    game.activeSide.hero.equip(truesilverChampion);
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(game.activeSide.hero, yeti);
    assertThat(yeti.healthLoss()).isEqualTo(4);
    // Heal for 2 first, which has no effect because hero has full health already then take 4 damage
    // from yeti. Should have 26 health left.
    assertThat(game.activeSide.hero.healthLoss()).isEqualTo(4);

    EffectFactory.AttackFactory.pipePhysicalDamageEffect(game.activeSide.hero, yeti);
    // Heal for 2 first when take 4 from yeti. Should have 24 health left.
    assertThat(game.activeSide.hero.healthLoss()).isEqualTo(4 - 2 + 4);
    assertThat(game.activeSide.hero.getWeapon().isPresent()).isFalse();
  }

  @Test
  public void testTriggerOnHealMinion() {
    final Minion northshireCleric = minion.addToHandAndPlay(ConstMinion.NORTHSHIRE_CLERIC);
    final int handSize = game.activeSide.hand.size();

    // Wound own minion and heal it.
    yeti.takeDamage(2);
    minion.addToHandAndPlay(ConstMinion.DARKSCALE_HEALER);

    assertThat(yeti.healthLoss()).isEqualTo(0);
    assertThat(game.activeSide.deck.size()).isEqualTo(DECK_SIZE - 1);
    assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 1);

    // Put northshire on opponent's board.
    game.switchTurn();
    minion.addToHandAndPlay(ConstMinion.NORTHSHIRE_CLERIC);
    game.switchTurn();

    // Wound two minion and heal it.
    yeti.takeDamage(2);
    northshireCleric.takeDamage(2);
    minion.addToHandAndPlay(ConstMinion.DARKSCALE_HEALER);

    // With two northshire on board, should draw 2 x 2 = 4 cards.
    assertThat(yeti.healthLoss()).isEqualTo(0);
    assertThat(game.activeSide.deck.size()).isEqualTo(DECK_SIZE - 1 - 2 * 2);
    assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 1 + 2 * 2);
  }

  @Test
  public void testFlametongueTotem() {
    final Minion flametongueTotem = minion.create(ConstMinion.FLAMETONGUE_TOTEM);
    final int flametongueTotemAttack = flametongueTotem.attack().value();
    final int yetiAttack = yeti.attack().value();
    final int waterElementalAttack = waterElemental.attack().value();
    final int scarletCrusaderAttack = scarletCrusader.attack().value();

    assertThat(game.activeSide.board.get(0)).isEqualTo(yeti);
    assertThat(game.activeSide.board.get(1)).isEqualTo(waterElemental);
    assertThat(game.activeSide.board.get(2)).isEqualTo(scarletCrusader);

    minion.addToHandAndPlay(flametongueTotem);
    assertThat(game.activeSide.board.get(0)).isEqualTo(flametongueTotem);
    assertThat(game.activeSide.board.get(1)).isEqualTo(yeti);
    assertThat(game.activeSide.board.get(1).attack().value()).isEqualTo(yetiAttack + 2);

    final Minion ooze = minion.create(ConstMinion.ACIDIC_SWAMP_OOZE);
    final int oozeAttack = ooze.attack().value();
    minion.addToHandAndPlay(ooze);
    assertThat(game.activeSide.board.get(0)).isEqualTo(ooze);
    assertThat(game.activeSide.board.get(0).attack().value()).isEqualTo(oozeAttack + 2);

    // Test that only adjacent minion gets affected by aura.
    assertThat(waterElemental.attack().value()).isEqualTo(waterElementalAttack);
    assertThat(scarletCrusader.attack().value()).isEqualTo(scarletCrusaderAttack);
    assertThat(flametongueTotem.attack().value()).isEqualTo(flametongueTotemAttack);
  }

  @Test
  public void testSuccubus() {
    game.activeSide.hand.add(minion.create(ConstMinion.ACIDIC_SWAMP_OOZE));
    final int handSize = game.activeSide.hand.size();
    minion.addToHandAndPlay(ConstMinion.SUCCUBUS);
    assertThat(game.activeSide.hand.size()).isEqualTo(handSize - 1);
  }

  @Test
  public void testDreadInfernal() {
    final Minion dreadInfernal = minion.addToHandAndPlay(ConstMinion.DREAD_INFERNAL);
    assertThat(dreadInfernal.healthLoss()).isEqualTo(0);
    assertThat(yeti.healthLoss()).isEqualTo(1);
  }

  @Test
  public void testWarsongCommander() {
    final Minion rider = minion.addToHandAndPlay(ConstMinion.WOLFRIDER);
    assertThat(rider.booleanMechanics().isOff(ConstMechanic.CHARGE));

    final int riderAttack = rider.attack().value();
    final int yetiAttack = yeti.attack().value();
    final int waterElementalAttack = waterElemental.attack().value();
    final int scarletCrusaderAttack = scarletCrusader.attack().value();

    final Minion commander = minion.addToHandAndPlay(ConstMinion.WARSONG_COMMANDER);
    // Test that warsong commander only effects minion with charge.
    assertThat(yeti.attack().value()).isEqualTo(yetiAttack);
    assertThat(waterElemental.attack().value()).isEqualTo(waterElementalAttack);
    assertThat(scarletCrusader.attack().value()).isEqualTo(scarletCrusaderAttack);
    assertThat(rider.attack().value()).isEqualTo(riderAttack + 1);

    // Test that when commander is removed from the board, buff is gone as well.
    commander.takeDamage(10);
    assertThat(game.activeSide.board.contains(commander)).isFalse();
    assertThat(rider.attack().value()).isEqualTo(riderAttack);
  }
}
