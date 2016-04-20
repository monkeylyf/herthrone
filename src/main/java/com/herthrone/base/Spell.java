package com.herthrone.base;

import com.herthrone.action.ActionFactory;
import com.herthrone.action.SpellEffect;

import java.util.List;

/**
 * Created by yifeng on 4/4/16.
 */
public interface Spell extends BaseCard, SpellEffect {

  public List<ActionFactory> getActionFactories();
}
