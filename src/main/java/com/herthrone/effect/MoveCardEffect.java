package com.herthrone.effect;

import com.herthrone.base.Card;
import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.game.Container;
import com.herthrone.game.Side;

/**
 * Created by yifeng on 4/13/16.
 */
public class MoveCardEffect implements Effect {

  private final Container<Card> moveTo;
  private final Container<Card> moveFrom;
  private final Side side;

  public MoveCardEffect(final Container<Card> moveTo, final Container<Card> moveFrom, final Side side) {
    this.moveTo = moveTo;
    this.moveFrom = moveFrom;
    this.side = side;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.MOVE_CARD;
  }

  @Override
  public void act() {
    if (moveFrom.isEmpty()) {
      side.takeFatigueDamage();
    } else {
      moveTo.add(moveFrom.top());
    }
  }
}
