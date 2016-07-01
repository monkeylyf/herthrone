package com.herthrone.configuration;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.herthrone.constant.ConstEvent;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yifengliu on 5/10/16.
 */
public class MechanicConfig {

  private static final String NAME = "name";
  private static final String EFFECT = "effect";
  private static final String TRIGGER = "trigger";
  private final ConstMechanic mechanic;
  private final Optional<ConstEvent> triggeringEvent;
  private final Optional<EffectConfig> effect;

  public MechanicConfig(Map map) {
    this.mechanic = ConstMechanic.valueOf(Constant.upperCaseValue(map, NAME));
    this.triggeringEvent = map.containsKey(TRIGGER) ?
        Optional.of(ConstEvent.valueOf(Constant.upperCaseValue(map, TRIGGER))) : Optional.absent();
    this.effect = map.containsKey(EFFECT) ? Optional.of(new EffectConfig(map)) : Optional.absent();
  }

  public static Map<ConstMechanic, MechanicConfig> mechanicConfigFactory(Object configList) {
    final List<Map> configMaps = (List<Map>) configList;
    Map<ConstMechanic, MechanicConfig> configs = new HashMap<>();
    for (Map map : configMaps) {
      MechanicConfig config = new MechanicConfig(map);
      configs.put(config.getMechanic(), config);
    }
    return configs;
  }

  public ConstMechanic getMechanic() {
    return mechanic;
  }

  public Optional<EffectConfig> getEffect() {
    return effect;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add(NAME, mechanic).add(EFFECT, effect).toString();
  }
}
