package com.herthrone.base;

import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstType;

/**
 * Created by yifeng on 4/19/16.
 */
public interface Config<E extends Enum<E>> {

  E getName();

  ConstClass getClassName();

  ConstType getType();

  int getCrystal();
}
