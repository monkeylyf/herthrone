package com.herthrone.configuration;

import com.herthrone.Constant;

import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class HeroConfig implements BaseConfig <Constant.Hero> {

  private final Constant.Hero name;
  private final Constant.Clazz className;
  private final String description;
  private final String heroPower;

  public HeroConfig(Map map) {
    this.name = Constant.Hero.valueOf(Constant.upperCaseValue(map, "name"));
    this.className = Constant.Clazz.valueOf(Constant.upperCaseValue(map, "class"));
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
  public Constant.Hero getName() {
    return this.name;
  }

  @Override
  public Constant.Clazz getClassName() {
    return this.className;
  }

  @Override
  public Constant.Type getType() {
    return Constant.Type.HERO;
  }

  @Override
  public int getCrystal() {
    return 0;
  }
}
