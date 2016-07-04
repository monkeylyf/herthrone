package com.herthrone.configuration;

import com.google.common.base.Optional;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class MinionConfig extends ConfigLoader.AbstractConfig<ConstMinion> {

  private static final String ATTACK = "attack";
  private static final String HEALTH = "health";
  private static final String MECHANICS = "mechanics";
  public final int attack;
  public final int health;
  public final Map<ConstMechanic, List<MechanicConfig>> mechanics;
  public final ConstType type = ConstType.MINION;

  MinionConfig(final Map map) {
    super(map);
    this.attack = (int) map.get(ATTACK);
    this.health = (int) map.get(HEALTH);
    this.mechanics = MechanicConfig.mechanicConfigFactory(map.get(MECHANICS));
  }

  @Override
  protected ConstMinion loadName(final String name) {
    return ConstMinion.valueOf(name.toUpperCase());
  }

  public List<MechanicConfig> getMechanic(final ConstMechanic mechanic) {
    if (mechanics.containsKey(mechanic)) {
      return mechanics.get(mechanic);
    } else {
      return Collections.EMPTY_LIST;
    }
  }
}
