package com.herthrone.configuration;

import com.herthrone.base.Config;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;
import com.herthrone.helper.StringHelper;

import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class HeroConfig implements Config<ConstHero> {

  private static final String NAME = "name";
  private static final String CLASS = "class";
  private static final String HERO_POWER = "hero_power";
  private static final String DESCRIPTION = "description";
  private static final String DISPLAY = "display";
  private final ConstHero name;
  private final ConstClass className;
  private final String displayName;
  private final String description;
  private final ConstSpell heroPower;

  public HeroConfig(Map map) {
    this.name = ConstHero.valueOf(Constant.upperCaseValue(map, NAME));
    this.displayName = (map.containsKey(DISPLAY)) ?
        (String) map.get(DISPLAY) : StringHelper.lowerUnderscoreToUpperWhitespace(name);
    this.className = ConstClass.valueOf(Constant.upperCaseValue(map, CLASS));
    this.heroPower = ConstSpell.valueOf(Constant.upperCaseValue(map, HERO_POWER));
    this.description = (String) map.get(DESCRIPTION);
  }

  public ConstSpell getHeroPower() {
    return heroPower;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public ConstHero name() {
    return name;
  }

  @Override
  public String displayName() {
    return displayName;
  }

  @Override
  public ConstClass className() {
    return className;
  }

  @Override
  public ConstType type() {
    return ConstType.HERO;
  }

  @Override
  public int manaCost() {
    return 0;
  }
}
