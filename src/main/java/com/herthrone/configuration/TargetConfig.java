package com.herthrone.configuration;

import com.google.common.base.Objects;
import com.herthrone.constant.ConstTarget;
import com.herthrone.constant.ConstType;

import java.util.Map;

import static com.herthrone.configuration.ConfigLoader.getUpperCaseStringValue;

/**
 * Created by yifengliu on 5/18/16.
 */
public class TargetConfig {

  private static final String SCOPE = "scope";
  private static final String TYPE = "type";
  public final ConstTarget scope;
  public final ConstType type;

  TargetConfig(Map map) {
    this.scope = ConstTarget.valueOf(getUpperCaseStringValue(map, SCOPE));
    this.type = ConstType.valueOf(getUpperCaseStringValue(map, TYPE));
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add(SCOPE, scope)
        .add(TYPE, type)
        .toString();
  }
}
