package com.herthrone.configuration;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstTrigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.herthrone.configuration.ConfigLoader.getUpperCaseStringValue;

/**
 * Created by yifengliu on 5/10/16.
 */
public class MechanicConfig {

  private static final String NAME = "name";
  private static final String EFFECT = "effect";
  private static final String TRIGGER = "trigger";
  private static final String TRIGGER_ONLY_WITH_TARGET = "trigger_only_with_target";
  public final ConstMechanic mechanic;
  public final ConstTrigger trigger;
  public final Optional<EffectConfig> effect;
  public final boolean triggerOnlyWithTarget;

  MechanicConfig(final Map map) {
    this.mechanic = ConstMechanic.valueOf(getUpperCaseStringValue(map, NAME));
    this.trigger = map.containsKey(TRIGGER) ?
        ConstTrigger.valueOf(getUpperCaseStringValue(map, TRIGGER)) : ConstTrigger.NO_TRIGGER;
    this.triggerOnlyWithTarget = ConfigLoader.getByDefault(map, TRIGGER_ONLY_WITH_TARGET, false);
    this.effect = map.containsKey(EFFECT) ? Optional.of(new EffectConfig(map)) : Optional.absent();
  }

  public static Map<ConstTrigger, List<MechanicConfig>> mechanicConfigFactory(final Object configList) {
    final Map<ConstTrigger, List<MechanicConfig>> configs = new HashMap<>();
    if (configList != null) {
      @SuppressWarnings("unchecked") final List<Map> configMaps = (List<Map>) configList;
      for (Map map : configMaps) {
        final MechanicConfig config = new MechanicConfig(map);
        if (configs.containsKey(config.trigger)) {
          configs.get(config.trigger).add(config);
        } else {
          configs.put(config.trigger, new ArrayList<>(Arrays.asList(config)));
        }
      }
    }
    return configs;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add(NAME, mechanic).add(EFFECT, effect).toString();
  }
}
