package com.herthrone.base;

import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstType;

/**
 * Created by yifeng on 4/19/16.
 */
public interface Config<T extends Enum<T>> {

  T getName();

  ConstClass getClassName();

  ConstType getType();

  int getCrystal();
}
