package com.herthrone.action;

import com.herthrone.base.BaseCard;
import com.herthrone.factory.Action;
import com.herthrone.game.Container;

/**
 * Created by yifeng on 4/13/16.
 */
public class MoveCardEffect implements Action {

  private final Container<BaseCard> moveTo;
  private final Container<BaseCard> moveFrom;

  public MoveCardEffect(final Container<BaseCard> moveTo, final Container<BaseCard> moveFrom) {
    this.moveTo = moveTo;
    this.moveFrom = moveFrom;
  }

  @Override
  public void act() {
    moveTo.add(moveFrom.top());
  }
}
