package com.herthrone.configuration;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstSelect;
import com.herthrone.constant.ConstTarget;
import com.herthrone.constant.ConstType;

import java.util.Map;
import java.util.OptionalInt;

import static com.herthrone.configuration.ConfigLoader.getUpperCaseStringValue;

public class TargetConfig {

  private static final String SELECT = "select";
  private static final String SCOPE = "scope";
  private static final String TYPE = "type";
  private static final String RANDOM = "random";
  private static final String ADJACENT = "adjacent";
  private static final String SELF_EXCLUDED = "self_excluded";
  private static final String MECHANIC = "mechanic";
  private static final String RANDOM_TARGET = "random_target";
  public final ConstTarget scope;
  public final ConstType type;
  public final ConstSelect select;
  public final boolean isRandom;
  public final boolean isAdjacent;
  public final boolean isSelfExcluded;
  public final Optional<ConstMechanic> mechanic;
  public final OptionalInt randomTarget;

  TargetConfig(final Map map) {
    this.scope = ConstTarget.valueOf(getUpperCaseStringValue(map, SCOPE));
    this.type = ConstType.valueOf(getUpperCaseStringValue(map, TYPE));
    this.isRandom = ConfigLoader.getByDefault(map, RANDOM, false);
    this.isAdjacent = ConfigLoader.getByDefault(map, ADJACENT, false);
    this.isSelfExcluded = ConfigLoader.getByDefault(map, SELF_EXCLUDED, false);
    this.mechanic = (map.containsKey(MECHANIC)) ?
        Optional.of(ConstMechanic.valueOf(getUpperCaseStringValue(map, MECHANIC))) :
        Optional.absent();
    this.randomTarget = (map.containsKey(RANDOM_TARGET)) ?
        OptionalInt.of((int) map.get(RANDOM_TARGET)) : OptionalInt.empty();
    this.select = (map.containsKey(SELECT)) ?
        ConstSelect.valueOf(getUpperCaseStringValue(map, SELECT)) : ConstSelect.NOT_PROVIDED;
  }

  private TargetConfig(final ConstTarget target, final ConstType type) {
    this.scope = target;
    this.type = type;
    this.isRandom = false;
    this.isAdjacent = false;
    this.isSelfExcluded = false;
    this.mechanic = Optional.absent();
    this.randomTarget = OptionalInt.empty();
    this.select = ConstSelect.NOT_PROVIDED;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add(SCOPE, scope)
        .add(SELECT, select)
        .add(TYPE, type)
        .add(RANDOM, isRandom)
        .add(ADJACENT, isAdjacent)
        .toString();
  }

  public static TargetConfig getDefaultTargetConfig() {
    return new TargetConfig(ConstTarget.ALL.ALL, ConstType.ALL);
  }
}
