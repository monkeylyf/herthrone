package com.herthrone.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class SpellConfig {

  private final String name;
  private final String className;
  private final List<EffectConfig> effects;

  public SpellConfig(Map map) {
    this.name = (String) map.get("name");
    this.className = (String) map.get("class");
    this.effects = new ArrayList<>();

    List<Object> actions = (List) map.get("actions");
    for (Object action : actions) {
      Map actionMap = (Map) action;
      EffectConfig config = new EffectConfig(actionMap);
      this.effects.add(config);
    }
  }

  public String getName() {
    return name;
  }

  public String getClassName() {
    return className;
  }

  public List<EffectConfig> getEffects() {
    return effects;
  }
}
