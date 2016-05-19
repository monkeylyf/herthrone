package com.herthrone.configuration;

import com.herthrone.constant.ConstTarget;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;

import java.util.Map;

/**
 * Created by yifengliu on 5/18/16.
 */
public class TargetConfig {

  private final ConstTarget target;
  private final ConstType type;

  public TargetConfig(Map map) {
    this.target = ConstTarget.valueOf(Constant.upperCaseValue(map, "scope"));
    this.type = ConstType.valueOf(Constant.upperCaseValue(map, "type"));
  }
}
