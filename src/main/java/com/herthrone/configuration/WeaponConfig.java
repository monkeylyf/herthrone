package com.herthrone.configuration;


import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.constant.Constant;

import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class WeaponConfig implements BaseConfig<ConstWeapon> {

  private final ConstWeapon name;
  private final ConstClass className;
  private final int attack;
  private final int durability;
  private final int crystal;
  private final boolean collectible;

  public WeaponConfig(Map map) {
    this.name = ConstWeapon.valueOf(Constant.upperCaseValue(map, "name"));
    this.className = ConstClass.valueOf(Constant.upperCaseValue(map, "class"));
    this.attack = (int) map.get("attack");
    this.durability = (int) map.get("durability");
    this.crystal = (int) map.get("crystal");
    this.collectible = map.containsKey("collectible") && (Boolean) map.get("collectible");
  }

  public int getAttack() {
    return attack;
  }

  public int getDurability() {
    return durability;
  }

  @Override
  public ConstWeapon getName() {
    return name;
  }

  @Override
  public ConstClass getClassName() {
    return className;
  }

  @Override
  public ConstType getType() {
    return ConstType.WEAPON;
  }

  @Override
  public int getCrystal() {
    return crystal;
  }

  public boolean isCollectible() {
    return collectible;
  }
}
