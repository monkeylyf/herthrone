package com.herthrone.game;

import com.google.common.base.Preconditions;
import com.herthrone.base.BaseCard;
import com.herthrone.base.Minion;
import com.herthrone.base.Secret;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.card.factory.Action;
import com.herthrone.card.factory.Factory;
import com.herthrone.card.factory.HeroFactory;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.HeroConfig;
import com.herthrone.constant.ConstHero;
import com.herthrone.stats.IntAttribute;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Created by yifeng on 4/14/16.
 */
public class GameManager {

  public final Factory factory1;
  public final Factory factory2;
  public final Battlefield battlefield1;
  public final Battlefield battlefield2;
  private final Queue<Action> actionQueue;

  private Battlefield activeBattlefield;
  private Factory activeFactory;

  public GameManager(final ConstHero hero1, final ConstHero hero2, final List<String> cardNames1, final List<String> cardNames2) {
    // TODO: need to find a place to init deck given cards in a collection.
    this.battlefield1 = new Battlefield(
            HeroFactory.createHeroByName(hero1),
            HeroFactory.createHeroByName(hero2));
    this.battlefield2 = this.battlefield1.getMirrorBattlefield();
    this.factory1 = new Factory(this.battlefield1);
    this.factory2 = new Factory(this.battlefield2);

    final List<BaseCard> cards1 = generateDeck(cardNames1, this.factory1);
    final List<BaseCard> cards2 = generateDeck(cardNames1, this.factory2);

    final Spell heroPower1 = generateHeroPower(hero1, factory1);
    final Spell heroPower2 = generateHeroPower(hero2, factory2);

    this.battlefield1.mySide.setHeroPower(heroPower1);
    this.battlefield2.mySide.setHeroPower(heroPower2);

    this.battlefield1.mySide.populateDeck(cards1);
    this.battlefield2.mySide.populateDeck(cards2);

    this.actionQueue = new LinkedList<>();
    this.activeBattlefield = this.battlefield1;
    this.activeFactory = this.factory1;
  }

  private static List<BaseCard> generateDeck(final List<String> cardNames, final Factory factory) {
    return cardNames.stream().map(cardName -> factory.createCardInstance(cardName)).collect(Collectors.toList());
  }

  private static Spell generateHeroPower(final ConstHero hero, final Factory factory) {
    final HeroConfig heroConfig = ConfigLoader.getHeroConfigByName(hero);
    return factory.spellFactory.createHeroPowerByName(heroConfig.getHeroPower());
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

  void playCard(final int index) {
    checkManaCost(index);
    final BaseCard card = activeBattlefield.mySide.hand.remove(index);

    if (card instanceof Minion) {
      Minion minion = (Minion) card;
      activeBattlefield.mySide.board.add(minion);
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

  void playCard(final int index, final Minion target) {
    checkManaCost(index);
    final BaseCard card = activeBattlefield.mySide.hand.remove(index);

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

  void drawCard() {
    if (this.activeBattlefield.mySide.deck.isEmpty()) {
      activeBattlefield.mySide.fatigue += 1;
      activeBattlefield.mySide.hero.takeDamage(this.activeBattlefield.mySide.fatigue);
    } else {
      final BaseCard card = this.activeBattlefield.mySide.deck.top();
      activeBattlefield.mySide.hand.add(card);
    }
  }

  void consumeCrystal(final BaseCard card) {
    final int cost = card.getCrystalManaCost().getVal();
    activeBattlefield.mySide.crystal.consume(cost);
  }

  void useHeroPower(final Minion minion) {
    activeFactory.effectFactory.getActionsByConfig(activeBattlefield.mySide.heroPower, minion).stream().forEach(Action::act);
  }

  private void checkManaCost(final int index) {
    final BaseCard card = activeBattlefield.mySide.hand.get(index);
    final int manaCost = card.getCrystalManaCost().getVal();
    Preconditions.checkArgument(
            manaCost <= activeBattlefield.mySide.crystal.getCrystal(),
            "Not enough mana to play " + card.getCardName());
  }

}
