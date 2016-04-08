package com.herthrone.action;

import com.herthrone.base.BaseCreature;

import java.util.List;

/**
 * Created by yifeng on 4/6/16.
 */
public interface ActionFactory  {

  public Action yieldAction(List<BaseCreature> creature);
}
