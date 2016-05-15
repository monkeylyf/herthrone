package com.herthrone.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yifengliu on 5/10/16.
 */
public class MechanicConfig {

  private final String mechanic;

  public MechanicConfig(Map map) {
    this.mechanic = (String) map.get("name");
  }

  public static Map<String, MechanicConfig> mechanicConfigFactory(Object configList) {
    final List<Map> configMaps = (List<Map>) configList;
    Map<String, MechanicConfig> configs = new HashMap<>();
    for (Map map : configMaps) {
      MechanicConfig config = new MechanicConfig(map);
      configs.put(config.getMechanic(), config);
    }
    return configs;
  }

  public String getMechanic() {
    return mechanic;
  }

}
