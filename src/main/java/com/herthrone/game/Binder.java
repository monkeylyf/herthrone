package com.herthrone.game;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * Created by yifengliu on 6/22/16.
 */
public class Binder {

  private Optional<Side> sideOptional;
  private Optional<Battlefield> battlefieldOptional;

  public Binder() {
    sideOptional = Optional.absent();
    battlefieldOptional = Optional.absent();
  }

  public static void bind(final Binder binder, final Side side) {
    binder.bind(side);
  }

  public void bind(final Side side) {
    sideOptional = Optional.of(side);
  }

  public static void bind(final Binder binder, final Battlefield battlefield) {
    binder.bind(battlefield);
  }

  public void bind(final Battlefield battlefield) {
    battlefieldOptional = Optional.of(battlefield);
  }

  public Side getSide() {
    Preconditions.checkArgument(sideOptional.isPresent(), "Side not bound yet");
    return sideOptional.get();
  }

  public Battlefield getBattlefield() {
    Preconditions.checkArgument(battlefieldOptional.isPresent(), "Side not bound yet");
    return battlefieldOptional.get();
  }
}
