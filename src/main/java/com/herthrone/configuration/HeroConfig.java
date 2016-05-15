package com.herthrone.configuration;

import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;

import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class HeroConfig implements BaseConfig<ConstHero> {

  private final ConstHero name;
  private final ConstClass className;
  private final String description;
  private final String heroPower;

  public HeroConfig(Map map) {
    this.name = ConstHero.valueOf(Constant.upperCaseValue(map, "name"));
    this.className = ConstClass.valueOf(Constant.upperCaseValue(map, "class"));
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
  public ConstHero getName() {
    return this.name;
  }

  @Override
  public ConstClass getClassName() {
    return this.className;
  }

  @Override
  public ConstType getType() {
    return ConstType.HERO;
  }

  @Override
  public int getCrystal() {
    return 0;
  }
}
