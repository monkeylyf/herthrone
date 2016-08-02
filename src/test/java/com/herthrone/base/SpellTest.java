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
import com.herthrone.game.GameManager;
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

  private Hero hero1;
  private Hero hero2;
  private Minion yeti;
  private int initBoardSize;
  private GameManager gm;
  private static final int DECK_SIZE = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));

  @Before
  public void setUp() {
    final List<Enum> cards = Collections.nCopies(DECK_SIZE, ConstMinion.CHILLWIND_YETI);
    this.gm = new GameManager(ConstHero.GULDAN, ConstHero.GULDAN, cards, cards);
    this.hero1 = gm.activeSide.hero;
    this.hero2 = gm.inactiveSide.hero;

    gm.startTurn();
    this.yeti = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(yeti);
    gm.playCard(yeti);
    initBoardSize = gm.activeSide.board.size();
  }

  private Spell createSpellAndBind(final ConstSpell spellName) {
    final Spell spell = SpellFactory.create(spellName);
    gm.activeSide.bind(spell);
    return spell;
  }

  private Minion createAndBindMinion(final ConstMinion minionName) {
    final Minion minion = MinionFactory.create(minionName);
    gm.activeSide.bind(minion);
    return minion;
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

  private Spell createHeroPowerAndBind(final ConstSpell spellName) {
    final Spell spell = HeroPowerFactory.create(spellName);
    gm.activeSide.bind(spell);
    return spell;
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
    final int boardSize = gm.activeSide.board.size();
    EffectFactory.pipeEffects(reinforce, hero1);
    assertThat(gm.activeSide.board.size()).isEqualTo(boardSize + 1);

    final Minion minion = gm.activeSide.board.get(boardSize);
    assertThat(minion.cardName()).isEqualTo(ConstMinion.SILVER_HAND_RECRUIT.toString());
  }

  @Test
  public void testTotemicCall() {
    final Spell totemicCall = createHeroPowerAndBind(ConstSpell.TOTEMIC_CALL);
    final int size = 4;
    for (int i = 0; i < size; ++i) {
      EffectFactory.pipeEffects(totemicCall, hero1);
      assertThat(gm.activeSide.board.size()).isEqualTo(initBoardSize + i + 1);
    }

    final int totemCount = gm.activeSide.board.stream()
        .map(Minion::cardName).collect(Collectors.toSet()).size();
    assertThat(totemCount).isEqualTo(initBoardSize + size);
  }

  @Test
  public void testLifeTap() {
    final Spell lifeTap = createHeroPowerAndBind(ConstSpell.LIFE_TAP);
    final int damage = 2;

    final int deckSize = gm.activeSide.deck.size();
    final int handSize = gm.activeSide.hand.size();

    assertThat(hero1.healthLoss()).isEqualTo(0);

    EffectFactory.pipeEffects(lifeTap, hero1);

    assertThat(gm.activeSide.hand.size()).isEqualTo(handSize + 1);
    assertThat(gm.activeSide.deck.size()).isEqualTo(deckSize - 1);
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
    final Minion yeti = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(yeti);
    gm.playCard(yeti);
    final Minion ooze = MinionFactory.create(ConstMinion.ACIDIC_SWAMP_OOZE);
    gm.activeSide.bind(ooze);
    gm.playCard(ooze);
    gm.switchTurn();

    gm.playCard(swipe, yeti);
    assertThat(yeti.healthLoss()).isEqualTo(4);
    assertThat(ooze.healthLoss()).isEqualTo(1);
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
    final Minion yeti3 = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(yeti3);
    gm.playCard(yeti3);
    gm.switchTurn();

    gm.playCard(multiShot);
    assertThat(gm.inactiveSide.board.stream().filter(m -> m.healthLoss() == damage).count())
        .isEqualTo(2);
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

  @Test
  public void testKillCommand() {
    final int damage = 3;
    final int damageBoosted = 5;
    // Test without beast on board.
    gm.playCard(createSpellAndBind(ConstSpell.KILL_COMMAND), yeti);
    assertThat(yeti.healthLoss()).isEqualTo(damage);

    final Minion boar = MinionFactory.create(ConstMinion.BOAR);
    gm.activeSide.bind(boar);
    gm.playCard(boar);

    // Test with beast on board.
    yeti.health().increase(damage);
    gm.playCard(createSpellAndBind(ConstSpell.KILL_COMMAND), yeti);
    assertThat(yeti.healthLoss()).isEqualTo(damageBoosted);
  }

  @Test
  public void testPolymorph() {
    final Spell polymorph = createSpellAndBind(ConstSpell.POLYMORPH);

    final int position = gm.activeSide.board.indexOf(yeti);
    assertThat(gm.activeSide.board.get(position)).isEqualTo(yeti);
    gm.playCard(polymorph, yeti);
    assertThat(gm.activeSide.board.get(position)).isNotEqualTo(yeti);
    assertThat(gm.activeSide.board.get(position).cardName()).isEqualTo(ConstMinion.SHEEP.toString());
  }

  @Test
  public void testFrostNova() {
    final Spell frostNova = createSpellAndBind(ConstSpell.FROST_NOVA);

    gm.switchTurn();
    final Minion yeti1 = createAndBindMinion(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(yeti1);
    gm.playCard(yeti1);
    final Minion yeti2 = createAndBindMinion(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(yeti2);
    gm.playCard(yeti2);
    gm.switchTurn();

    assertThat(yeti1.booleanMechanics().isOff(ConstMechanic.FROZEN)).isTrue();
    assertThat(yeti2.booleanMechanics().isOff(ConstMechanic.FROZEN)).isTrue();
    gm.playCard(frostNova);
    assertThat(yeti1.booleanMechanics().isOn(ConstMechanic.FROZEN)).isTrue();
    assertThat(yeti2.booleanMechanics().isOn(ConstMechanic.FROZEN)).isTrue();

    yeti1.startTurn();
    yeti2.startTurn();
    assertThat(yeti1.booleanMechanics().isOff(ConstMechanic.FROZEN)).isTrue();
    assertThat(yeti2.booleanMechanics().isOff(ConstMechanic.FROZEN)).isTrue();
  }

  @Test
  public void testFrostbolt() {
    final Spell frostbolt = createSpellAndBind(ConstSpell.FROSTBOLT);
    assertThat(yeti.booleanMechanics().isOff(ConstMechanic.FROZEN)).isTrue();
    gm.playCard(frostbolt, yeti);
    assertThat(yeti.healthLoss()).isEqualTo(3);
    assertThat(yeti.booleanMechanics().isOn(ConstMechanic.FROZEN)).isTrue();
  }

  @Test
  public void testMirrorImage() {
    final Spell mirrorImage = createSpellAndBind(ConstSpell.MIRROR_IMAGE);
    gm.playCard(mirrorImage);
    assertThat(gm.activeSide.board.size()).isEqualTo(initBoardSize + 2);
    assertThat(gm.activeSide.board.get(initBoardSize + 1).cardName()).isEqualTo(
        ConstMinion.MIRROR_IMAGE_MINION.toString());
    assertThat(gm.activeSide.board.get(initBoardSize).cardName()).isEqualTo(
        ConstMinion.MIRROR_IMAGE_MINION.toString());
  }

  @Test
  public void testDealDamageAndDrawCardMixture() {
    final Spell hammerOfWrath = createSpellAndBind(ConstSpell.HAMMER_OF_WRATH);
    final int deckSize = gm.activeSide.deck.size();
    final int handSize = gm.activeSide.hand.size();

    gm.playCard(hammerOfWrath, yeti);
    assertThat(yeti.healthLoss()).isEqualTo(3);
    assertThat(gm.activeSide.deck.size()).isEqualTo(deckSize - 1);
    assertThat(gm.activeSide.hand.size()).isEqualTo(handSize + 1);
  }

  @Test
  public void testHumility() {
    final Spell humility = createSpellAndBind(ConstSpell.HUMILITY);
    gm.playCard(humility, yeti);
    assertThat(yeti.attack().value()).isEqualTo(1);
  }

  @Test
  public void testPowerWordShield() {
    final int deckSize = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    for (int i = 0; i < deckSize; ++i) {
      gm.activeSide.deck.add(MinionFactory.create(ConstMinion.CHILLWIND_YETI));
    }
    final int handSize = gm.activeSide.hand.size();
    gm.switchTurn();
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    gm.playCard(ooze);
    gm.switchTurn();

    final Spell powerWordShield = createSpellAndBind(ConstSpell.POWER_WORD_SHIELD);
    gm.playCard(powerWordShield, ooze);

    assertThat(gm.activeSide.deck.size()).isEqualTo(deckSize - 1);
    assertThat(gm.activeSide.hand.size()).isEqualTo(handSize + 1);
    assertThat(ooze.health().value()).isEqualTo(4);
  }

  @Test
  public void testDivineSpirit() {
    final Spell divineSpirit = createSpellAndBind(ConstSpell.DIVINE_SPIRIT);
    final int health = yeti.health().value();
    gm.playCard(divineSpirit, yeti);

    assertThat(yeti.health().value()).isEqualTo(2 * health);
  }

  @Test
  public void testHolyNova() {
    final Spell holyNova = createSpellAndBind(ConstSpell.HOLY_NOVA);
    yeti.takeDamage(2);
    gm.activeSide.hero.takeDamage(2);

    gm.switchTurn();
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    gm.playCard(ooze);
    gm.switchTurn();

    gm.playCard(holyNova);

    // Test own side all healed by 2.
    assertThat(yeti.healthLoss()).isEqualTo(0);
    assertThat(gm.activeSide.hero.healthLoss()).isEqualTo(0);
    // Test opponent side all damage by 2.
    assertThat(ooze.healthLoss()).isEqualTo(2);
    assertThat(gm.inactiveSide.hero.healthLoss()).isEqualTo(2);
  }

  @Test
  public void testMindControl() {
    final Spell mindControl = createSpellAndBind(ConstSpell.MIND_CONTROL);

    gm.switchTurn();
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    gm.playCard(ooze);
    gm.switchTurn();

    gm.playCard(mindControl, ooze);

    assertThat(gm.activeSide.board.get(gm.activeSide.board.size() - 1)).isEqualTo(ooze);
    assertThat(gm.inactiveSide.board.contains(ooze)).isFalse();
  }

  @Test
  public void testMindVision() {
    final Spell mindVision = createSpellAndBind(ConstSpell.MIND_VISION);

    gm.switchTurn();
    gm.activeSide.hand.add(createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE));
    gm.activeSide.hand.add(createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE));
    gm.switchTurn();

    final int handSize = gm.activeSide.hand.size();
    gm.playCard(mindVision);
    assertThat(gm.activeSide.hand.size()).isEqualTo(handSize + 1);
    assertThat(gm.activeSide.hand.get(gm.activeSide.hand.size() - 1).cardName())
        .isEqualTo(ConstMinion.ACIDIC_SWAMP_OOZE.toString());
  }

  @Test
  public void testShadowWord() {
    final Spell shadowWordPain = createSpellAndBind(ConstSpell.SHADOW_WORD_PAIN);
    // In reality, shadow word: pain cannot be used to target at yeti but here
    // it's just testing when it's targeted, no effect happens.
    gm.playCard(shadowWordPain, yeti);
    assertThat(gm.activeSide.board.contains(yeti)).isTrue();
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    gm.playCard(ooze);
    // Test that ooze can be targeted by shadow word: pain.
    assertThat(gm.activeSide.board.contains(ooze)).isTrue();
    gm.playCard(shadowWordPain, ooze);
    assertThat(gm.activeSide.board.contains(ooze)).isFalse();

    final Spell shadowWordDeath = createSpellAndBind(ConstSpell.SHADOW_WORD_DEATH);
    yeti.attack().getPermanentBuff().increase(1);
    assertThat(yeti.attack().value()).isEqualTo(5);
    gm.playCard(shadowWordDeath, yeti);
    assertThat(gm.activeSide.board.contains(yeti)).isFalse();
  }

  @Test
  public void testBackStab() {
    final Spell backStab = createSpellAndBind(ConstSpell.BACKSTAB);

    gm.playCard(backStab, yeti);
    assertThat(yeti.healthLoss()).isEqualTo(2);
    // Should not be targetable. Just test no effect happens.
    gm.playCard(backStab, yeti);
    assertThat(yeti.healthLoss()).isEqualTo(2);
  }

  @Test
  public void testDeadlyPoison() {
    final Spell deadlyPoison = createSpellAndBind(ConstSpell.DEADLY_POISON);
    final Weapon assasinsBlade = WeaponFactory.create(ConstWeapon.ASSASSINS_BLADE);
    final int attack = assasinsBlade.getAttackAttr().value();
    hero1.equip(assasinsBlade);
    gm.playCard(deadlyPoison);
    assertThat(assasinsBlade.getAttackAttr().value()).isEqualTo(attack + 2);
  }

  @Test
  public void testSprint() {
    final int deckSize = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    for (int i = 0; i < deckSize; ++i) {
      gm.activeSide.deck.add(MinionFactory.create(ConstMinion.CHILLWIND_YETI));
    }

    final int handSize = gm.activeSide.hand.size();
    final Spell sprint = createSpellAndBind(ConstSpell.SPRINT);
    gm.playCard(sprint);

    assertThat(gm.activeSide.hand.size()).isEqualTo(handSize + 4);
    assertThat(gm.activeSide.deck.size()).isEqualTo(deckSize - 4);
  }

  @Test
  public void testVanish() {
    gm.playCard(createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE));
    gm.switchTurn();
    gm.playCard(createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE));
    gm.playCard(createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE));
    gm.switchTurn();

    final int activeSideHandSize = gm.activeSide.hand.size();
    final int activeSideBoardSize = gm.activeSide.board.size();
    final int inactiveSideHandSize = gm.inactiveSide.hand.size();
    final int inactiveSideBoardSize = gm.inactiveSide.board.size();

    final Spell vanish = createSpellAndBind(ConstSpell.VANISH);
    gm.playCard(vanish);

    assertThat(gm.activeSide.board.size()).isEqualTo(0);
    assertThat(gm.activeSide.hand.size()).isEqualTo(activeSideHandSize + activeSideBoardSize);
    assertThat(gm.inactiveSide.board.size()).isEqualTo(0);
    assertThat(gm.inactiveSide.hand.size()).isEqualTo(inactiveSideHandSize + inactiveSideBoardSize);
  }

  @Test
  public void testAncestralHealing() {
    final Spell ancestralHealing = createSpellAndBind(ConstSpell.ANCESTRAL_HEALING);
    yeti.takeDamage(1);
    assertThat(yeti.booleanMechanics().isOff(ConstMechanic.TAUNT));
    gm.playCard(ancestralHealing, yeti);

    assertThat(yeti.booleanMechanics().isOn(ConstMechanic.TAUNT));
    assertThat(yeti.healthLoss()).isEqualTo(0);

    yeti.takeDamage(4);
    gm.playCard(ancestralHealing, yeti);
    assertThat(yeti.healthLoss()).isEqualTo(0);
  }

  @Test
  public void testRockbiterWeapon() {
    final Spell rockbiterWeapon = createSpellAndBind(ConstSpell.ROCKBITER_WEAPON);
    final int minionAttack = yeti.attack().value();
    gm.playCard(rockbiterWeapon, yeti);
    assertThat(yeti.attack().value()).isEqualTo(minionAttack + 3);
    yeti.endTurn();
    assertThat(yeti.attack().value()).isEqualTo(minionAttack);

    final int heroAttack = hero1.attack().value();
    gm.playCard(rockbiterWeapon, hero1);
    assertThat(hero1.attack().value()).isEqualTo(heroAttack + 3);
    hero1.endTurn();
    assertThat(hero1.attack().value()).isEqualTo(heroAttack);
  }

  @Test
  public void testWindfury() {
    final Spell windfury = createSpellAndBind(ConstSpell.WINDFURY);
    // Test a minion just put on board.
    assertThat(yeti.attackMovePoints().value()).isEqualTo(0);
    gm.playCard(windfury, yeti);
    assertThat(yeti.attackMovePoints().value()).isEqualTo(0);
    yeti.endTurn();
    assertThat(yeti.attackMovePoints().value()).isEqualTo(2);
    // Test a minion in second round.
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    gm.playCard(ooze);
    assertThat(ooze.attackMovePoints().value()).isEqualTo(0);
    ooze.endTurn();
    assertThat(ooze.attackMovePoints().value()).isEqualTo(1);
    gm.playCard(windfury, ooze);
    assertThat(ooze.attackMovePoints().value()).isEqualTo(2);
    // Test a minion in second round that has attacked already.
    final Minion bodyguard = createAndBindMinion(ConstMinion.BOOTY_BAY_BODYGUARD);
    bodyguard.endTurn();
    assertThat(bodyguard.attackMovePoints().value()).isEqualTo(1);
    bodyguard.attackMovePoints().decrease(1);
    assertThat(bodyguard.attackMovePoints().value()).isEqualTo(0);
    gm.playCard(windfury, bodyguard);
    assertThat(bodyguard.attackMovePoints().value()).isEqualTo(1);
  }

  @Test
  public void testTotemicMight() {
    final Minion healingTotem = createAndBindMinion(ConstMinion.HEALING_TOTEM);
    final Minion searingTotem = createAndBindMinion(ConstMinion.SEARING_TOTEM);

    final int healingTotemHealth = healingTotem.health().value();
    final int searingTotemHealth = searingTotem.health().value();
    final int yetiHealth = yeti.health().value();

    final Spell totemicMight = createSpellAndBind(ConstSpell.TOTEMIC_MIGHT);
    gm.playCard(healingTotem);
    gm.playCard(searingTotem);
    gm.playCard(totemicMight);

    assertThat(healingTotem.health().value()).isEqualTo(healingTotemHealth + 2);
    assertThat(searingTotem.health().value()).isEqualTo(searingTotemHealth + 2);
    assertThat(yeti.health().value()).isEqualTo(yetiHealth);
  }

  @Test
  public void testDrainLife() {
    final Spell drainLife = createSpellAndBind(ConstSpell.DRAIN_LIFE);
    gm.activeSide.hero.takeDamage(2);

    gm.playCard(drainLife, yeti);
    assertThat(yeti.healthLoss()).isEqualTo(2);
  }

  @Test
  public void testCorruption() {
    gm.switchTurn();
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    gm.playCard(ooze);
    gm.switchTurn();

    final Spell corruption = createSpellAndBind(ConstSpell.CORRUPTION);
    gm.playCard(corruption, ooze);

    gm.switchTurn();
    assertThat(gm.activeSide.board.contains(ooze)).isTrue();
    gm.switchTurn();
    assertThat(gm.inactiveSide.board.contains(ooze)).isFalse();
  }

  @Test
  public void testMortalCoil() {
    final Spell mortalCoil = createSpellAndBind(ConstSpell.MORTAL_COIL);
    yeti.takeDamage(3);
    assertThat(yeti.health().value()).isEqualTo(2);
    final int deckSize = gm.activeSide.deck.size();
    final int handSize = gm.activeSide.hand.size();
    gm.playCard(mortalCoil, yeti);
    assertThat(yeti.health().value()).isEqualTo(1);
    assertThat(gm.activeSide.deck.size()).isEqualTo(deckSize);
    assertThat(gm.activeSide.hand.size()).isEqualTo(handSize);

    gm.playCard(mortalCoil, yeti);
    assertThat(yeti.isDead()).isTrue();
    // TODO: doesn't work with current effect/trigger factory.
    //assertThat(gm.activeSide.deck.size()).isEqualTo(deckSize - 1);
    //assertThat(gm.activeSide.hand.size()).isEqualTo(handSize + 1);
  }

  @Test
  public void testExecute() {
    final Spell execute = createSpellAndBind(ConstSpell.EXECUTE);
    gm.playCard(execute, yeti);
    assertThat(gm.activeSide.board.contains(yeti)).isTrue();

    yeti.takeDamage(1);
    gm.playCard(execute, yeti);
    assertThat(gm.activeSide.board.contains(yeti)).isFalse();
  }

  @Test
  public void testCleave() {
    gm.switchTurn();
    final Minion ooze = createAndBindMinion(ConstMinion.ACIDIC_SWAMP_OOZE);
    final Minion grizzly = createAndBindMinion(ConstMinion.IRONFUR_GRIZZLY);
    final Minion dalaranMage = createAndBindMinion(ConstMinion.DALARAN_MAGE);
    gm.playCard(ooze);
    gm.playCard(grizzly);
    gm.playCard(dalaranMage);
    assertThat(gm.activeSide.board.size()).isEqualTo(3);
    gm.switchTurn();

    final Spell cleave = createSpellAndBind(ConstSpell.CLEAVE);
    gm.playCard(cleave);
    assertThat(gm.inactiveSide.board.stream().filter(minion -> minion.healthLoss() > 0).count())
        .isEqualTo(2);
  }
}
