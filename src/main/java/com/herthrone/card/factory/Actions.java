package com.herthrone.card.factory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifengliu on 5/17/16.
 */
public class Actions implements Action {

  private final List<Action> actions;

  @Override
  public void act() {
    this.actions.stream().forEach(Action::act);
  }

  public Actions(final List<Action> actions) {
    this.actions = actions;
  }

  public Actions() {
    this(new ArrayList<>());
  }
}
