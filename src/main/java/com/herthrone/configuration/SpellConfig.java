package com.herthrone.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class SpellConfig implements BaseConfig {

  private final String name;
  private final String className;
  private final String type;
  private final int crystal;
  private final List<EffectConfig> effects;

  public SpellConfig(Map map) {
    this.name = (String) map.get("name");
    this.className = (String) map.get("class");
    this.type = (String) map.get("type");
    this.crystal = (int) map.get("crystal");
    this.effects = new ArrayList<>();

    List<Object> actions = (List) map.get("actions");
    for (Object action : actions) {
      Map actionMap = (Map) action;
      EffectConfig config = new EffectConfig(actionMap);
      this.effects.add(config);
    }
  }


  public List<EffectConfig> getEffects() {
    return effects;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getClassName() {
    return this.className;
  }

  @Override
  public String getType() {
    return this.type;
  }

  @Override
  public int getCrystal() {
    return this.crystal;
  }
}
