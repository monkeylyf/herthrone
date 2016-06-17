package com.herthrone.configuration;

import com.google.common.base.Optional;
import com.herthrone.base.Config;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;

import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class MinionConfig implements Config<ConstMinion> {

  private static final String NAME_FIELD = "name";
  private static final String CLASS_FIELD = "class";
  private static final String DISPLAY_FIELD = "display";
  private static final String ATTACK_FIELD = "attack";
  private static final String TYPE_FIELD = "type";
  private static final String HEALTH_FIELD = "health";
  private static final String CRYSTAL_FIELD = "crystal";
  private static final String MECHANICS_FIELD = "mechanics";
  private static final String COLLECTIBLE_FIELD = "collectible";
  private final ConstMinion name;
  private final ConstClass className;
  private final String displayName;
  private final int attack;
  private final int health;
  private final int crystal;
  private final ConstType type;
  private final Map<ConstMechanic, MechanicConfig> mechanics;
  private final boolean collectible;

  public MinionConfig(final Map map) {
    this.name = ConstMinion.valueOf(Constant.upperCaseValue(map, NAME_FIELD));
    this.className = ConstClass.valueOf(Constant.upperCaseValue(map, CLASS_FIELD));
    this.displayName = (String) map.get(DISPLAY_FIELD);
    this.attack = (int) map.get(ATTACK_FIELD);
    this.health = (int) map.get(HEALTH_FIELD);
    this.crystal = (int) map.get(CRYSTAL_FIELD);
    this.type = ConstType.valueOf(Constant.upperCaseValue(map, TYPE_FIELD, ConstType.GENERAL.toString()));
    this.mechanics = MechanicConfig.mechanicConfigFactory(map.get(MECHANICS_FIELD));
    this.collectible = map.containsKey(COLLECTIBLE_FIELD) && (Boolean) map.get(COLLECTIBLE_FIELD);
  }

  public int getAttack() {
    return attack;
  }

  public int getHealth() {
    return health;
  }

  public Map<ConstMechanic, MechanicConfig> getMechanics() {
    return mechanics;
  }

  public Optional<MechanicConfig> getMechanic(final ConstMechanic mechanic) {
    final MechanicConfig config = mechanics.get(mechanic);
    return Optional.fromNullable(config);
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

  public int getCrystal() {
    return crystal;
  }

  public boolean isCollectible() {
    return collectible;
  }
}
