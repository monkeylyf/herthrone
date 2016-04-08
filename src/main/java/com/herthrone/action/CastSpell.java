package com.herthrone.action;

import com.herthrone.base.BaseCreature;

/**
 * Created by yifeng on 4/6/16.
 */
public interface CastSpell<T extends BaseCreature>  {

  public void cast(T creature);
}
