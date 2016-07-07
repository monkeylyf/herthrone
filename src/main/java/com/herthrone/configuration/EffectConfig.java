package com.herthrone.configuration;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.herthrone.constant.ConstEffectType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

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
  public int value;
  public final boolean isUnique;
  public final boolean isRandom;
  public final boolean isPermanent;
  public final List<String> choices;
  public final TargetConfig target;
  public final Optional<ConditionConfig> conditionConfigOptional;

  @SuppressWarnings("unchecked")
  EffectConfig(final Map map) {
    this.name = ConstEffectType.valueOf(ConfigLoader.getUpperCaseStringValue(map, EFFECT));
    this.type = (String) map.get(TYPE);
    this.value = (int) map.get(VALUE);
    this.isPermanent = ConfigLoader.getByDefault(map, PERMANENT, false);
    this.isUnique = ConfigLoader.getByDefault (map, UNIQUE, false);
    this.isRandom = ConfigLoader.getByDefault (map, RANDOM, false);
    this.choices = ConfigLoader.getByDefault(map, CHOICES, Collections.EMPTY_LIST);
    this.target = new TargetConfig((Map) map.get(TARGET));
    this.conditionConfigOptional = (map.containsKey(CONDITION)) ?
        Optional.of(new ConditionConfig((Map) map.get(CONDITION))) :
        Optional.absent();
  }

  private EffectConfig(final EffectConfig effectConfig) {
    this.name = effectConfig.name;
    this.type = effectConfig.type;
    this.value = effectConfig.value;
    this.isPermanent = effectConfig.isPermanent;
    this.isUnique = effectConfig.isUnique;
    this.isRandom = effectConfig.isRandom;
    this.choices = effectConfig.choices;
    this.target = effectConfig.target;
    this.conditionConfigOptional = effectConfig.conditionConfigOptional;
  }

  public static EffectConfig clone(final EffectConfig effectConfigToClone) {
    return new EffectConfig(effectConfigToClone);
  }

  @Override
  public String toString() {
    final Objects.ToStringHelper stringHelper = Objects.toStringHelper(this)
        .add(EFFECT, name)
        .add(TYPE, type)
        .add(VALUE, value)
        .add(TARGET, target);
    if (isPermanent) {
      stringHelper.add(PERMANENT, isPermanent);
    }
    if (isRandom) {
      stringHelper.add(RANDOM, isRandom);
    }
    if (isUnique) {
      stringHelper.add(UNIQUE, isUnique);
    }
    if (choices.size() > 0) {
      stringHelper.add(CHOICES, choices);
    }
    return stringHelper.toString();
  }
}