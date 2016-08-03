package com.herthrone.effect;

import com.herthrone.base.Card;
import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.game.Container;
import com.herthrone.game.Side;

public class MoveCardEffect implements Effect {

  private final Container<Card> moveTo;
  private final Container<Card> moveFrom;
  private final Side side;
  private final int numberOfCards;

  public MoveCardEffect(final Container<Card> moveTo, final Container<Card> moveFrom,
                        final Side side, final int numberOfCards) {
    this.moveTo = moveTo;
    this.moveFrom = moveFrom;
    this.side = side;
    this.numberOfCards = numberOfCards;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.MOVE_CARD;
  }

  @Override
  public void act() {
    for (int i = 0; i < numberOfCards; ++i) {
      if (moveFrom.isEmpty()) {
        side.takeFatigueDamage();
      } else {
        moveTo.add(moveFrom.top());
      }
    }
  }
}
