package com.herthrone.game;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.herthrone.base.Card;
import com.herthrone.base.Creature;
import com.herthrone.base.Effect;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Secret;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.HeroConfig;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.constant.ConstCommand;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstType;
import com.herthrone.factory.Factory;
import com.herthrone.factory.HeroFactory;
import com.herthrone.stats.BooleanAttribute;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yifeng on 4/14/16.
 */
public class GameManager {

  static Logger logger = Logger.getLogger(GameManager.class.getName());

  public final Factory factory1;
  public final Factory factory2;
  public final Battlefield battlefield1;
  public final Battlefield battlefield2;
  Battlefield activeBattlefield;
  private int seqId = 0;
  private Factory activeFactory;

  public GameManager(final ConstHero hero1, final ConstHero hero2, final List<Enum> cardNames1,
                     final List<Enum> cardNames2) {
    // TODO: need to find a place to init deck given cards in a collection.
    this.battlefield1 = new Battlefield(
        HeroFactory.createHeroByName(hero1),
        HeroFactory.createHeroByName(hero2));
    this.battlefield2 = battlefield1.getMirrorBattlefield();
    this.factory1 = new Factory(battlefield1);
    this.factory2 = new Factory(battlefield2);
    this.activeBattlefield = battlefield1;
    this.activeFactory = factory1;

    final List<Card> cards1 = generateDeck(cardNames1, factory1);
    final List<Card> cards2 = generateDeck(cardNames1, factory2);

    final Spell heroPower1 = generateHeroPower(hero1, factory1);
    final Spell heroPower2 = generateHeroPower(hero2, factory2);

    battlefield1.mySide.setHeroPower(heroPower1);
    battlefield2.mySide.setHeroPower(heroPower2);

    battlefield1.mySide.populateDeck(cards1);
    battlefield2.mySide.populateDeck(cards2);

  }

  private static List<Card> generateDeck(final List<Enum> cardNames, final Factory factory) {
    return cardNames.stream().map(cardName -> factory.createCardInstance(cardName)).collect(Collectors.toList());
  }

  private static Spell generateHeroPower(final ConstHero hero, final Factory factory) {
    final HeroConfig heroConfig = ConfigLoader.getHeroConfigByName(hero);
    return factory.spellFactory.createHeroPowerByName(heroConfig.getHeroPower());
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
    return activeBattlefield.mySide.hero.isDead() || activeBattlefield.opponentSide.hero.isDead();
  }

  void startTurn() {
    increaseCrystalUpperBound();
    drawCard();
    activeBattlefield.mySide.board.stream().forEach(minion -> minion.endTurn());
    activeBattlefield.mySide.hero.endTurn();
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
      clearBoard();
    } while (!isGameFinished() && !isTurnFinished(leafNode));
  }

  void switchTurn() {
    if (activeBattlefield == battlefield1) {
      activeBattlefield = battlefield2;
      activeFactory = factory2;
    } else {
      activeBattlefield = battlefield1;
      activeFactory = factory1;
    }
  }

  void increaseCrystalUpperBound() {
    activeBattlefield.mySide.manaCrystal.endTurn();
  }

  void drawCard() {
    if (activeBattlefield.mySide.deck.isEmpty()) {
      activeBattlefield.mySide.takeFatigueDamage();
    } else {
      final Card card = activeBattlefield.mySide.deck.top();
      activeBattlefield.mySide.hand.add(card);
    }
  }

  void play(final CommandLine.CommandNode leafNode) {
    if (leafNode.option.equals(ConstCommand.END_TURN.toString())) {
      return;
    }

    if (leafNode.option.equals(ConstCommand.USE_HERO_POWER.toString())) {
      // Use hero power without a specific target.
      activeFactory.effectFactory.getActionsByConfig(activeBattlefield.mySide.heroPower, activeBattlefield.mySide.hero).stream().forEach(Effect::act);
      consumeCrystal(activeBattlefield.mySide.heroPower);
      activeBattlefield.mySide.hero.getAttackMovePoints().buff.temp.decrease(1);
    } else if (leafNode.getParentType().equals(ConstCommand.USE_HERO_POWER.toString())) {
      // Use hero power with a specific target.
      final Creature creature = CommandLine.toTargetCreature(activeBattlefield, leafNode);
      activeFactory.effectFactory.getActionsByConfig(activeBattlefield.mySide.heroPower, creature).stream().forEach(Effect::act);
      consumeCrystal(activeBattlefield.mySide.heroPower);
      activeBattlefield.mySide.hero.getAttackMovePoints().buff.temp.decrease(1);
    } else if (leafNode.getParentType().equals(ConstCommand.PLAY_CARD.toString())) {
      final Card card = activeBattlefield.mySide.hand.get(leafNode.index);
      playCard(leafNode.index);
      consumeCrystal(card);
    } else if (leafNode.getParent().getParentType().equals(ConstCommand.MOVE_MINION.toString())) {
      final Creature attacker = CommandLine.toTargetCreature(activeBattlefield, leafNode.getParent());
      final Creature attackee = CommandLine.toTargetCreature(activeBattlefield, leafNode);
      activeFactory.attackFactory.getPhysicalDamageAction(attacker, attackee);
      // Cost one move point.
      attacker.getAttackMovePoints().buff.temp.decrease(1);
    } else {
      throw new RuntimeException("Unknown option: " + leafNode.option.toString());
    }
  }

  void clearBoard() {
    clearBoard(activeBattlefield.mySide.board);
    clearBoard(activeBattlefield.opponentSide.board);
  }

  boolean isTurnFinished(final CommandLine.CommandNode node) {
    return node == null || node.option.equals(ConstCommand.END_TURN.toString());
  }

  void consumeCrystal(final Card card) {
    final int cost = card.getCrystalManaCost().getVal();
    activeBattlefield.mySide.manaCrystal.consume(cost);
  }

  void playCard(final int index) {
    checkManaCost(index);
    final Card card = activeBattlefield.mySide.hand.remove(index);
    playCard(card);
    activeBattlefield.mySide.incrementPlayedCardCount();
  }

  void clearBoard(final Container<Minion> board) {
    for (int i = 0; i < board.size(); ++i) {
      if (board.get(i).isDead()) {
        board.remove(i);
      }
    }
  }

  private void checkManaCost(final int index) {
    final Card card = activeBattlefield.mySide.hand.get(index);
    final int manaCost = card.getCrystalManaCost().getVal();
    Preconditions.checkArgument(
        manaCost <= activeBattlefield.mySide.manaCrystal.getCrystal(),
        "Not enough mana to play " + card.getCardName());
  }

  public void playCard(final Card card) {
    if (card instanceof Minion) {
      Minion minion = (Minion) card;
      // Assign game board sequence id to minion.
      activeBattlefield.mySide.board.add(minion);
      minion.setSequenceId(seqId);
      seqId += 1;

      Optional<MechanicConfig> battlecry = minion.getEffectMechanics().get(ConstMechanic.BATTLECRY);
      if (battlecry.isPresent()) {
        Effect effect = activeFactory.effectFactory.getEffectByMechanic(battlecry.get(), Optional
            .absent());
        effect.act();
      }
    } else if (card instanceof Secret) {
      Secret secret = (Secret) card;
      activeBattlefield.mySide.secrets.add(secret);
    } else if (card instanceof Weapon) {
      Weapon weapon = (Weapon) card;
      activeBattlefield.mySide.hero.arm(weapon);
    } else if (card instanceof Spell) {
      Spell spell = (Spell) card;
      //spell.getEffects().
    } else {

    }
  }

  public static boolean isMinionTargetable(final Minion minion, final Container<Minion> board,
                                           final ConstType type) {
    if (BooleanAttribute.isPresentAndOn(minion.getBooleanMechanics().get(ConstMechanic.IMMUNE))) {
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

  private static boolean isMinionTargetableByAttack(final Minion minion, final Container<Minion>
      board) {
    // A stealth minion can not be targeted, even it is a taunt minion.
    final Optional<BooleanAttribute> stealth = minion.getBooleanMechanics().get(ConstMechanic
        .STEALTH);
    if (BooleanAttribute.isPresentAndOn(stealth)) {
      return false;
    }

    // A taunt minion is targetable.
    final Optional<BooleanAttribute> taunt = minion.getBooleanMechanics().get(ConstMechanic.TAUNT);
    if (BooleanAttribute.isPresentAndOn(taunt)) {
      return true;
    }

    // If there is any other minion on the board has taunt ability, this minion cannot be targeted.
    return !board.stream().anyMatch(
        m -> BooleanAttribute.isPresentAndOn(m.getBooleanMechanics().get(ConstMechanic.TAUNT)));
  }

  private static boolean isMinionTargetableBySpell(final Minion minion, final Container<Minion>
      board) {
    final Optional<BooleanAttribute> elusive = minion.getBooleanMechanics().get(
        ConstMechanic.ELUSIVE);
    return !BooleanAttribute.isPresentAndOn(elusive);
  }

  public static boolean isHeroTargetable(final Hero hero, final Container<Minion> board,
                                         final ConstType type) {
    if (BooleanAttribute.isPresentAndOn(hero.getBooleanMechanics().get(ConstMechanic.IMMUNE))) {
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
    return !board.stream().anyMatch(
        m -> BooleanAttribute.isPresentAndOn(m.getBooleanMechanics().get(ConstMechanic.TAUNT)));
  }

  private static boolean isHeroTargetableBySpell(final Hero hero, final Container<Minion> board) {
    return true;
  }

  void playCard(final int index, final Minion target) {
    checkManaCost(index);
    final Card card = activeBattlefield.mySide.hand.remove(index);

    if (card instanceof Minion) {
      Minion minion = (Minion) card;
      activeBattlefield.mySide.board.add(minion);
    } else if (card instanceof Weapon) {
      Weapon weapon = (Weapon) card;
      activeBattlefield.mySide.hero.arm(weapon);
    } else if (card instanceof Spell) {
      Spell spell = (Spell) card;
      //spell.getEffects().
    } else {

    }
  }

  void useHeroPower(final Creature creature) {
    final Side side = activeBattlefield.mySide;
    Preconditions.checkArgument(side.heroPowerMovePoints.getVal() > 0, "Cannot use hero power any more in current turn");
    activeFactory.effectFactory.getActionsByConfig(side.heroPower, creature).stream().forEach(Effect::act);
    side.heroPowerMovePoints.decrease(1);
  }

}
