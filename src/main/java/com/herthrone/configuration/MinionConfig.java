package com.herthrone.configuration;

import java.util.List;
import java.util.Map;

/**
* Created by yifeng on 4/12/16.
*/
public class MinionConfig implements BaseConfig {

  private final String name;
  private final String className;
  private final String type = Constants.MINION;
  private final int attack;
  private final int health;
  private final int crystal;
  private final List<String> mechanics;
  private final boolean collectible;

  public MinionConfig(final Map map) {
    this.name = (String) map.get("name");
    this.className = (String) map.get("class");
    this.attack = (int) map.get("attack");
    this.health = (int) map.get("health");
    this.crystal = (int) map.get("crystal");
    this.mechanics = (List<String>) map.get("mechanics");
    this.collectible = map.containsKey("collectible") && (Boolean) map.get("collectible");
  }

  public int getAttack() { return this.attack; }
  public int getHealth() { return this.health; }
  public int getCrystal() { return this.crystal; }
  public List<String> getMechanics() { return this.mechanics; }

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
    return this.type;
  }

  public boolean isCollectible() {
    return this.collectible;
  }
}
