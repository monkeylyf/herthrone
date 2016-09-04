package com.herthrone.configuration;

import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;

import java.util.Map;


public class HeroConfig extends ConfigLoader.AbstractConfig<ConstHero> {

  private static final String HERO_POWER = "hero_power";
  public final ConstSpell heroPower;
  public final ConstType type = ConstType.HERO;

  HeroConfig(final Map map) {
    super(map);
    this.heroPower = ConstSpell.valueOf(ConfigLoader.getUpperCaseStringValue(map, HERO_POWER));
  }

  @Override
  protected ConstHero loadName(final String name) {
    return ConstHero.valueOf(name.toUpperCase());
  }
}
