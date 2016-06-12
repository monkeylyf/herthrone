package com.herthrone.configuration;

import com.herthrone.base.Config;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;

import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class HeroConfig implements Config<ConstHero> {

  private final ConstHero name;
  private final ConstClass className;
  private final String description;
  private final ConstSpell heroPower;

  public HeroConfig(Map map) {
    this.name = ConstHero.valueOf(Constant.upperCaseValue(map, "name"));
    this.className = ConstClass.valueOf(Constant.upperCaseValue(map, "class"));
    this.heroPower = ConstSpell.valueOf(Constant.upperCaseValue(map, "hero_power"));
    this.description = (String) map.get("description");
  }

  public ConstSpell getHeroPower() {
    return heroPower;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public ConstHero getName() {
    return name;
  }

  @Override
  public ConstClass getClassName() {
    return className;
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
