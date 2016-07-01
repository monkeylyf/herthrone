package com.herthrone.object;

import com.google.common.base.Optional;
import com.herthrone.base.Reset;
import com.herthrone.base.Round;

/**
 * Created by yifeng on 4/14/16.
 */
public class BooleanAttribute implements Round, Reset {

  private boolean on;
  private double roundUntilExpire;

  public BooleanAttribute() {
    this(Double.POSITIVE_INFINITY);
  }

  public BooleanAttribute(final double roundUntilExpire) {
    this.on = true;
    this.roundUntilExpire = roundUntilExpire;
  }

  public static boolean isAbsentOrOff(Optional<BooleanAttribute> booleanAttributeOptional) {
    return !isPresentAndOn(booleanAttributeOptional);
  }

  public static boolean isPresentAndOn(Optional<BooleanAttribute> booleanAttributeOptional) {
    return booleanAttributeOptional.isPresent() && booleanAttributeOptional.get().isOn();
  }

  public boolean isOn() {
    return on;
  }

  public void on(final double roundUntilExpire) {
    this.on = true;
    this.roundUntilExpire = roundUntilExpire;
  }

  @Override
  public void endTurn() {
    roundUntilExpire -= 1;
    if (roundUntilExpire == 0) {
      reset();
    }
  }

  @Override
  public void startTurn() {

  }

  @Override
  public void reset() {
    on = false;
    roundUntilExpire = 0.0;
  }

  @Override
  public String toString() {
    return String.valueOf(on);
  }
}
