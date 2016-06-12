package com.herthrone.configuration;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yifengliu on 5/10/16.
 */
public class MechanicConfig {

  private final ConstMechanic mechanic;
  private final Optional<EffectConfig> effect;

  public MechanicConfig(Map map) {
    this.mechanic = ConstMechanic.valueOf(Constant.upperCaseValue(map, "name"));
    this.effect = map.containsKey("effect") ? Optional.of(new EffectConfig(map)) : Optional.absent();
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
    return Objects.toStringHelper(this)
        .add("mechanic", mechanic)
        .add("effect", effect)
        .toString();
  }
}