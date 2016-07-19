package com.herthrone.configuration;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.herthrone.constant.ConstDependency;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstTrigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.herthrone.configuration.ConfigLoader.addIfConditionIsTrue;
import static com.herthrone.configuration.ConfigLoader.getUpperCaseStringValue;

/**
 * Created by yifeng on 4/18/16.
 */
public class MechanicConfig {

  private static final String NAME = "name";
  private static final String TRIGGER = "trigger";
  private static final String TYPE = "type";
  private static final String EFFECT = "effect";
  private static final String VALUE = "value";
  private static final String PERMANENT = "permanent";
  private static final String UNIQUE = "unique";
  private static final String CHOICES = "choices";
  private static final String TARGET = "target";
  private static final String VALUE_DEPENDENCY = "value_dependency";
  private static final String CONDITION = "condition";
  private static final String TRIGGER_ONLY_WITH_TARGET = "trigger_only_with_target";
  public final ConstMechanic mechanic;
  public final ConstTrigger trigger;
  public final boolean triggerOnlyWithTarget;
  public final ConstEffectType effectType;
  public final String type;  // TODO: get rid of all String.
  public final boolean isUnique;
  public final boolean isPermanent;
  public final List<String> choices;
  public final TargetConfig target;
  public final Optional<ConstDependency> valueDependency;
  public final Optional<ConditionConfig> conditionConfigOptional;
  public int value;

  @SuppressWarnings("unchecked")
  MechanicConfig(final Map map) {
    this.effectType = ConstEffectType.valueOf(getUpperCaseStringValue(map, EFFECT));
    this.mechanic = ConstMechanic.valueOf(getUpperCaseStringValue(map, NAME));
    this.trigger = map.containsKey(TRIGGER) ?
        ConstTrigger.valueOf(getUpperCaseStringValue(map, TRIGGER)) : ConstTrigger.NO_TRIGGER;
    this.triggerOnlyWithTarget = ConfigLoader.getByDefault(map, TRIGGER_ONLY_WITH_TARGET, false);
    this.type = (String) map.get(TYPE);
    this.value = ConfigLoader.getByDefault(map, VALUE, 0);
    this.isPermanent = ConfigLoader.getByDefault(map, PERMANENT, false);
    this.isUnique = ConfigLoader.getByDefault(map, UNIQUE, false);
    this.choices = ConfigLoader.getByDefault(map, CHOICES, Collections.EMPTY_LIST);
    this.target = new TargetConfig((Map) map.get(TARGET));
    this.valueDependency = (map.containsKey(VALUE_DEPENDENCY)) ?
        Optional.of(ConstDependency.valueOf(getUpperCaseStringValue(map, VALUE_DEPENDENCY))) :
        Optional.absent();
    this.conditionConfigOptional = (map.containsKey(CONDITION)) ?
        Optional.of(new ConditionConfig((Map) map.get(CONDITION))) : Optional.absent();
  }

  private MechanicConfig(final MechanicConfig mechanicConfig) {
    this.effectType = mechanicConfig.effectType;
    this.mechanic = mechanicConfig.mechanic;
    this.trigger = mechanicConfig.trigger;
    this.triggerOnlyWithTarget = mechanicConfig.triggerOnlyWithTarget;
    this.type = mechanicConfig.type;
    this.value = mechanicConfig.value;
    this.isPermanent = mechanicConfig.isPermanent;
    this.isUnique = mechanicConfig.isUnique;
    this.choices = mechanicConfig.choices;
    this.target = mechanicConfig.target;
    this.valueDependency = mechanicConfig.valueDependency;
    this.conditionConfigOptional = mechanicConfig.conditionConfigOptional;
  }

  public static MechanicConfig clone(final MechanicConfig mechanicConfigToClone) {
    return new MechanicConfig(mechanicConfigToClone);
  }

  public static Map<ConstTrigger, List<MechanicConfig>> getTriggerToMechanicMap(final Object configList) {
    final Map<ConstTrigger, List<MechanicConfig>> configs = new HashMap<>();
    if (configList != null) {
      @SuppressWarnings("unchecked") final List<Map> configMaps = (List<Map>) configList;
      for (final Map map : configMaps) {
        final MechanicConfig config = new MechanicConfig(map);
        if (configs.containsKey(config.trigger)) {
          configs.get(config.trigger).add(config);
        } else {
          configs.put(config.trigger, new ArrayList<>(Collections.singletonList(config)));
        }
      }
    }
    return configs;
  }

  @Override
  public String toString() {
    final Objects.ToStringHelper stringHelper = Objects.toStringHelper(this)
        .add(EFFECT, effectType)
        .add(TYPE, type)
        .add(TARGET, target);
    if (valueDependency.isPresent()) {
      stringHelper.add(VALUE_DEPENDENCY, valueDependency.get());
    }
    addIfConditionIsTrue(value > 0, stringHelper, VALUE, value);
    addIfConditionIsTrue(isPermanent, stringHelper, PERMANENT, isPermanent);
    addIfConditionIsTrue(isUnique, stringHelper, UNIQUE, isUnique);
    addIfConditionIsTrue(choices.size() > 0, stringHelper, CHOICES, choices);
    return stringHelper.toString();
  }
}