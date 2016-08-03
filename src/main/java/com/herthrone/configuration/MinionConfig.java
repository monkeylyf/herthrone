package com.herthrone.configuration;

import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstType;

import java.util.List;
import java.util.Map;

import static com.herthrone.configuration.ConfigLoader.getUpperCaseStringValue;

public class MinionConfig extends ConfigLoader.AbstractConfig<ConstMinion> {

  private static final String ATTACK = "attack";
  private static final String HEALTH = "health";
  private static final String MECHANICS = "mechanics";
  private static final String TYPE = "type";
  public final int attack;
  public final int health;
  public final ConstType type;
  public final Map<ConstTrigger, List<MechanicConfig>> mechanics;

  MinionConfig(final Map map) {
    super(map);
    this.attack = (int) map.get(ATTACK);
    this.health = (int) map.get(HEALTH);
    this.type = (map.containsKey(TYPE)) ?
        ConstType.valueOf(getUpperCaseStringValue(map, TYPE)) : ConstType.MINION;
    this.mechanics = MechanicConfig.getTriggerToMechanicMap(map.get(MECHANICS));
  }

  @Override
  protected ConstMinion loadName(final String name) {
    return ConstMinion.valueOf(name.toUpperCase());
  }
}
