package com.herthrone.configuration;

import com.herthrone.base.Config;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;

import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class MinionConfig implements Config<ConstMinion> {

  private final ConstMinion name;
  private final ConstClass className;
  private final int attack;
  private final int health;
  private final int crystal;
  private final Map<String, MechanicConfig> mechanics;
  private final boolean collectible;

  public MinionConfig(final Map map) {
    this.name = ConstMinion.valueOf(Constant.upperCaseValue(map, "name"));
    this.className = ConstClass.valueOf(Constant.upperCaseValue(map, "class"));
    this.attack = (int) map.get("attack");
    this.health = (int) map.get("health");
    this.crystal = (int) map.get("crystal");
    this.mechanics = MechanicConfig.mechanicConfigFactory(map.get("mechanics"));
    this.collectible = map.containsKey("collectible") && (Boolean) map.get("collectible");
  }

  public int getAttack() {
    return attack;
  }

  public int getHealth() {
    return health;
  }

  public int getCrystal() {
    return crystal;
  }

  public Map<String, MechanicConfig> getMechanics() {
    return mechanics;
  }

  @Override
  public ConstMinion getName() {
    return name;
  }

  @Override
  public ConstClass getClassName() {
    return className;
  }

  @Override
  public ConstType getType() {
    return ConstType.MINION;
  }

  public boolean isCollectible() {
    return collectible;
  }
}
