package com.herthrone.card.action;

import com.herthrone.card.factory.Action;
import com.herthrone.base.Status;

/**
 * Created by yifeng on 4/15/16.
 */
public class StatusEffect implements Action {

  private final Status status;
  private final double roundUntilExpire;

  public StatusEffect(final Status status, final double roundUntilExpire) {
    this.status = status;
    this.roundUntilExpire = roundUntilExpire;
  }

  @Override
  public void act() {
    this.status.on(this.roundUntilExpire);
  }
}
