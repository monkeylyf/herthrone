package com.herthrone.base;

/**
 * Created by yifeng on 4/2/16.
 */
public class Battlefield {

  private final Side mySide;
  private final Side opponentSide;

  public Battlefield(Hero hero1, Hero hero2, Container<BaseCard> hand1, Container<BaseCard> hand2, Container<BaseCard> deck1, Container<BaseCard> deck2, Container<Minion> board1, Container<Minion> board2, Container<Secret> secrets1, Container<Secret> secrets2) {
    this.mySide = new Side(hero1, hand1, deck1, board1, secrets1);
    this.opponentSide = new Side(hero2, hand2, deck2, board2, secrets2);
  }

  public Side getMySide() { return this.mySide; }
  public Side getOpponentSide() { return this.opponentSide; }

  public Battlefield getMirrorBattlefield() {
    return new Battlefield(this.opponentSide.getHero(), this.mySide.getHero(), this.opponentSide.getHand(), this.mySide.getHand(), this.opponentSide.getDeck(), this.mySide.getDeck(), this.opponentSide.getMinions(), this.mySide.getMinions(), this.opponentSide.getSecrets(), this.getMySide().getSecrets());
  }
}


