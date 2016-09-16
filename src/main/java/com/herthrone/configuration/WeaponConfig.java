package com.herthrone.configuration;


import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.service.Weapon;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

  public Weapon toWeaponProto() {
    return Weapon.newBuilder()
        .setName(name.toString())
        .setCrystal(crystal)
        .setDisplayName(displayName)
        .setAttack(attack)
        .setDurability(durability)
        .addAllMechanics(mechanics.values().stream()
            .flatMap(mechanicConfigs -> mechanicConfigs.stream())
            .map(MechanicConfig::toMechanicProto)
            .collect(Collectors.toList()))
        .build();
  }
}
