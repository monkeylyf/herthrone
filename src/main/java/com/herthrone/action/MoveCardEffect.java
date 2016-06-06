package com.herthrone.action;

import com.herthrone.base.Card;
import com.herthrone.base.Effect;
import com.herthrone.game.Container;

/**
 * Created by yifeng on 4/13/16.
 */
public class MoveCardEffect implements Effect {

  private final Container<Card> moveTo;
  private final Container<Card> moveFrom;

  public MoveCardEffect(final Container<Card> moveTo, final Container<Card> moveFrom) {
    this.moveTo = moveTo;
    this.moveFrom = moveFrom;
  }

  @Override
  public void act() {
    moveTo.add(moveFrom.top());
  }
}
