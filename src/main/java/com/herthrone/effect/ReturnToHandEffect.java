package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.factory.MinionFactory;
import com.herthrone.game.Side;

public class ReturnToHandEffect implements Effect {

  private final Minion minion;

  public ReturnToHandEffect(final Minion minion) {
    this.minion = minion;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.RETURN_TO_HAND;
  }

  @Override
  public void act() {
    final Side side = minion.binder().getSide();
    side.board.remove(minion);
    // TODO: returning by removing the minion from board and creating a same new minion
    side.hand.add(MinionFactory.create(minion.minionConstName()));
  }
}
