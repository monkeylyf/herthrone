package com.herthrone.configuration;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.herthrone.constant.ConstEffectType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.herthrone.configuration.ConfigLoader.getByDefault;
import static com.herthrone.configuration.ConfigLoader.getUpperCaseStringValue;

/**
 * Created by yifeng on 4/18/16.
 */
public class EffectConfig {

  private static final String TYPE = "type";
  private static final String EFFECT = "effect";
  private static final String VALUE = "value";
  private static final String PERMANENT = "permanent";
  private static final String UNIQUE = "unique";
  private static final String CHOICES = "choices";
  private static final String RANDOM = "random";
  private static final String TARGET = "target";
  private static final String CONDITION = "condition";
  public final ConstEffectType name;
  public final String type;  // TODO: get rid of all String.
  public final int value;
  public final boolean isUnique;
  public final boolean isRandom;
  public final boolean isPermanent;
  public final List<String> choices;
  public final TargetConfig target;
  public final Optional<ConditionConfig> conditionConfigOptional;

  EffectConfig(Map map) {
    this.name = ConstEffectType.valueOf(getUpperCaseStringValue(map, EFFECT));
    this.type = (String) map.get(TYPE);
    this.value = (int) map.get(VALUE);
    this.isPermanent = getByDefault(map, PERMANENT, false);
    this.isUnique = getByDefault (map, UNIQUE, false);
    this.isRandom = getByDefault (map, RANDOM, false);
    this.choices = getByDefault(map, CHOICES, Collections.EMPTY_LIST);
    this.target = new TargetConfig((Map) map.get(TARGET));
    this.conditionConfigOptional = (map.containsKey(CONDITION)) ?
        Optional.of(new ConditionConfig((Map) map.get(CONDITION))) :
        Optional.absent();
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add(EFFECT, name)
        .add(TYPE, type)
        .add(VALUE, value)
        .add(TARGET, target)
        .toString();
  }
}