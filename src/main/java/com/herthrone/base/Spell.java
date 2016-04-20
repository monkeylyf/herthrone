package com.herthrone.base;

import com.herthrone.card.factory.ActionFactory;

import java.util.List;

/**
 * Created by yifeng on 4/4/16.
 */
public interface Spell extends BaseCard {

  public List<ActionFactory> getActionFactories();
}
