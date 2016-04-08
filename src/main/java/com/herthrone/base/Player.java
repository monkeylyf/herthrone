package com.herthrone.base;

import com.herthrone.container.Board;
import com.herthrone.container.Deck;
import com.herthrone.container.Hand;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/2/16.
 */
public class Player {

  public final Board board;
  public final Deck deck;
  public final Hand hand;
  public final BaseCreature hero;
  public final User user;
  public Player opponent = null;

  private int manaCrystal = 0;
  private int availableManaCrystal = 0;
  private static final int maxManaCrystal = 10;

  public Player(final User user, final BaseCreature hero, final List<BaseCard> deck) {
    this.board = new Board();
    this.deck = new Deck(deck);
    this.hand = new Hand();
    this.hero = hero;
    this.user = user;
  }

  public void registerOpponent(Player opponent) {
    this.opponent = opponent;
  }

  public void incrementManaCrystal() {
    if (!isManaCrystalMax()) {
      this.manaCrystal += 1;
    }
  }

  public boolean isManaCrystalMax() {
    return this.manaCrystal == Player.maxManaCrystal;
  }

  public void refreshAvailableManaCrystal() {
    this.availableManaCrystal = this.manaCrystal;
  }

  public void move() {

  }

  public boolean hasMove() {
    return this.hand.stream().anyMatch(card -> card.getCrystalManaCost().getVal() <= this.availableManaCrystal);
  }

  public boolean isDefeated() {
    return false;
  }

  public List<BaseCreature> toHeroList() {
    return Arrays.asList(this.hero);
  }
}
