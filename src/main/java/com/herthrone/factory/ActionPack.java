package com.herthrone.factory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifengliu on 5/17/16.
 */
public class ActionPack implements Action {

  private final List<Action> actions;

  public ActionPack(final List<Action> actions) {
    this.actions = actions;
  }

  public ActionPack() {
    this(new ArrayList<>());
  }

  @Override
  public void act() {
    actions.stream().forEach(Action::act);
  }

}
