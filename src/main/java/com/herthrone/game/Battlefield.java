package com.herthrone.game;

import com.herthrone.base.BaseCard;
import com.herthrone.constant.ConstHero;

/**
 * Created by yifeng on 4/2/16.
 */
public class Battlefield {

  public final Side mySide;
  public final Side opponentSide;

  public Battlefield(final ConstHero hero1, final ConstHero hero2, final Container<BaseCard> deck1, final Container<BaseCard> deck2) {
    this.mySide = new Side(hero1, deck1);
    this.opponentSide = new Side(hero2, deck2);
  }

  private Battlefield(final Side mySide, final Side opponentSide) {
    this.mySide = mySide;
    this.opponentSide = opponentSide;
  }

  public Battlefield getMirrorBattlefield() {
    return new Battlefield(this.opponentSide, this.mySide);
  }
}
