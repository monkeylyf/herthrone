package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.game.Container;


/**
 * Created by yifeng on 4/13/16.
 */
public class SummonEffect implements Effect {

  private final Container<Minion> board;
  private final Minion minion;

  public SummonEffect(final Container<Minion> board, final Minion minion) {
    this.board = board;
    this.minion = minion;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.SUMMON;
  }

  @Override
  public void act() {
    board.add(minion);
  }
}
