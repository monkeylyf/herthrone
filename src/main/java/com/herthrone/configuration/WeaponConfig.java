package com.herthrone.configuration;


import com.herthrone.Constant;

import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class WeaponConfig implements BaseConfig<Constant.Weapon> {

  private final Constant.Weapon name;
  private final Constant.Clazz className;
  private final int attack;
  private final int durability;
  private final int crystal;
  private final boolean collectible;

  public WeaponConfig(Map map) {
    this.name = Constant.Weapon.valueOf(Constant.upperCaseValue(map, "name"));
    this.className = Constant.Clazz.valueOf(Constant.upperCaseValue(map, "class"));
    this.attack = (int) map.get("attack");
    this.durability = (int) map.get("durability");
    this.crystal = (int) map.get("crystal");
    this.collectible = map.containsKey("collectible") && (Boolean) map.get("collectible");
  }

  public int getAttack() {
    return this.attack;
  }

  public int getDurability() {
    return this.durability;
  }

  @Override
  public Constant.Weapon getName() {
    return this.name;
  }

  @Override
  public Constant.Clazz getClassName() {
    return this.className;
  }

  @Override
  public Constant.Type getType() {
    return Constant.Type.WEAPON;
  }

  @Override
  public int getCrystal() {
    return this.crystal;
  }

  public boolean isCollectible() {
    return this.collectible;
  }
}
