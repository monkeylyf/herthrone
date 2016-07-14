package com.herthrone.configuration;


import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class WeaponConfig extends ConfigLoader.AbstractConfig<ConstWeapon> {

  private static final String ATTACK = "attack";
  private static final String DURABILITY = "durability";
  private static final String MECHANICS = "mechanics";
  public final int attack;
  public final int durability;
  public final Map<ConstTrigger, List<MechanicConfig>> mechanics;
  public final ConstType type = ConstType.WEAPON;

  @SuppressWarnings("unchecked")
  WeaponConfig(final Map map) {
    super(map);
    this.attack = (int) map.get(ATTACK);
    this.durability = (int) map.get(DURABILITY);
    this.mechanics = map.containsKey(MECHANICS) ?
        MechanicConfig.getTriggerToMechanicMap(map.get(MECHANICS)) : Collections.EMPTY_MAP;
  }

  @Override
  protected ConstWeapon loadName(final String name) {
    return ConstWeapon.valueOf(name.toUpperCase());
  }

}
