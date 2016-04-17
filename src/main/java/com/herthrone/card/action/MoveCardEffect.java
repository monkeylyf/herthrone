package com.herthrone.card.action;

import com.herthrone.action.Action;
import com.herthrone.base.BaseCard;
import com.herthrone.container.Container;

/**
 * Created by yifeng on 4/13/16.
 */
public class MoveCardEffect implements Action {

  private final Container<BaseCard> moveTo;
  private final Container<BaseCard> moveFrom;

  public MoveCardEffect(Container<BaseCard> moveTo, Container<BaseCard> moveFrom) {
    this.moveTo = moveTo;
    this.moveFrom = moveFrom;
  }

  @Override
  public void act() {
    this.moveTo.add(this.moveFrom.top());
  }
}
