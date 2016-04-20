package com.herthrone.base;

/**
 * Created by yifeng on 4/14/16.
 */
public class Side {
  private final Hero hero;
  private final Container<BaseCard> hand;
  private final Container<BaseCard> deck;
  private final Container<Minion> minions;
  private final Container<Secret> secrets;

  public Side(final Hero hero, final Container<BaseCard> hand, final Container<BaseCard> deck, final Container<Minion> minions, final Container<Secret> secrets) {
    this.hero = hero;
    this.hand = hand;
    this.deck = deck;
    this.minions = minions;
    this.secrets = secrets;
  }

  public Hero getHero() { return this.hero; }
  public Container<BaseCard> getHand() { return this.hand; }
  public Container<BaseCard> getDeck() { return this.deck; }
  public Container<Minion> getMinions() { return this.minions; }
  public Container<Secret> getSecrets() { return this.secrets; }
}
