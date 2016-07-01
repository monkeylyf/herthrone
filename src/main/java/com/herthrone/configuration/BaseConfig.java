package com.herthrone.configuration;

import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstType;

/**
 * Created by yifengliu on 6/30/16.
 */
public abstract class BaseConfig<E extends Enum<E>> {

  public abstract E getName();

  public abstract String getDisplayName();

  public abstract ConstClass getClassName();

  public abstract ConstType getType();

  public abstract int getCrystal();
}
