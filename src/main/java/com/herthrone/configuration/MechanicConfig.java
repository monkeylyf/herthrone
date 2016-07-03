package com.herthrone.configuration;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstTrigger;

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
  public final ConstMechanic mechanic;
  public final Optional<ConstTrigger> triggeringEvent;
  public final Optional<EffectConfig> effect;

  MechanicConfig(Map map) {
    this.mechanic = ConstMechanic.valueOf(getUpperCaseStringValue(map, NAME));
    this.triggeringEvent = map.containsKey(TRIGGER) ?
        Optional.of(ConstTrigger.valueOf(getUpperCaseStringValue(map, TRIGGER))) : Optional.absent();
    this.effect = map.containsKey(EFFECT) ? Optional.of(new EffectConfig(map)) : Optional.absent();
  }

  public static Map<ConstMechanic, MechanicConfig> mechanicConfigFactory(Object configList) {
    Map<ConstMechanic, MechanicConfig> configs = new HashMap<>();
    if (configList != null) {
      @SuppressWarnings("unchecked") final List<Map> configMaps = (List<Map>) configList;
      for (Map map : configMaps) {
        MechanicConfig config = new MechanicConfig(map);
        configs.put(config.mechanic, config);
      }
    }
    return configs;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add(NAME, mechanic).add(EFFECT, effect).toString();
  }
}
