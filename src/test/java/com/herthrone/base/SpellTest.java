package com.herthrone.base;

import com.herthrone.BaseGame;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.SpellFactory;
import com.herthrone.factory.WeaponFactory;
import com.herthrone.service.BoardSide;
import com.herthrone.service.ContainerType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class SpellTest extends BaseGame {

  private Hero hero;
  private Hero guldan;
  private Minion yeti;
  private int initBoardSize;

  private Spell createSpellAndBind(final ConstSpell spellName) {
    final Spell spell = SpellFactory.create(spellName);
    game.activeSide.bind(spell);
    return spell;
  }

  @Before
  public void setUp() {
    setUpGame(ConstHero.GULDAN, ConstHero.GULDAN);
    this.hero = game.activeSide.hero;
    this.guldan = game.inactiveSide.hero;

    game.startTurn();
    this.yeti = minion.addToHandAndPlay(ConstMinion.CHILLWIND_YETI);
    initBoardSize = game.activeSide.board.size();
  }

  @Test
  public void testFireBall() {
    final int health = yeti.health().value();
    spell.addToHandAndCast(ConstSpell.FIRE_BALL, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.health().value()).isEqualTo(health - 6);
    assertThat(yeti.isDead()).isTrue();
  }

  @Test
  public void testArmorUp() {
    heroPower.update(ConstSpell.ARMOR_UP);
    assertThat(hero.armor().value()).isEqualTo(0);
    heroPower.use();
    assertThat(hero.armor().value()).isEqualTo(2);
  }

  @Test
  public void testLesserHeal() {
    final int largeDamage = 5;
    final int healVol = 2;
    assertThat(hero.healthLoss()).isEqualTo(0);
    hero.takeDamage(largeDamage);
    assertThat(hero.healthLoss()).isEqualTo(largeDamage);

    heroPower.update(ConstSpell.LESSER_HEAL);
    heroPower.use(BoardSide.OWN, ContainerType.HERO, 0);
    assertThat(hero.healthLoss()).isEqualTo(largeDamage - healVol);
    heroPower.use(BoardSide.OWN, ContainerType.HERO, 0);
    assertThat(hero.healthLoss()).isEqualTo(largeDamage - healVol * 2);
    // Healing cannot exceed the health upper bound.
    heroPower.use(BoardSide.OWN, ContainerType.HERO, 0);
    assertThat(hero.healthLoss()).isEqualTo(0);
  }

  @Test
  public void testFireBlast() {
    heroPower.update(ConstSpell.FIRE_BLAST);
    final int damage = 1;
    assertThat(guldan.healthLoss()).isEqualTo(0);
    heroPower.use(BoardSide.FOE, ContainerType.HERO, 0);
    assertThat(guldan.healthLoss()).isEqualTo(damage);
  }

  @Test
  public void testSteadyShot() {
    heroPower.update(ConstSpell.STEADY_SHOT);
    final int damage = 2;

    assertThat(guldan.healthLoss()).isEqualTo(0);
    // TODO: should not need to specify the target.
    heroPower.use(BoardSide.FOE, ContainerType.HERO, 0);
    assertThat(guldan.healthLoss()).isEqualTo(damage);
  }

  @Test
  public void testShapeshift() {
    final int attack = 1;
    final int armor = 1;
    assertThat(hero.attack().value()).isEqualTo(0);
    assertThat(hero.armor().value()).isEqualTo(0);

    heroPower.update(ConstSpell.SHAPESHIFT);
    heroPower.use();

    assertThat(hero.attack().value()).isEqualTo(attack);
    assertThat(hero.armor().value()).isEqualTo(armor);

    hero.endTurn();
    assertThat(hero.attack().value()).isEqualTo(0);
  }

  @Test
  public void testDaggerMastery() {
    heroPower.update(ConstSpell.DAGGER_MASTERY);
    assertThat(hero.canDamage()).isFalse();
    heroPower.use();
    assertThat(hero.canDamage()).isTrue();
  }

  @Test
  public void testReinforce() {
    heroPower.update(ConstSpell.REINFORCE);
    final int boardSize = game.activeSide.board.size();
    heroPower.use();
    assertThat(game.activeSide.board.size()).isEqualTo(boardSize + 1);

    final Minion minion = game.activeSide.board.get(boardSize);
    assertThat(minion.cardName()).isEqualTo(ConstMinion.SILVER_HAND_RECRUIT.toString());
  }

  @Test
  public void testTotemicCall() {
    heroPower.update(ConstSpell.TOTEMIC_CALL);
    final int size = 4;
    for (int i = 0; i < size; ++i) {
      heroPower.use();
      assertThat(game.activeSide.board.size()).isEqualTo(initBoardSize + i + 1);
    }

    final int totemCount = game.activeSide.board.stream()
        .map(Minion::cardName).collect(Collectors.toSet()).size();
    assertThat(totemCount).isEqualTo(initBoardSize + size);
  }

  @Test
  public void testLifeTap() {
    heroPower.update(ConstSpell.LIFE_TAP);
    final int damage = 2;

    final int deckSize = game.activeSide.deck.size();
    final int handSize = game.activeSide.hand.size();

    assertThat(hero.healthLoss()).isEqualTo(0);
    heroPower.use();
    assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 1);
    assertThat(game.activeSide.deck.size()).isEqualTo(deckSize - 1);
    assertThat(hero.healthLoss()).isEqualTo(damage);
  }

  @Test
  public void testWildGrowth() {
    final int manaCrystalCount = game.activeSide.hero.manaCrystal().getCrystalUpperBound();
    spell.addToHandAndCast(ConstSpell.WILD_GROWTH);
    assertThat(hero.manaCrystal().getCrystalUpperBound()).isEqualTo(manaCrystalCount + 1);
  }

  @Test
  public void testInnervate() {
    final int manaCrystalCount = hero.manaCrystal().getCrystal();
    spell.addToHandAndCast(ConstSpell.INNERVATE);
    assertThat(hero.manaCrystal().getCrystal()).isEqualTo(manaCrystalCount + 2);
  }

  @Test
  public void testClaw() {
    final int attack = hero.attack().value();
    final int armor = hero.armor().value();

    spell.addToHandAndCast(ConstSpell.CLAW);
    assertThat(hero.attack().value()).isEqualTo(attack + 2);
    assertThat(hero.armor().value()).isEqualTo(armor + 2);

    hero.endTurn();

    // Test that attack is valid only for one turn but armor gain is permanent.
    assertThat(hero.attack().value()).isEqualTo(attack);
    assertThat(hero.armor().value()).isEqualTo(armor + 2);
  }

  @Test
  public void testMarkOfTheWild() {
    final int attack = yeti.attack().value();
    final int health = yeti.health().value();
    final int maxHealth = yeti.maxHealth().value();
    assertThat(yeti.booleanMechanics().isOff(ConstMechanic.TAUNT));

    spell.addToHandAndCast(ConstSpell.MARK_OF_THE_WILD, BoardSide.OWN, ContainerType.BOARD, 0);

    assertThat(yeti.booleanMechanics().isOn(ConstMechanic.TAUNT));
    assertThat(yeti.attack().value()).isEqualTo(attack + 2);
    assertThat(yeti.health().value()).isEqualTo(health + 2);
    assertThat(yeti.maxHealth().value()).isEqualTo(maxHealth + 2);
  }

  @Test
  public void testSwipe() {
    game.switchTurn();
    final Minion ooze = minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    final Minion yeti = minion.addToHandAndPlay(ConstMinion.CHILLWIND_YETI);
    game.switchTurn();

    spell.addToHandAndCast(ConstSpell.SWIPE, BoardSide.FOE, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(4);
    assertThat(ooze.healthLoss()).isEqualTo(1);
    assertThat(game.inactiveSide.hero.healthLoss()).isEqualTo(1);
  }

  @Test
  public void testMultiShot() {
    final Spell multiShot = createSpellAndBind(ConstSpell.MULTI_SHOT);
    final int damage = 3;

    game.switchTurn();
    for (int i = 0; i < 3; ++i) {
      minion.addToHandAndPlay(ConstMinion.CHILLWIND_YETI);
    }
    game.switchTurn();

    spell.addToHandAndCast(multiShot);
    assertThat(game.inactiveSide.board.stream().filter(m -> m.healthLoss() == damage).count())
        .isEqualTo(2);
  }

  @Test
  public void testTracking() {
    // TODO: to be implemented
  }

  @Test
  public void testHuntersMark() {
    final int value = 1;
    assertThat(yeti.health().value()).isGreaterThan(value);
    assertThat(yeti.maxHealth().value()).isGreaterThan(value);
    spell.addToHandAndCast(ConstSpell.HUNTERS_MARK, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.health().value()).isEqualTo(value);
    assertThat(yeti.maxHealth().value()).isEqualTo(value);
  }

  @Test
  public void testKillCommand() {
    final int damage = 3;
    final int damageBoosted = 5;
    // Test without beast on board.
    spell.addToHandAndCast(ConstSpell.KILL_COMMAND, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(damage);

    minion.addToHandAndPlay(ConstMinion.BOAR);

    // Test with beast on board.
    yeti.health().increase(damage);
    spell.addToHandAndCast(ConstSpell.KILL_COMMAND, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(damageBoosted);
  }

  @Test
  public void testPolymorph() {
    final int position = game.activeSide.board.indexOf(yeti);
    spell.addToHandAndCast(ConstSpell.POLYMORPH, BoardSide.OWN, ContainerType.BOARD, position);
    assertThat(game.activeSide.board.get(position)).isNotEqualTo(yeti);
    assertThat(game.activeSide.board.get(position).cardName()).isEqualTo(ConstMinion.SHEEP.toString());
  }

  @Test
  public void testFrostNova() {
    game.switchTurn();
    final Minion yeti1 = minion.addToHandAndPlay(ConstMinion.CHILLWIND_YETI);
    final Minion yeti2 = minion.addToHandAndPlay(ConstMinion.CHILLWIND_YETI);
    game.switchTurn();

    assertThat(yeti1.booleanMechanics().isOff(ConstMechanic.FROZEN)).isTrue();
    assertThat(yeti2.booleanMechanics().isOff(ConstMechanic.FROZEN)).isTrue();

    spell.addToHandAndCast(ConstSpell.FROST_NOVA);

    assertThat(yeti1.booleanMechanics().isOn(ConstMechanic.FROZEN)).isTrue();
    assertThat(yeti2.booleanMechanics().isOn(ConstMechanic.FROZEN)).isTrue();

    yeti1.startTurn();
    yeti2.startTurn();
    assertThat(yeti1.booleanMechanics().isOff(ConstMechanic.FROZEN)).isTrue();
    assertThat(yeti2.booleanMechanics().isOff(ConstMechanic.FROZEN)).isTrue();
  }

  @Test
  public void testFrostbolt() {
    assertThat(yeti.booleanMechanics().isOff(ConstMechanic.FROZEN)).isTrue();
    spell.addToHandAndCast(ConstSpell.FROSTBOLT, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(3);
    assertThat(yeti.booleanMechanics().isOn(ConstMechanic.FROZEN)).isTrue();
  }

  @Test
  public void testMirrorImage() {
    spell.addToHandAndCast(ConstSpell.MIRROR_IMAGE);
    assertThat(game.activeSide.board.size()).isEqualTo(initBoardSize + 2);
    assertThat(game.activeSide.board.get(initBoardSize + 1).cardName()).isEqualTo(
        ConstMinion.MIRROR_IMAGE_MINION.toString());
    assertThat(game.activeSide.board.get(initBoardSize).cardName()).isEqualTo(
        ConstMinion.MIRROR_IMAGE_MINION.toString());
  }

  @Test
  public void testDealDamageAndDrawCardMixture() {
    final int deckSize = game.activeSide.deck.size();
    final int handSize = game.activeSide.hand.size();
    spell.addToHandAndCast(ConstSpell.HAMMER_OF_WRATH, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(3);
    assertThat(game.activeSide.deck.size()).isEqualTo(deckSize - 1);
    assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 1);
  }

  @Test
  public void testHumility() {
    spell.addToHandAndCast(ConstSpell.HUMILITY, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.attack().value()).isEqualTo(1);
  }

  @Test
  public void testPowerWordShield() {
    final int deckSize = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    for (int i = 0; i < deckSize; ++i) {
      game.activeSide.deck.add(minion.create(ConstMinion.CHILLWIND_YETI));
    }
    final int handSize = game.activeSide.hand.size();
    game.switchTurn();
    final Minion ooze = minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    game.switchTurn();

    spell.addToHandAndCast(ConstSpell.POWER_WORD_SHIELD, BoardSide.FOE, ContainerType.BOARD, 0);

    assertThat(game.activeSide.deck.size()).isEqualTo(deckSize - 1);
    assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 1);
    assertThat(ooze.health().value()).isEqualTo(4);
  }

  @Test
  public void testDivineSpirit() {
    final int health = yeti.health().value();
    spell.addToHandAndCast(ConstSpell.DIVINE_SPIRIT, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.health().value()).isEqualTo(2 * health);
  }

  @Test
  public void testHolyNova() {
    yeti.takeDamage(2);
    game.activeSide.hero.takeDamage(2);

    game.switchTurn();
    final Minion ooze = minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    game.switchTurn();

    spell.addToHandAndCast(ConstSpell.HOLY_NOVA);

    // Test own side all healed by 2.
    assertThat(yeti.healthLoss()).isEqualTo(0);
    assertThat(game.activeSide.hero.healthLoss()).isEqualTo(0);
    // Test opponent side all damage by 2.
    assertThat(ooze.healthLoss()).isEqualTo(2);
    assertThat(game.inactiveSide.hero.healthLoss()).isEqualTo(2);
  }

  @Test
  public void testMindControl() {
    game.switchTurn();
    final Minion ooze = minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    game.switchTurn();

    spell.addToHandAndCast(ConstSpell.MIND_CONTROL, BoardSide.FOE, ContainerType.BOARD, 0);

    assertThat(game.activeSide.board.get(game.activeSide.board.size() - 1)).isEqualTo(ooze);
    assertThat(game.inactiveSide.board.contains(ooze)).isFalse();
  }

  @Test
  public void testMindVision() {
    game.switchTurn();
    game.activeSide.hand.add(minion.create(ConstMinion.ACIDIC_SWAMP_OOZE));
    game.activeSide.hand.add(minion.create(ConstMinion.ACIDIC_SWAMP_OOZE));
    game.switchTurn();

    final int handSize = game.activeSide.hand.size();
    spell.addToHandAndCast(ConstSpell.MIND_VISION);
    assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 1);
    assertThat(game.activeSide.hand.get(game.activeSide.hand.size() - 1).cardName())
        .isEqualTo(ConstMinion.ACIDIC_SWAMP_OOZE.toString());
  }

  @Test
  public void testShadowWord() {
    final Spell shadowWordPain = createSpellAndBind(ConstSpell.SHADOW_WORD_PAIN);
    // In reality, shadow word: pain cannot be used to target at yeti but here
    // it's just testing when it's targeted, no effect happens.
    spell.addToHandAndCast(ConstSpell.SHADOW_WORD_PAIN, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(game.activeSide.board.contains(yeti)).isTrue();
    yeti.attack().getPermanentBuff().increase(1);
    assertThat(yeti.attack().value()).isEqualTo(5);
    spell.addToHandAndCast(ConstSpell.SHADOW_WORD_DEATH, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(game.activeSide.board.contains(yeti)).isFalse();
    final Minion ooze = minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    // Test that ooze can be targeted by shadow word: pain.
    assertThat(game.activeSide.board.contains(ooze)).isTrue();
    spell.addToHandAndCast(ConstSpell.SHADOW_WORD_PAIN, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(game.activeSide.board.contains(ooze)).isFalse();
  }

  @Test
  public void testBackStab() {
    spell.addToHandAndCast(ConstSpell.BACKSTAB, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(2);
    // Should not be targetable. Just test no effect happens.
    spell.addToHandAndCast(ConstSpell.BACKSTAB, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(2);
  }

  @Test
  public void testDeadlyPoison() {
    final Weapon assasinsBlade = WeaponFactory.create(ConstWeapon.ASSASSINS_BLADE);
    final int attack = assasinsBlade.getAttackAttr().value();
    hero.equip(assasinsBlade);
    spell.addToHandAndCast(ConstSpell.DEADLY_POISON);
    assertThat(assasinsBlade.getAttackAttr().value()).isEqualTo(attack + 2);
  }

  @Test
  public void testSprint() {
    final int deckSize = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    for (int i = 0; i < deckSize; ++i) {
      game.activeSide.deck.add(MinionFactory.create(ConstMinion.CHILLWIND_YETI));
    }

    final int handSize = game.activeSide.hand.size();
    spell.addToHandAndCast(ConstSpell.SPRINT);

    assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 4);
    assertThat(game.activeSide.deck.size()).isEqualTo(deckSize - 4);
  }

  @Test
  public void testVanish() {
    minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    game.switchTurn();
    minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    game.switchTurn();

    final int activeSideHandSize = game.activeSide.hand.size();
    final int activeSideBoardSize = game.activeSide.board.size();
    final int inactiveSideHandSize = game.inactiveSide.hand.size();
    final int inactiveSideBoardSize = game.inactiveSide.board.size();

    spell.addToHandAndCast(ConstSpell.VANISH);

    assertThat(game.activeSide.board.size()).isEqualTo(0);
    assertThat(game.activeSide.hand.size()).isEqualTo(activeSideHandSize + activeSideBoardSize);
    assertThat(game.inactiveSide.board.size()).isEqualTo(0);
    assertThat(game.inactiveSide.hand.size()).isEqualTo(inactiveSideHandSize + inactiveSideBoardSize);
  }

  @Test
  public void testAncestralHealing() {
    yeti.takeDamage(1);
    assertThat(yeti.booleanMechanics().isOff(ConstMechanic.TAUNT));
    spell.addToHandAndCast(ConstSpell.ANCESTRAL_HEALING, BoardSide.OWN, ContainerType.BOARD, 0);

    assertThat(yeti.booleanMechanics().isOn(ConstMechanic.TAUNT));
    assertThat(yeti.healthLoss()).isEqualTo(0);

    yeti.takeDamage(4);
    spell.addToHandAndCast(ConstSpell.ANCESTRAL_HEALING, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(0);
  }

  @Test
  public void testRockbiterWeapon() {
    final int minionAttack = yeti.attack().value();
    spell.addToHandAndCast(ConstSpell.ROCKBITER_WEAPON, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.attack().value()).isEqualTo(minionAttack + 3);
    yeti.endTurn();
    assertThat(yeti.attack().value()).isEqualTo(minionAttack);

    final int heroAttack = hero.attack().value();
    spell.addToHandAndCast(ConstSpell.ROCKBITER_WEAPON, BoardSide.OWN, ContainerType.HERO, 0);
    assertThat(hero.attack().value()).isEqualTo(heroAttack + 3);
    hero.endTurn();
    assertThat(hero.attack().value()).isEqualTo(heroAttack);
  }

  @Test
  public void testWindfury() {
    // Test a minion just put on board.
    assertThat(yeti.attackMovePoints().value()).isEqualTo(0);
    spell.addToHandAndCast(ConstSpell.WINDFURY, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.attackMovePoints().value()).isEqualTo(0);
    yeti.endTurn();
    assertThat(yeti.attackMovePoints().value()).isEqualTo(2);
    // Test a minion in second round.
    final Minion ooze = minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    assertThat(ooze.attackMovePoints().value()).isEqualTo(0);
    ooze.endTurn();
    assertThat(ooze.attackMovePoints().value()).isEqualTo(1);
    spell.addToHandAndCast(ConstSpell.WINDFURY, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(ooze.attackMovePoints().value()).isEqualTo(2);
    // Test a minion in second round that has attacked already.
    final Minion bodyguard = minion.addToHandAndPlay(ConstMinion.BOOTY_BAY_BODYGUARD);
    bodyguard.endTurn();
    assertThat(bodyguard.attackMovePoints().value()).isEqualTo(1);
    bodyguard.attackMovePoints().decrease(1);
    assertThat(bodyguard.attackMovePoints().value()).isEqualTo(0);
    spell.addToHandAndCast(ConstSpell.WINDFURY, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(bodyguard.attackMovePoints().value()).isEqualTo(1);
  }

  @Test
  public void testTotemicMight() {
    final Minion healingTotem = minion.create(ConstMinion.HEALING_TOTEM);
    final Minion searingTotem = minion.create(ConstMinion.SEARING_TOTEM);

    final int healingTotemHealth = healingTotem.health().value();
    final int searingTotemHealth = searingTotem.health().value();
    final int yetiHealth = yeti.health().value();

    minion.addToHandAndPlay(healingTotem);
    minion.addToHandAndPlay(searingTotem);
    spell.addToHandAndCast(ConstSpell.TOTEMIC_MIGHT);

    assertThat(healingTotem.health().value()).isEqualTo(healingTotemHealth + 2);
    assertThat(searingTotem.health().value()).isEqualTo(searingTotemHealth + 2);
    assertThat(yeti.health().value()).isEqualTo(yetiHealth);
  }

  @Test
  public void testDrainLife() {
    game.activeSide.hero.takeDamage(2);
    spell.addToHandAndCast(ConstSpell.DRAIN_LIFE, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(2);
  }

  @Test
  public void testCorruption() {
    game.switchTurn();
    final Minion ooze = minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    game.switchTurn();
    spell.addToHandAndCast(ConstSpell.CORRUPTION, BoardSide.FOE, ContainerType.BOARD, 0);

    game.switchTurn();
    assertThat(game.activeSide.board.contains(ooze)).isTrue();
    game.switchTurn();
    assertThat(game.inactiveSide.board.contains(ooze)).isFalse();
  }

  @Test
  public void testMortalCoil() {
    yeti.takeDamage(3);
    assertThat(yeti.health().value()).isEqualTo(2);
    final int deckSize = game.activeSide.deck.size();
    final int handSize = game.activeSide.hand.size();
    spell.addToHandAndCast(ConstSpell.MORTAL_COIL, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.health().value()).isEqualTo(1);
    assertThat(game.activeSide.deck.size()).isEqualTo(deckSize);
    assertThat(game.activeSide.hand.size()).isEqualTo(handSize);

    spell.addToHandAndCast(ConstSpell.MORTAL_COIL, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.isDead()).isTrue();
    // TODO: doesn't work with current effect/trigger factory.
    //assertThat(game.activeSide.deck.size()).isEqualTo(deckSize - 1);
    //assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 1);
  }

  @Test
  public void testExecute() {
    spell.addToHandAndCast(ConstSpell.EXECUTE, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(game.activeSide.board.contains(yeti)).isTrue();

    yeti.takeDamage(1);
    spell.addToHandAndCast(ConstSpell.EXECUTE, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(game.activeSide.board.contains(yeti)).isFalse();
  }

  @Test
  public void testCleave() {
    game.switchTurn();
    final Minion ooze = minion.addToHandAndPlay(ConstMinion.ACIDIC_SWAMP_OOZE);
    final Minion grizzly = minion.addToHandAndPlay(ConstMinion.IRONFUR_GRIZZLY);
    final Minion dalaranMage = minion.addToHandAndPlay(ConstMinion.DALARAN_MAGE);
    assertThat(game.activeSide.board.size()).isEqualTo(3);
    game.switchTurn();

    spell.addToHandAndCast(ConstSpell.CLEAVE);

    final int damagedMinionCount = ((ooze.healthLoss() > 0) ? 1 : 0) +
        ((grizzly.healthLoss() > 0) ? 1 : 0) +
        ((dalaranMage.healthLoss() > 0) ? 1: 0);
    assertThat(damagedMinionCount).isEqualTo(2);
  }
}
