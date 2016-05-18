package com.herthrone.game;

import com.herthrone.base.Hero;

/**
 * Created by yifeng on 4/2/16.
 */
public class Battlefield {

  public final Side mySide;
  public final Side opponentSide;

  public Battlefield(final Hero hero1, final Hero hero2) {
    this.mySide = new Side(hero1);
    this.opponentSide = new Side(hero2);
  }

  private Battlefield(final Side mySide, final Side opponentSide) {
    this.mySide = mySide;
    this.opponentSide = opponentSide;
  }

  public Battlefield getMirrorBattlefield() {
    return new Battlefield(this.opponentSide, this.mySide);
  }
}
