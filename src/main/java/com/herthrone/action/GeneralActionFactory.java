package com.herthrone.action;

import com.herthrone.base.Player;

import java.util.List;

/**
 * Created by yifeng on 4/6/16.
 */
public interface GeneralActionFactory {

  public List<Action> yieldAction(Player player);
}
