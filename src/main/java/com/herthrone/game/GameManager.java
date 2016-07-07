package com.herthrone.game;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.herthrone.base.Card;
import com.herthrone.base.Creature;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Round;
import com.herthrone.base.Secret;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.constant.ConstAction;
import com.herthrone.constant.ConstCommand;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSecret;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.HeroFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.SecretFactory;
import com.herthrone.factory.SpellFactory;
import com.herthrone.factory.WeaponFactory;
import com.herthrone.object.BooleanAttribute;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yifeng on 4/14/16.
 */
public class GameManager implements Round {

  private static final Logger logger = Logger.getLogger(GameManager.class.getName());

  private final Battlefield battlefield1;
  private final Battlefield battlefield2;
  public Battlefield activeBattlefield;
  public Side activeSide;
  public Side inactiveSide;
  private int seqId = 0;

  public GameManager(final ConstHero hero1, final ConstHero hero2, final List<Enum> cardNames1, final List<Enum> cardNames2) {
    // TODO: need to find a place to init deck given cards in a collection.
    this.battlefield1 = new Battlefield(HeroFactory.create(hero1), HeroFactory.create(hero2));
    this.battlefield2 = battlefield1.getMirrorBattlefield();
    this.activeBattlefield = battlefield1;
    this.activeSide = battlefield1.mySide;
    this.inactiveSide = battlefield1.opponentSide;

    activeSide.populateDeck(cardNames1);
    inactiveSide.populateDeck(cardNames2);
  }

  public static Card createCardInstance(final String cardName, final ConstType type) {
    switch (type) {
      case MINION:
        return MinionFactory.create(ConstMinion.valueOf(cardName.toUpperCase()));
      case WEAPON:
        return WeaponFactory.create(ConstWeapon.valueOf(cardName.toUpperCase()));
      case SPELL:
        return SpellFactory.create(ConstSpell.valueOf(cardName.toUpperCase()));
      case SECRET:
        return SecretFactory.create(ConstSecret.valueOf(cardName.toUpperCase()));
      default:
        throw new RuntimeException(String.format("Unknown card %s", cardName));
    }
  }

  public static Card createCardInstance(final Enum cardName) {
    final String name = cardName.toString();

    Optional<ConstMinion> constMinion = Enums.getIfPresent(ConstMinion.class, name);
    if (constMinion.isPresent()) {
      return MinionFactory.create(constMinion.get());
    }

    Optional<ConstWeapon> constWeapon = Enums.getIfPresent(ConstWeapon.class, name);
    if (constWeapon.isPresent()) {
      return WeaponFactory.create(constWeapon.get());
    }

    Optional<ConstSpell> constSpell = Enums.getIfPresent(ConstSpell.class, name);
    if (constSpell.isPresent()) {
      return SpellFactory.create(constSpell.get());
    }

    Optional<ConstSecret> constSecret = Enums.getIfPresent(ConstSecret.class, name);
    if (constSecret.isPresent()) {
      return SecretFactory.create(constSecret.get());
    }

    throw new RuntimeException(String.format("Unknown card %s", name));
  }

  public static boolean isMinionTargetable(final Minion minion, final Container<Minion> board, final ConstType type) {
    if (BooleanAttribute.isPresentAndOn(minion.booleanMechanics().get(ConstMechanic.IMMUNE))) {
      return false;
    } else {
      switch (type) {
        case ATTACK:
          return isMinionTargetableByAttack(minion, board);
        case SPELL:
          return isMinionTargetableBySpell(minion, board);
        default:
          throw new RuntimeException(String.format("Unknown type %s for target", type.toString()));
      }
    }
  }

  private static boolean isMinionTargetableByAttack(final Minion minion, final Container<Minion> board) {
    // A stealth minion can not be targeted, even it is a taunt minion.
    final Optional<BooleanAttribute> stealth = minion.booleanMechanics().get(ConstMechanic.STEALTH);
    if (BooleanAttribute.isPresentAndOn(stealth)) {
      return false;
    }

    // A taunt minion is targetable.
    final Optional<BooleanAttribute> taunt = minion.booleanMechanics().get(ConstMechanic.TAUNT);
    if (BooleanAttribute.isPresentAndOn(taunt)) {
      return true;
    }

    // If there is any other minions on the board with taunt but not stealth ability, this minion
    // cannot be targeted.
    return !board.stream().anyMatch(m -> BooleanAttribute.isPresentAndOn(m.booleanMechanics().get(ConstMechanic.TAUNT)) && BooleanAttribute.isAbsentOrOff(m.booleanMechanics().get(ConstMechanic.STEALTH)));
  }

  private static boolean isMinionTargetableBySpell(final Minion minion, final Container<Minion> board) {
    final Optional<BooleanAttribute> elusive = minion.booleanMechanics().get(ConstMechanic.ELUSIVE);
    return !BooleanAttribute.isPresentAndOn(elusive);
  }

  public static boolean isHeroTargetable(final Hero hero, final Container<Minion> board, final ConstType type) {
    if (BooleanAttribute.isPresentAndOn(hero.booleanMechanics().get(ConstMechanic.IMMUNE))) {
      return false;
    } else {
      switch (type) {
        case ATTACK:
          return isHeroTargetableByAttack(hero, board);
        case SPELL:
          return isHeroTargetableBySpell(hero, board);
        default:
          throw new RuntimeException(String.format("Unknown type %s for target", type.toString()));
      }
    }
  }

  private static boolean isHeroTargetableByAttack(final Hero hero, final Container<Minion> board) {
    return !board.stream().anyMatch(m -> BooleanAttribute.isPresentAndOn(m.booleanMechanics().get(ConstMechanic.TAUNT)));
  }

  private static boolean isHeroTargetableBySpell(final Hero hero, final Container<Minion> board) {
    return true;
  }

  public static void main(String[] args) {
    logger.info("Starting game");

    final int deck_size = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    ConstMinion MINION = ConstMinion.CHILLWIND_YETI;
    List<Enum> cards1 = Collections.nCopies(deck_size, MINION);
    List<Enum> cards2 = Collections.nCopies(deck_size, MINION);

    final GameManager gameManager = new GameManager(ConstHero.ANDUIN_WRYNN, ConstHero.JAINA_PROUDMOORE, cards1, cards2);
    gameManager.play();
  }

  public void play() {
    int turn = 1;
    while (!isGameFinished()) {
      logger.debug("Turn #." + turn);
      logger.debug("Round #." + (turn + 1) / 2);
      startTurn();
      playUtilEndTurn();
      CommandLine.println("Your turn is finished.");
      switchTurn();
      turn += 1;
    }
  }

  boolean isGameFinished() {
    return activeSide.hero.isDead() || inactiveSide.hero.isDead();
  }

  @Override
  public void endTurn() {
    EffectFactory.triggerEndTurnMechanics(activeSide);
  }

  @Override
  public void startTurn() {
    increaseCrystalUpperBound();
    activeSide.startTurn();
    drawCard();
    activeSide.board.stream().forEach(minion -> minion.endTurn());
    activeSide.hero.endTurn();
  }

  void playUtilEndTurn() {
    CommandLine.CommandNode leafNode = null;
    do {
      final CommandLine.CommandNode root = CommandLine.yieldCommands(activeBattlefield);
      for (Map.Entry entry : activeBattlefield.view().entrySet()) {
        CommandLine.println(entry.getKey() + " " + entry.getValue());
      }
      leafNode = CommandLine.run(root);
      play(leafNode);
    } while (!isGameFinished() && !isTurnFinished(leafNode));
  }

  public void switchTurn() {
    activeSide.endTurn();
    if (activeBattlefield == battlefield1) {
      activeBattlefield = battlefield2;
    } else {
      activeBattlefield = battlefield1;
    }

    activeSide = activeBattlefield.mySide;
    inactiveSide = activeBattlefield.opponentSide;
    activeSide.startTurn();
  }

  void increaseCrystalUpperBound() {
    activeSide.manaCrystal.endTurn();
  }

  void drawCard() {
    if (activeSide.deck.isEmpty()) {
      activeSide.takeFatigueDamage();
    } else {
      final Card card = activeSide.deck.top();
      activeSide.hand.add(card);
    }
  }

  void play(final CommandLine.CommandNode leafNode) {
    if (leafNode.option.equals(ConstCommand.END_TURN.toString())) {
      return;
    }

    if (leafNode.option.equals(ConstCommand.USE_HERO_POWER.toString())) {
      // Use hero power without a specific target.
      EffectFactory.pipeEffectsByConfig(activeSide.hero.getHeroPower(), activeSide.hero);
      consumeCrystal(activeSide.hero.getHeroPower());
      activeSide.hero.attackMovePoints().getTemporaryBuff().increase(-1);
    } else if (leafNode.getParentType().equals(ConstCommand.USE_HERO_POWER.toString())) {
      // Use hero power with a specific target.
      final Creature creature = CommandLine.toTargetCreature(activeBattlefield, leafNode);
      EffectFactory.pipeEffectsByConfig(activeSide.hero.getHeroPower(), creature);
      consumeCrystal(activeSide.hero.getHeroPower());
      activeSide.hero.attackMovePoints().getTemporaryBuff().increase(-1);
    } else if (leafNode.getParentType().equals(ConstCommand.PLAY_CARD.toString())) {
      final Card card = activeSide.hand.get(leafNode.index);
      playCard(leafNode.index);
      consumeCrystal(card);
    } else if (leafNode.getParent().getParentType().equals(ConstCommand.MINION_ATTACK.toString())) {
      final Creature attacker = CommandLine.toTargetCreature(activeBattlefield, leafNode.getParent());
      final Creature attackee = CommandLine.toTargetCreature(activeBattlefield, leafNode);
      EffectFactory.AttackFactory.getPhysicalDamageAction(attacker, attackee);
      // Cost one move point.
      attacker.attackMovePoints().getTemporaryBuff().increase(-1);
    } else {
      throw new RuntimeException("Unknown option: " + leafNode.option.toString());
    }
  }

  boolean isTurnFinished(final CommandLine.CommandNode node) {
    return node == null || node.option.equals(ConstCommand.END_TURN.toString());
  }

  void consumeCrystal(final Card card) {
    final int cost = card.manaCost().value();
    activeSide.manaCrystal.consume(cost);
  }

  void playCard(final int index) {
    checkManaCost(index);
    final Card card = activeSide.hand.remove(index);
    playCard(card);
  }

  private void checkManaCost(final int index) {
    final Card card = activeSide.hand.get(index);
    final int manaCost = card.manaCost().value();
    Preconditions.checkArgument(manaCost <= activeSide.manaCrystal.getCrystal(), "Not enough mana for: " + card.cardName());
  }

  public void playCard(final Card card) {
    //card.binder().bind(activeSide);
    if (card instanceof Minion) {
      final Minion minion = (Minion) card;
      activeSide.replay.add(null, -1, ConstAction.PLAY_CARD, minion.cardName());
      // Assign game board sequence id to minion.
      setIncrementalSequenceId(minion);
      minion.playOnBoard(activeSide.board);
    } else if (card instanceof Secret) {
      activeSide.secrets.add((Secret) card);
    } else if (card instanceof Weapon) {
      activeSide.hero.equip((Weapon) card);
    } else if (card instanceof Spell) {
      //spell.getEffects().
    } else {

    }
  }

  public void playCard(final Card card, final Creature target) {
    //card.binder().bind(activeSide);
    if (card instanceof Minion) {
      final Minion minion = (Minion) card;
      activeSide.replay.add(null, -1, ConstAction.PLAY_CARD, minion.cardName());
      // Assign game board sequence id to minion.
      setIncrementalSequenceId(minion);
      minion.playOnBoard(activeSide.board, target);
    } else if (card instanceof Spell) {
      final Spell spell = (Spell) card;
      EffectFactory.pipeEffectsByConfig(spell, target);
    }
  }

  public void setIncrementalSequenceId(final Minion minion) {
    minion.setSequenceId(seqId);
    seqId += 1;
  }

  void playCard(final int index, final Minion target) {
    checkManaCost(index);
    final Card card = activeSide.hand.remove(index);

    if (card instanceof Minion) {
      Minion minion = (Minion) card;
      activeSide.board.add(minion);
    } else if (card instanceof Weapon) {
      Weapon weapon = (Weapon) card;
      activeSide.hero.equip(weapon);
    } else if (card instanceof Spell) {
      Spell spell = (Spell) card;
      //spell.getEffects().
    } else {

    }
  }

  public void useHeroPower(final Creature creature) {
    activeSide.hero.useHeroPower(creature);
  }
}
