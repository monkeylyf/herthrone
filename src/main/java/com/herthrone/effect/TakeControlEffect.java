package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.game.Side;

public class TakeControlEffect implements Effect {

  private final Minion minion;

  public TakeControlEffect(final Minion minion) {
    this.minion = minion;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.TAKE_CONTROL;
  }

  @Override
  public void act() {
    final Side currentSide = minion.binder().getSide();
    final Side opponentSide = currentSide.getOpponentSide();
    minion.binder().switchSide();
    currentSide.board.remove(minion);
    minion.summonOnBoard(opponentSide.board, opponentSide.board.size());
  }
}
