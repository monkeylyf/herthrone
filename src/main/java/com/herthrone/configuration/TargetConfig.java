package com.herthrone.configuration;

import com.google.common.base.Objects;
import com.herthrone.constant.ConstTarget;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;

import java.util.Map;

/**
 * Created by yifengliu on 5/18/16.
 */
public class TargetConfig {

  public final ConstTarget scope;
  public final ConstType type;

  public TargetConfig(Map map) {
    this.scope = ConstTarget.valueOf(Constant.upperCaseValue(map, "scope"));
    this.type = ConstType.valueOf(Constant.upperCaseValue(map, "type"));
  }

  public String toString() {
    return Objects.toStringHelper(this)
            .add("scope", scope)
            .add("type", type)
            .toString();
  }
}
