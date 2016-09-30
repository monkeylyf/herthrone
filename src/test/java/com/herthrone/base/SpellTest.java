package com.herthrone.base;

import com.herthrone.configuration.ConfigLoader;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.HeroPowerFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.SpellFactory;
import com.herthrone.factory.WeaponFactory;
import com.herthrone.game.Game;
import com.herthrone.service.BoardSide;
import com.herthrone.service.Command;
import com.herthrone.service.CommandType;
import com.herthrone.service.ContainerType;
import com.herthrone.service.Entity;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class SpellTest extends TestCase {

  private Hero hero;
  private Hero guldan;
  private Minion yeti;
  private int initBoardSize;
  private Game game;
  private static final int DECK_SIZE = Integer.parseInt(
      ConfigLoader.getResource().getString("deck_max_capacity"));

  private void useHeroPower(final BoardSide side, final ContainerType containerType,
                            final int index) {
    final Command useHeroPowerCommand = Command.newBuilder()
        .setType(CommandType.USE_HERO_POWER)
        .setTarget(Entity.newBuilder()
            .setSide(side)
            .setContainerType(containerType)
            .setPosition(index))
        .build();
    game.command(useHeroPowerCommand);
  }

  private void addCardToHandAndPlayItOnOwnBoard(final ConstMinion minionName) {
    final Minion minion = createAndBindMinion(minionName);
    addCardToHandAndPlayItOnOwnBoard(minion);
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

  private void updateHeroPower(final ConstSpell spellName) {
    final Spell heroPower = createHeroPowerAndBind(spellName);
    hero.setHeroPower(heroPower);
  }

  private void useHeroPower() {
    final Command useHeroPowerCommand = Command.newBuilder()
        .setType(CommandType.USE_HERO_POWER)
        .build();
    game.command(useHeroPowerCommand);
  }

  private Spell createHeroPowerAndBind(final ConstSpell spellName) {
    final Spell spell = HeroPowerFactory.create(spellName);
    game.activeSide.bind(spell);
    return spell;
  }

  private Spell createSpellAndBind(final ConstSpell spellName) {
    final Spell spell = SpellFactory.create(spellName);
    game.activeSide.bind(spell);
    return spell;
  }

  private Minion createAndBindMinion(final ConstMinion minionName) {
    final Minion minion = MinionFactory.create(minionName);
    game.activeSide.bind(minion);
    return minion;
  }

  private void addSpellToHandAndCastIt(final ConstSpell spellName, final BoardSide side,
                                       final ContainerType containerType, final int index) {
    final Spell spell = createSpellAndBind(spellName);
    addSpellToHandAndCastIt(spell, side, containerType, index);
  }
  private void addSpellToHandAndCastIt(final ConstSpell spellName) {
    final Spell spell = createSpellAndBind(spellName);
    addSpellToHandAndCastIt(spell);
  }

  private void addSpellToHandAndCastIt(final Card card) {
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

  private void addSpellToHandAndCastIt(final Card card, final BoardSide side,
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

  @Before
  public void setUp() {
    final List<Enum> cards = Collections.nCopies(DECK_SIZE, ConstMinion.CHILLWIND_YETI);
    this.game = new Game("gameId", ConstHero.GULDAN, ConstHero.GULDAN, cards, cards);
    this.hero = game.activeSide.hero;
    this.guldan = game.inactiveSide.hero;

    game.startTurn();
    guldan1PlayYeti();
    initBoardSize = game.activeSide.board.size();
  }

  private void guldan1PlayYeti() {
    final Command playYetiCommand = Command.newBuilder()
        .setBoardPosition(0)
        .setType(CommandType.PLAY_CARD)
        .setDoer(Entity.newBuilder()
            .setSide(BoardSide.OWN)
            .setContainerType(ContainerType.HAND)
            .setPosition(0))
        .build();
    game.command(playYetiCommand);
    this.yeti = game.activeSide.board.get(0);
  }

  @Test
  public void testFireBall() {
    final int health = yeti.health().value();
    addSpellToHandAndCastIt(ConstSpell.FIRE_BALL, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.health().value()).isEqualTo(health - 6);
    assertThat(yeti.isDead()).isTrue();
  }

  @Test
  public void testArmorUp() {
    updateHeroPower(ConstSpell.ARMOR_UP);

    assertThat(hero.armor().value()).isEqualTo(0);
    useHeroPower();
    assertThat(hero.armor().value()).isEqualTo(2);
  }

  @Test
  public void testLesserHeal() {
    final int largeDamage = 5;
    final int healVol = 2;
    assertThat(hero.healthLoss()).isEqualTo(0);
    hero.takeDamage(largeDamage);
    assertThat(hero.healthLoss()).isEqualTo(largeDamage);

    updateHeroPower(ConstSpell.LESSER_HEAL);
    useHeroPower(BoardSide.OWN, ContainerType.HERO, 0);
    assertThat(hero.healthLoss()).isEqualTo(largeDamage - healVol);
    useHeroPower(BoardSide.OWN, ContainerType.HERO, 0);
    assertThat(hero.healthLoss()).isEqualTo(largeDamage - healVol * 2);
    // Healing cannot exceed the health upper bound.
    useHeroPower(BoardSide.OWN, ContainerType.HERO, 0);
    assertThat(hero.healthLoss()).isEqualTo(0);
  }

  @Test
  public void testFireBlast() {
    updateHeroPower(ConstSpell.FIRE_BLAST);
    final int damage = 1;
    assertThat(guldan.healthLoss()).isEqualTo(0);
    useHeroPower(BoardSide.FOE, ContainerType.HERO, 0);
    assertThat(guldan.healthLoss()).isEqualTo(damage);
  }

  @Test
  public void testSteadyShot() {
    updateHeroPower(ConstSpell.STEADY_SHOT);
    final int damage = 2;

    assertThat(guldan.healthLoss()).isEqualTo(0);
    // TODO: should not need to specify the target.
    useHeroPower(BoardSide.FOE, ContainerType.HERO, 0);
    assertThat(guldan.healthLoss()).isEqualTo(damage);
  }

  @Test
  public void testShapeshift() {
    final int attack = 1;
    final int armor = 1;
    assertThat(hero.attack().value()).isEqualTo(0);
    assertThat(hero.armor().value()).isEqualTo(0);

    updateHeroPower(ConstSpell.SHAPESHIFT);
    useHeroPower();

    assertThat(hero.attack().value()).isEqualTo(attack);
    assertThat(hero.armor().value()).isEqualTo(armor);

    hero.endTurn();
    assertThat(hero.attack().value()).isEqualTo(0);
  }

  @Test
  public void testDaggerMastery() {
    updateHeroPower(ConstSpell.DAGGER_MASTERY);
    assertThat(hero.canDamage()).isFalse();
    useHeroPower();
    assertThat(hero.canDamage()).isTrue();
  }

  @Test
  public void testReinforce() {
    updateHeroPower(ConstSpell.REINFORCE);
    final int boardSize = game.activeSide.board.size();
    useHeroPower();
    assertThat(game.activeSide.board.size()).isEqualTo(boardSize + 1);

    final Minion minion = game.activeSide.board.get(boardSize);
    assertThat(minion.cardName()).isEqualTo(ConstMinion.SILVER_HAND_RECRUIT.toString());
  }

  @Test
  public void testTotemicCall() {
    updateHeroPower(ConstSpell.TOTEMIC_CALL);
    final int size = 4;
    for (int i = 0; i < size; ++i) {
      useHeroPower();
      assertThat(game.activeSide.board.size()).isEqualTo(initBoardSize + i + 1);
    }

    final int totemCount = game.activeSide.board.stream()
        .map(Minion::cardName).collect(Collectors.toSet()).size();
    assertThat(totemCount).isEqualTo(initBoardSize + size);
  }

  @Test
  public void testLifeTap() {
    updateHeroPower(ConstSpell.LIFE_TAP);
    final int damage = 2;

    final int deckSize = game.activeSide.deck.size();
    final int handSize = game.activeSide.hand.size();

    assertThat(hero.healthLoss()).isEqualTo(0);
    useHeroPower();
    assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 1);
    assertThat(game.activeSide.deck.size()).isEqualTo(deckSize - 1);
    assertThat(hero.healthLoss()).isEqualTo(damage);
  }

  @Test
  public void testWildGrowth() {
    final int manaCrystalCount = game.activeSide.hero.manaCrystal().getCrystalUpperBound();
    addSpellToHandAndCastIt(ConstSpell.WILD_GROWTH);
    assertThat(hero.manaCrystal().getCrystalUpperBound()).isEqualTo(manaCrystalCount + 1);
  }

  @Test
  public void testInnervate() {
    final int manaCrystalCount = hero.manaCrystal().getCrystal();
    addSpellToHandAndCastIt(ConstSpell.INNERVATE);
    assertThat(hero.manaCrystal().getCrystal()).isEqualTo(manaCrystalCount + 2);
  }

  @Test
  public void testClaw() {
    final int attack = hero.attack().value();
    final int armor = hero.armor().value();

    addSpellToHandAndCastIt(ConstSpell.CLAW);
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

    addSpellToHandAndCastIt(ConstSpell.MARK_OF_THE_WILD, BoardSide.OWN, ContainerType.BOARD, 0);

    assertThat(yeti.booleanMechanics().isOn(ConstMechanic.TAUNT));
    assertThat(yeti.attack().value()).isEqualTo(attack + 2);
    assertThat(yeti.health().value()).isEqualTo(health + 2);
    assertThat(yeti.maxHealth().value()).isEqualTo(maxHealth + 2);
  }

  @Test
  public void testSwipe() {
    game.switchTurn();
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    addCardToHandAndPlayItOnOwnBoard(ooze);
    final Minion yeti = createAndBindMinion(ConstMinion.CHILLWIND_YETI);
    addCardToHandAndPlayItOnOwnBoard(yeti);
    game.switchTurn();

    addSpellToHandAndCastIt(ConstSpell.SWIPE, BoardSide.FOE, ContainerType.BOARD, 0);
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
      addCardToHandAndPlayItOnOwnBoard(ConstMinion.CHILLWIND_YETI);
    }
    game.switchTurn();

    addSpellToHandAndCastIt(multiShot);
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
    addSpellToHandAndCastIt(ConstSpell.HUNTERS_MARK, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.health().value()).isEqualTo(value);
    assertThat(yeti.maxHealth().value()).isEqualTo(value);
  }

  @Test
  public void testKillCommand() {
    final int damage = 3;
    final int damageBoosted = 5;
    // Test without beast on board.
    addSpellToHandAndCastIt(ConstSpell.KILL_COMMAND, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(damage);

    addCardToHandAndPlayItOnOwnBoard(ConstMinion.BOAR);

    // Test with beast on board.
    yeti.health().increase(damage);
    addSpellToHandAndCastIt(ConstSpell.KILL_COMMAND, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(damageBoosted);
  }

  @Test
  public void testPolymorph() {
    final int position = game.activeSide.board.indexOf(yeti);
    addSpellToHandAndCastIt(ConstSpell.POLYMORPH, BoardSide.OWN, ContainerType.BOARD, position);
    assertThat(game.activeSide.board.get(position)).isNotEqualTo(yeti);
    assertThat(game.activeSide.board.get(position).cardName()).isEqualTo(ConstMinion.SHEEP.toString());
  }

  @Test
  public void testFrostNova() {
    game.switchTurn();
    final Minion yeti1 = createAndBindMinion(ConstMinion.CHILLWIND_YETI);
    addCardToHandAndPlayItOnOwnBoard(yeti1);
    final Minion yeti2 = createAndBindMinion(ConstMinion.CHILLWIND_YETI);
    addCardToHandAndPlayItOnOwnBoard(yeti2);
    game.switchTurn();

    assertThat(yeti1.booleanMechanics().isOff(ConstMechanic.FROZEN)).isTrue();
    assertThat(yeti2.booleanMechanics().isOff(ConstMechanic.FROZEN)).isTrue();

    addSpellToHandAndCastIt(ConstSpell.FROST_NOVA);

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
    addSpellToHandAndCastIt(ConstSpell.FROSTBOLT, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(3);
    assertThat(yeti.booleanMechanics().isOn(ConstMechanic.FROZEN)).isTrue();
  }

  @Test
  public void testMirrorImage() {
    addSpellToHandAndCastIt(ConstSpell.MIRROR_IMAGE);
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
    addSpellToHandAndCastIt(ConstSpell.HAMMER_OF_WRATH, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(3);
    assertThat(game.activeSide.deck.size()).isEqualTo(deckSize - 1);
    assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 1);
  }

  @Test
  public void testHumility() {
    addSpellToHandAndCastIt(ConstSpell.HUMILITY, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.attack().value()).isEqualTo(1);
  }

  @Test
  public void testPowerWordShield() {
    final int deckSize = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    for (int i = 0; i < deckSize; ++i) {
      game.activeSide.deck.add(createAndBindMinion(ConstMinion.CHILLWIND_YETI));
    }
    final int handSize = game.activeSide.hand.size();
    game.switchTurn();
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    addCardToHandAndPlayItOnOwnBoard(ooze);
    game.switchTurn();

    addSpellToHandAndCastIt(ConstSpell.POWER_WORD_SHIELD, BoardSide.FOE, ContainerType.BOARD, 0);

    assertThat(game.activeSide.deck.size()).isEqualTo(deckSize - 1);
    assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 1);
    assertThat(ooze.health().value()).isEqualTo(4);
  }

  @Test
  public void testDivineSpirit() {
    final int health = yeti.health().value();
    addSpellToHandAndCastIt(ConstSpell.DIVINE_SPIRIT, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.health().value()).isEqualTo(2 * health);
  }

  @Test
  public void testHolyNova() {
    yeti.takeDamage(2);
    game.activeSide.hero.takeDamage(2);

    game.switchTurn();
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    addCardToHandAndPlayItOnOwnBoard(ooze);
    game.switchTurn();

    addSpellToHandAndCastIt(ConstSpell.HOLY_NOVA);

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
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    addCardToHandAndPlayItOnOwnBoard(ooze);
    game.switchTurn();

    addSpellToHandAndCastIt(ConstSpell.MIND_CONTROL, BoardSide.FOE, ContainerType.BOARD, 0);

    assertThat(game.activeSide.board.get(game.activeSide.board.size() - 1)).isEqualTo(ooze);
    assertThat(game.inactiveSide.board.contains(ooze)).isFalse();
  }

  @Test
  public void testMindVision() {
    game.switchTurn();
    game.activeSide.hand.add(createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE));
    game.activeSide.hand.add(createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE));
    game.switchTurn();

    final int handSize = game.activeSide.hand.size();
    addSpellToHandAndCastIt(ConstSpell.MIND_VISION);
    assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 1);
    assertThat(game.activeSide.hand.get(game.activeSide.hand.size() - 1).cardName())
        .isEqualTo(ConstMinion.ACIDIC_SWAMP_OOZE.toString());
  }

  @Test
  public void testShadowWord() {
    final Spell shadowWordPain = createSpellAndBind(ConstSpell.SHADOW_WORD_PAIN);
    // In reality, shadow word: pain cannot be used to target at yeti but here
    // it's just testing when it's targeted, no effect happens.
    addSpellToHandAndCastIt(ConstSpell.SHADOW_WORD_PAIN, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(game.activeSide.board.contains(yeti)).isTrue();
    yeti.attack().getPermanentBuff().increase(1);
    assertThat(yeti.attack().value()).isEqualTo(5);
    addSpellToHandAndCastIt(ConstSpell.SHADOW_WORD_DEATH, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(game.activeSide.board.contains(yeti)).isFalse();
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    addCardToHandAndPlayItOnOwnBoard(ooze);
    // Test that ooze can be targeted by shadow word: pain.
    assertThat(game.activeSide.board.contains(ooze)).isTrue();
    addSpellToHandAndCastIt(ConstSpell.SHADOW_WORD_PAIN, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(game.activeSide.board.contains(ooze)).isFalse();
  }

  @Test
  public void testBackStab() {
    addSpellToHandAndCastIt(ConstSpell.BACKSTAB, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(2);
    // Should not be targetable. Just test no effect happens.
    addSpellToHandAndCastIt(ConstSpell.BACKSTAB, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(2);
  }

  @Test
  public void testDeadlyPoison() {
    final Weapon assasinsBlade = WeaponFactory.create(ConstWeapon.ASSASSINS_BLADE);
    final int attack = assasinsBlade.getAttackAttr().value();
    hero.equip(assasinsBlade);
    addSpellToHandAndCastIt(ConstSpell.DEADLY_POISON);
    assertThat(assasinsBlade.getAttackAttr().value()).isEqualTo(attack + 2);
  }

  @Test
  public void testSprint() {
    final int deckSize = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    for (int i = 0; i < deckSize; ++i) {
      game.activeSide.deck.add(MinionFactory.create(ConstMinion.CHILLWIND_YETI));
    }

    final int handSize = game.activeSide.hand.size();
    addSpellToHandAndCastIt(ConstSpell.SPRINT);

    assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 4);
    assertThat(game.activeSide.deck.size()).isEqualTo(deckSize - 4);
  }

  @Test
  public void testVanish() {
    addCardToHandAndPlayItOnOwnBoard(ConstMinion.ACIDIC_SWAMP_OOZE);
    game.switchTurn();
    addCardToHandAndPlayItOnOwnBoard(ConstMinion.ACIDIC_SWAMP_OOZE);
    addCardToHandAndPlayItOnOwnBoard(ConstMinion.ACIDIC_SWAMP_OOZE);
    game.switchTurn();

    final int activeSideHandSize = game.activeSide.hand.size();
    final int activeSideBoardSize = game.activeSide.board.size();
    final int inactiveSideHandSize = game.inactiveSide.hand.size();
    final int inactiveSideBoardSize = game.inactiveSide.board.size();

    addSpellToHandAndCastIt(ConstSpell.VANISH);

    assertThat(game.activeSide.board.size()).isEqualTo(0);
    assertThat(game.activeSide.hand.size()).isEqualTo(activeSideHandSize + activeSideBoardSize);
    assertThat(game.inactiveSide.board.size()).isEqualTo(0);
    assertThat(game.inactiveSide.hand.size()).isEqualTo(inactiveSideHandSize + inactiveSideBoardSize);
  }

  @Test
  public void testAncestralHealing() {
    yeti.takeDamage(1);
    assertThat(yeti.booleanMechanics().isOff(ConstMechanic.TAUNT));
    addSpellToHandAndCastIt(ConstSpell.ANCESTRAL_HEALING, BoardSide.OWN, ContainerType.BOARD, 0);

    assertThat(yeti.booleanMechanics().isOn(ConstMechanic.TAUNT));
    assertThat(yeti.healthLoss()).isEqualTo(0);

    yeti.takeDamage(4);
    addSpellToHandAndCastIt(ConstSpell.ANCESTRAL_HEALING, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(0);
  }

  @Test
  public void testRockbiterWeapon() {
    final int minionAttack = yeti.attack().value();
    addSpellToHandAndCastIt(ConstSpell.ROCKBITER_WEAPON, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.attack().value()).isEqualTo(minionAttack + 3);
    yeti.endTurn();
    assertThat(yeti.attack().value()).isEqualTo(minionAttack);

    final int heroAttack = hero.attack().value();
    addSpellToHandAndCastIt(ConstSpell.ROCKBITER_WEAPON, BoardSide.OWN, ContainerType.HERO, 0);
    assertThat(hero.attack().value()).isEqualTo(heroAttack + 3);
    hero.endTurn();
    assertThat(hero.attack().value()).isEqualTo(heroAttack);
  }

  @Test
  public void testWindfury() {
    final Spell windfury = createSpellAndBind(ConstSpell.WINDFURY);
    // Test a minion just put on board.
    assertThat(yeti.attackMovePoints().value()).isEqualTo(0);
    addSpellToHandAndCastIt(ConstSpell.WINDFURY, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.attackMovePoints().value()).isEqualTo(0);
    yeti.endTurn();
    assertThat(yeti.attackMovePoints().value()).isEqualTo(2);
    // Test a minion in second round.
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    addCardToHandAndPlayItOnOwnBoard(ooze);
    assertThat(ooze.attackMovePoints().value()).isEqualTo(0);
    ooze.endTurn();
    assertThat(ooze.attackMovePoints().value()).isEqualTo(1);
    addSpellToHandAndCastIt(ConstSpell.WINDFURY, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(ooze.attackMovePoints().value()).isEqualTo(2);
    // Test a minion in second round that has attacked already.
    final Minion bodyguard = createAndBindMinion(ConstMinion.BOOTY_BAY_BODYGUARD);
    addCardToHandAndPlayItOnOwnBoard(bodyguard);
    bodyguard.endTurn();
    assertThat(bodyguard.attackMovePoints().value()).isEqualTo(1);
    bodyguard.attackMovePoints().decrease(1);
    assertThat(bodyguard.attackMovePoints().value()).isEqualTo(0);
    addSpellToHandAndCastIt(ConstSpell.WINDFURY, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(bodyguard.attackMovePoints().value()).isEqualTo(1);
  }

  @Test
  public void testTotemicMight() {
    final Minion healingTotem = createAndBindMinion(ConstMinion.HEALING_TOTEM);
    final Minion searingTotem = createAndBindMinion(ConstMinion.SEARING_TOTEM);

    final int healingTotemHealth = healingTotem.health().value();
    final int searingTotemHealth = searingTotem.health().value();
    final int yetiHealth = yeti.health().value();

    addCardToHandAndPlayItOnOwnBoard(healingTotem);
    addCardToHandAndPlayItOnOwnBoard(searingTotem);
    addSpellToHandAndCastIt(ConstSpell.TOTEMIC_MIGHT);

    assertThat(healingTotem.health().value()).isEqualTo(healingTotemHealth + 2);
    assertThat(searingTotem.health().value()).isEqualTo(searingTotemHealth + 2);
    assertThat(yeti.health().value()).isEqualTo(yetiHealth);
  }

  @Test
  public void testDrainLife() {
    game.activeSide.hero.takeDamage(2);
    addSpellToHandAndCastIt(ConstSpell.DRAIN_LIFE, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(2);
  }

  @Test
  public void testCorruption() {
    game.switchTurn();
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    addCardToHandAndPlayItOnOwnBoard(ooze);
    game.switchTurn();
    addSpellToHandAndCastIt(ConstSpell.CORRUPTION, BoardSide.FOE, ContainerType.BOARD, 0);

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
    addSpellToHandAndCastIt(ConstSpell.MORTAL_COIL, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.health().value()).isEqualTo(1);
    assertThat(game.activeSide.deck.size()).isEqualTo(deckSize);
    assertThat(game.activeSide.hand.size()).isEqualTo(handSize);

    addSpellToHandAndCastIt(ConstSpell.MORTAL_COIL, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.isDead()).isTrue();
    // TODO: doesn't work with current effect/trigger factory.
    //assertThat(game.activeSide.deck.size()).isEqualTo(deckSize - 1);
    //assertThat(game.activeSide.hand.size()).isEqualTo(handSize + 1);
  }

  @Test
  public void testExecute() {
    addSpellToHandAndCastIt(ConstSpell.EXECUTE, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(game.activeSide.board.contains(yeti)).isTrue();

    yeti.takeDamage(1);
    addSpellToHandAndCastIt(ConstSpell.EXECUTE, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(game.activeSide.board.contains(yeti)).isFalse();
  }

  @Test
  public void testCleave() {
    game.switchTurn();
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    addCardToHandAndPlayItOnOwnBoard(ooze);
    final Minion grizzly = createAndBindMinion(ConstMinion.IRONFUR_GRIZZLY);
    addCardToHandAndPlayItOnOwnBoard(grizzly);
    final Minion dalaranMage = createAndBindMinion(ConstMinion.DALARAN_MAGE);
    addCardToHandAndPlayItOnOwnBoard(dalaranMage);
    assertThat(game.activeSide.board.size()).isEqualTo(3);
    game.switchTurn();

    addSpellToHandAndCastIt(ConstSpell.CLEAVE);

    final int damagedMinionCount = ((ooze.healthLoss() > 0) ? 1 : 0) +
        ((grizzly.healthLoss() > 0) ? 1 : 0) +
        ((dalaranMage.healthLoss() > 0) ? 1: 0);
    assertThat(damagedMinionCount).isEqualTo(2);
  }
}
