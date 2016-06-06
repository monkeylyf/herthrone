package com.herthrone.factory;

import com.herthrone.base.Effect;

import java.util.List;

/**
 * Created by yifeng on 4/6/16.
 */
public interface ActionFactory {

  List<Effect> yieldActions();
}
