package com.herthrone.game;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class Binder {

  private Optional<Side> sideOptional;

  public Binder() {
    sideOptional = Optional.absent();
  }

  public void bind(final Side side) {
    sideOptional = Optional.of(side);
  }

  public Side getSide() {
    Preconditions.checkArgument(sideOptional.isPresent(), "Side not bound yet");
    return sideOptional.get();
  }

  public Side getOpponentSide() {
    Preconditions.checkArgument(sideOptional.isPresent(), "Side not bound yet");
    return sideOptional.get().getFoeSide();
  }

  public void switchSide() {
    sideOptional = Optional.of(sideOptional.get().getFoeSide());
  }
}
