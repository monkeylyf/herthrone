package com.herthrone.card.action;

import com.herthrone.card.factory.Action;
import com.herthrone.stats.BooleanAttribute;

/**
 * Created by yifeng on 4/15/16.
 */
public class StatusEffect implements Action {

  private final BooleanAttribute booleanAttribute;
  private final double roundUntilExpire;

  public StatusEffect(final BooleanAttribute booleanAttribute, final double roundUntilExpire) {
    this.booleanAttribute = booleanAttribute;
    this.roundUntilExpire = roundUntilExpire;
  }

  @Override
  public void act() {
    booleanAttribute.on(roundUntilExpire);
  }
}
