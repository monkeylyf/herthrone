package com.herthrone.configuration;


import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class WeaponConfig implements BaseConfig {

  private final String name;
  private final String className;
  private final int attack;
  private final int duration;
  private final int crystal;

  public WeaponConfig(Map map) {
    this.name = (String) map.get("name");
    this.className = (String) map.get("className");
    this.attack = (int) map.get("attack");
    this.duration = (int) map.get("duration");
    this.crystal = (int) map.get("crystal");
  }

  public int getAttack() {
    return this.attack;
  }

  public int getDuration() {
    return this.duration;
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
    return Constants.WEAPON;
  }

  @Override
  public int getCrystal() {
    return this.crystal;
  }
}
