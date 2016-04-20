package com.herthrone;

import com.herthrone.base.BaseCard;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Secret;
import com.herthrone.base.Battlefield;
import com.herthrone.card.factory.HeroFactory;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.base.Container;
import com.herthrone.exception.CardNotFoundException;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class GameManager {

  private final Hero hero1;
  private final Hero hero2;
  private final Container<BaseCard> hand1;
  private final Container<BaseCard> hand2;
  private final Container<BaseCard> deck1;
  private final Container<BaseCard> deck2;
  private final Container<Minion> board1;
  private final Container<Minion> board2;
  private final Container<Secret> secrets1;
  private final Container<Secret> secrets2;
  private final Battlefield battlefield1;
  private final Battlefield battlefield2;

  public GameManager(final String hero1, final String hero2, final List<String> cardList1, final List<String> cardList2) throws CardNotFoundException, FileNotFoundException {
    final int handCapacity = Integer.parseInt(ConfigLoader.getResource().getString("hand_max_capacity"));
    final int deckCapacity = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    final int boardCapacity = Integer.parseInt(ConfigLoader.getResource().getString("board_max_capacity"));

    this.hero1 = HeroFactory.createHeroByName(hero1);
    this.hero2 = HeroFactory.createHeroByName(hero2);
    this.hand1 = new Container<>(handCapacity);
    this.hand2 = new Container<>(handCapacity);
    this.deck1 = new Container<>(deckCapacity);
    this.deck2 = new Container<>(deckCapacity);
    this.board1 = new Container<>(boardCapacity);
    this.board2 = new Container<>(boardCapacity);
    this.secrets1 = new Container<>();
    this.secrets2 = new Container<>();

    this.battlefield1 = new Battlefield(this.hero1, this.hero2, this.hand1, this.hand2, this.deck1, this.deck2, this.board1, this.board2, this.secrets1, this.secrets2);
    this.battlefield2 = this.battlefield1.getMirrorBattlefield();
  }

  public Hero getHero1() {
    return this.hero1;
  }

  public Hero getHero2() {
    return this.hero2;
  }

  public Container<BaseCard> getHand1() {
    return this.hand1;
  }

  public Container<BaseCard> getHand2() {
    return this.hand2;
  }

  public Container<BaseCard> getDeck1() {
    return this.deck1;
  }

  public Container<BaseCard> getDeck2() {
    return this.deck2;
  }

  public Container<Minion> getBoard1() {
    return this.board1;
  }

  public Container<Minion> getBoard2() {
    return this.board2;
  }

  public Container<Secret> getSecrets1() {
    return this.secrets1;
  }

  public Container<Secret> getSecrets2() {
    return this.secrets2;
  }

  public Battlefield getBattlefield1() {
    return this.battlefield1;
  }

  public Battlefield getBattlefield2() {
    return this.battlefield2;
  }
}
