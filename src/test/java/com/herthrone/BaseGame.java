package com.herthrone;

import com.herthrone.base.Card;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSpell;
import com.herthrone.factory.HeroPowerFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.SpellFactory;
import com.herthrone.game.Game;
import com.herthrone.service.BoardSide;
import com.herthrone.service.Command;
import com.herthrone.service.CommandType;
import com.herthrone.service.ContainerType;
import com.herthrone.service.Entity;

import java.util.Collections;
import java.util.List;

public class BaseGame {

  protected Game game;
  protected HeroPowerUtil heroPower;
  protected SpellUtil spell;
  protected MinionUtil minion;
  protected WeaponUtil weapon;
  protected Action action;

  protected static final int DECK_SIZE = Integer.parseInt(
      ConfigLoader.getResource().getString("deck_max_capacity"));
  protected static final int HAND_SIZE = Integer.parseInt(
      ConfigLoader.getResource().getString("hand_max_capacity"));
  protected static final ConstMinion YETI = ConstMinion.CHILLWIND_YETI;
  private static final String GAME_ID = "gameId";
  private static final int HAND_POSITION = 0;
  private static final int BOARD_POSITION = 0;

  protected void setUpGame(final ConstHero heroName1, final ConstHero heroName2) {
    final List<Enum> cards = Collections.nCopies(DECK_SIZE, YETI);
    this.game = new Game(GAME_ID, heroName1, heroName2, cards, cards);

    this.heroPower = new HeroPowerUtil(game);
    this.spell = new SpellUtil(game);
    this.minion = new MinionUtil(game);
    this.weapon = new WeaponUtil(game);
    this.action = new Action(game);
  }

  protected static class WeaponUtil {
    private final Game game;

    protected WeaponUtil(final Game game) {
      this.game = game;
    }
  }

  protected static class Action {
    private final Game game;

    protected Action(final Game game) {
      this.game = game;
    }

    public void attack(final BoardSide attackerSide, final ContainerType attackerContainerType,
                       final int attackerPosition, final BoardSide attackeeSide,
                       final ContainerType attackeeContainerType, final int attackeePosition) {
      final Command attackCommand = Command.newBuilder()
          .setType(CommandType.ATTACK)
          .setDoer(Entity.newBuilder()
              .setSide(attackerSide)
              .setContainerType(attackerContainerType)
              .setPosition(attackerPosition))
          .setTarget(Entity.newBuilder()
              .setSide(attackeeSide)
              .setContainerType(attackeeContainerType)
              .setPosition(attackeePosition))
          .build();
      game.command(attackCommand);
    }
  }

  protected static class SpellUtil {
    private final Game game;

    protected SpellUtil(final Game game) {
      this.game = game;
    }

    public Spell create(final ConstSpell spellName) {
      final Spell spell = SpellFactory.create(spellName);
      game.activeSide.bind(spell);
      return spell;
    }

    public void addToHandAndCast(final ConstSpell spellName, final BoardSide side,
                                 final ContainerType containerType, final int index) {
      final Spell spell = create(spellName);
      addToHandAndCast(spell, side, containerType, index);
    }

    public void addToHandAndCast(final ConstSpell spellName) {
      final Spell spell = create(spellName);
      addToHandAndCast(spell);
    }

    public void addToHandAndCast(final Card card) {
      game.activeSide.hand.add(HAND_POSITION, card);
      final Command playCardCommand = Command.newBuilder()
          .setType(CommandType.PLAY_CARD)
          .setDoer(Entity.newBuilder()
              .setSide(BoardSide.OWN)
              .setContainerType(ContainerType.HAND)
              .setPosition(HAND_POSITION))
          .build();
      game.command(playCardCommand);
    }

    public void addToHandAndCast(final Card card, final BoardSide side,
                                 final ContainerType containerType, final int index) {
      game.activeSide.hand.add(HAND_POSITION, card);
      final Command playCardCommand = Command.newBuilder()
          .setType(CommandType.PLAY_CARD)
          .setDoer(Entity.newBuilder()
              .setSide(BoardSide.OWN)
              .setContainerType(ContainerType.HAND)
              .setPosition(HAND_POSITION))
          .setTarget(Entity.newBuilder()
              .setSide(side)
              .setContainerType(containerType)
              .setPosition(index))
          .build();
      game.command(playCardCommand);
    }
  }

  protected static class MinionUtil {
    private final Game game;

    protected MinionUtil(final Game game) {
      this.game = game;
    }

    public Minion create(final ConstMinion minionName) {
      final Minion minion = MinionFactory.create(minionName);
      game.activeSide.bind(minion);
      return minion;
    }

    public Minion addToHandAndPlay(final ConstMinion minionName, final BoardSide side,
                                   final ContainerType containerType, final int index) {
      final Minion minion = create(minionName);
      game.activeSide.hand.add(HAND_POSITION, minion);
      final Command playCardCommand = Command.newBuilder()
          .setType(CommandType.PLAY_CARD)
          .setDoer(Entity.newBuilder()
              .setSide(BoardSide.OWN)
              .setContainerType(ContainerType.HAND)
              .setPosition(HAND_POSITION))
          .setTarget(Entity.newBuilder()
              .setSide(side)
              .setContainerType(containerType)
              .setPosition(index))
          .build();
      game.command(playCardCommand);
      return minion;
    }

    public Minion addToHandAndPlay(final ConstMinion minionName) {
      final Minion minion = create(minionName);
      addToHandAndPlay(minion);
      return minion;
    }

    public void addToHandAndPlay(final Minion minion) {
      game.activeSide.hand.add(HAND_POSITION, minion);
      final Command playCardCommand = Command.newBuilder()
          .setType(CommandType.PLAY_CARD)
          .setDoer(Entity.newBuilder()
              .setSide(BoardSide.OWN)
              .setContainerType(ContainerType.HAND)
              .setPosition(HAND_POSITION))
          .build();
      game.command(playCardCommand);
    }
  }

  protected static class HeroPowerUtil {
    private final Game game;

    protected HeroPowerUtil(final Game game) {
      this.game = game;
    }

    public Spell create(final ConstSpell heroPowerName) {
      final Spell spell = HeroPowerFactory.create(heroPowerName);
      game.activeSide.bind(spell);
      return spell;
    }

    public void use(final BoardSide side, final ContainerType containerType,
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

    public void use() {
      final Command useHeroPowerCommand = Command.newBuilder()
          .setType(CommandType.USE_HERO_POWER)
          .build();
      game.command(useHeroPowerCommand);
    }

    public void update(final ConstSpell heroPowerName) {
      final Spell heroPower = create(heroPowerName);
      game.activeSide.hero.setHeroPower(heroPower);
    }

  }
}
