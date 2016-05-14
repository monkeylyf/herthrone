package com.herthrone.configuration;

import com.herthrone.Constant;

/**
 * Created by yifeng on 4/19/16.
 */
public interface BaseConfig <T extends Enum<T>> {

  T getName();

  Constant.Clazz getClassName();

  Constant.Type getType();

  int getCrystal();
}
