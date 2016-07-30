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
  private static final String RANDOM = "random";
  private static final String ADJACENT = "adjacent";
  private static final String SELF_EXCLUDED = "self_excluded";
  public final ConstTarget scope;
  public final ConstType type;
  public final boolean isRandom;
  public final boolean isAdjacent;
  public final boolean isSelfExcluded;

  TargetConfig(final Map map) {
    this.scope = ConstTarget.valueOf(getUpperCaseStringValue(map, SCOPE));
    this.type = ConstType.valueOf(getUpperCaseStringValue(map, TYPE));
    this.isRandom = ConfigLoader.getByDefault(map, RANDOM, false);
    this.isAdjacent = ConfigLoader.getByDefault(map, ADJACENT, false);
    this.isSelfExcluded = ConfigLoader.getByDefault(map, SELF_EXCLUDED, false);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add(SCOPE, scope)
        .add(TYPE, type)
        .add(RANDOM, isRandom)
        .add(ADJACENT, isAdjacent)
        .toString();
  }
}
