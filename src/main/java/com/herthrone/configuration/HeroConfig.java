package com.herthrone.configuration;

import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class HeroConfig {

  private final String name;
  private final String className;
  private final String description;
  private final String heroPower;

  public HeroConfig(Map map) {
    this.name = (String) map.get("name");
    this.className = (String) map.get("class");
    this.heroPower = (String) map.get("hero_power");
    this.description = (String) map.get("description");
  }

  public String getName() { return this.name; };
  public String getClassName() { return this.className; }
  public String getHeroPower() { return this.heroPower; }
  public String getDescription() { return this.description; }
}
