package com.herthrone.action;

import com.herthrone.base.Minion;

import java.util.List;

/**
 * Created by yifeng on 4/12/16.
 */
public interface SpellActionFactory {

  public List<SpellAction> yieldSpellAction(List<Minion> creature);
}
