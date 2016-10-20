package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.game.Side;

public class MoveCardEffect implements Effect {

  private final Side side;
  private final int numberOfCards;

  public MoveCardEffect(final Side side, final int numberOfCards) {
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
      side.drawCard();
    }
  }
}
