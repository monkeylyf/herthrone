package com.herthrone.configuration;

import com.herthrone.Constant;

import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class MinionConfig implements BaseConfig <Constant.Minion> {

  private final Constant.Minion name;
  private final Constant.Clazz className;
  private final int attack;
  private final int health;
  private final int crystal;
  private final Map<String, MechanicConfig> mechanics;
  private final boolean collectible;

  public MinionConfig(final Map map) {
    this.name = Constant.Minion.valueOf(Constant.upperCaseValue(map, "name"));
    this.className = Constant.Clazz.valueOf(Constant.upperCaseValue(map, "class"));
    this.attack = (int) map.get("attack");
    this.health = (int) map.get("health");
    this.crystal = (int) map.get("crystal");
    this.mechanics = MechanicConfig.mechanicConfigFactory(map.get("mechanics"));
    this.collectible = map.containsKey("collectible") && (Boolean) map.get("collectible");
  }

  public int getAttack() {
    return this.attack;
  }

  public int getHealth() {
    return this.health;
  }

  public int getCrystal() {
    return this.crystal;
  }

  public Map<String, MechanicConfig> getMechanics() {
    return this.mechanics;
  }

  @Override
  public Constant.Minion getName() {
    return this.name;
  }

  @Override
  public Constant.Clazz getClassName() {
    return this.className;
  }

  @Override
  public Constant.Type getType() {
    return Constant.Type.MINION;
  }

  public boolean isCollectible() {
    return this.collectible;
  }
}
