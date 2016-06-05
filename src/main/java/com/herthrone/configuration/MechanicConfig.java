package com.herthrone.configuration;

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

  public static Map<String, MechanicConfig> mechanicConfigFactory(Object configList) {
    final List<Map> configMaps = (List<Map>) configList;
    Map<String, MechanicConfig> configs = new HashMap<>();
    for (Map map : configMaps) {
      MechanicConfig config = new MechanicConfig(map);
      configs.put(config.getMechanic().toString(), config);
    }
    return configs;
  }

  public ConstMechanic getMechanic() {
    return mechanic;
  }

}
