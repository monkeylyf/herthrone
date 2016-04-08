package com.herthrone.action;

import com.herthrone.base.Player;
import com.herthrone.base.Summon;

/**
 * Created by yifeng on 4/5/16.
 */
public class SummonAction implements Action {

  private final Summon summon;
  private final Player player;

  public SummonAction(Summon summon, final Player player) {
    this.summon = summon;
    this.player = player;
  }

  @Override
  public void act() {
    this.summon.summon(this.player.board);
  }
}
