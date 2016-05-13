package com.herthrone.configuration;

import com.herthrone.Constant;

import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class HeroConfig implements BaseConfig {

  private final String name;
  private final String className;
  private final String description;
  private final String heroPower;
  private final String type = Constant.HERO;

  public HeroConfig(Map map) {
    this.name = (String) map.get("name");
    this.className = (String) map.get("class");
    this.heroPower = (String) map.get("hero_power");
    this.description = (String) map.get("description");
  }

  public String getHeroPower() {
    return this.heroPower;
  }

  public String getDescription() {
    return this.description;
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
    return 0;
  }
}
