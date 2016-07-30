package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.game.Side;


/**
 * Created by yifeng on 4/13/16.
 */
public class SummonEffect implements Effect {

  private final Side side;
  private final Minion minion;

  public SummonEffect(final Side side, final Minion minion) {
    this.side = side;
    this.minion = minion;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.SUMMON;
  }

  @Override
  public void act() {
    side.setSequenceId(minion);
    side.board.add(minion);
  }
}
