package com.herthrone.configuration;

import java.util.List;
import java.util.Map;

/**
* Created by yifeng on 4/12/16.
*/
public class MinionConfig {

  private final int id;
  private final String name;
  private final String className;
  private final String type = Constants.MINION;
  private final int attack;
  private final int health;
  private final int crystal;
  private final List<String> mechanics;

  public MinionConfig(final Map map) {
    this.id = (int) map.get("id");
    this.name = (String) map.get("name");
    this.className = (String) map.get("class");
    this.attack = (int) map.get("attack");
    this.health = (int) map.get("health");
    this.crystal = (int) map.get("crystal");
    this.mechanics = (List<String>) map.get("mechanics");
  }

  public String toString() { return this.name; }

  public int getId() { return this.id; }
  public String getName() { return this.name; }
  public String getClassName() { return this.className; }
  public int getAttack() { return this.attack; }
  public int getHealth() { return this.health; }
  public int getCrystal() { return this.crystal; }
  public List<String> getMechanics() { return this.mechanics; }
  public String getType() { return this.type; }

}
