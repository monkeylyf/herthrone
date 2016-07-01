package com.herthrone.configuration;


import com.herthrone.base.Config;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.constant.Constant;
import com.herthrone.helper.StringHelper;

import java.util.Collections;
import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class WeaponConfig implements Config<ConstWeapon> {

  private static final String NAME = "name";
  private static final String CLASS = "class";
  private static final String ATTACK = "attack";
  private static final String DURABILITY = "durability";
  private static final String CRYSTAL = "crystal";
  private static final String COLLECTIBLE = "collectible";
  private static final String MECHANICS = "mechanics";
  private static final String DISPLAY = "display";
  private final ConstWeapon name;
  private final ConstClass className;
  private final String displayName;
  private final int attack;
  private final int durability;
  private final int crystal;
  private final boolean collectible;
  private final Map<ConstMechanic, MechanicConfig> mechanics;

  public WeaponConfig(Map map) {
    this.name = ConstWeapon.valueOf(Constant.upperCaseValue(map, NAME));
    this.displayName = (map.containsKey(DISPLAY)) ?
        (String) map.get(DISPLAY) : StringHelper.lowerUnderscoreToUpperWhitespace(name);
    this.className = ConstClass.valueOf(Constant.upperCaseValue(map, CLASS));
    this.attack = (int) map.get(ATTACK);
    this.durability = (int) map.get(DURABILITY);
    this.crystal = (int) map.get(CRYSTAL);
    this.collectible = map.containsKey(COLLECTIBLE) && (Boolean) map.get(COLLECTIBLE);
    this.mechanics = map.containsKey(MECHANICS) ? MechanicConfig.mechanicConfigFactory(map.get(MECHANICS)) : Collections.EMPTY_MAP;
  }

  public int getAttack() {
    return attack;
  }

  public int getDurability() {
    return durability;
  }

  @Override
  public ConstWeapon name() {
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
    return ConstType.WEAPON;
  }

  @Override
  public int manaCost() {
    return crystal;
  }

  public boolean isCollectible() {
    return collectible;
  }

  public Map<ConstMechanic, MechanicConfig> getMechanics() {
    return mechanics;
  }
}
