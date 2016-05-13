package com.herthrone.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yifengliu on 5/10/16.
 */
public class MechanicConfig {

  private final String mechanic;

  public MechanicConfig(Map map) {
    this.mechanic = (String) map.get("name");
  }

  public String getMechanic() {
    return mechanic;
  }

  public static Map<String, MechanicConfig> mechanicConfigFactory(Object configList) {
    final List<Map> configMaps = (List<Map>) configList;
    Map<String, MechanicConfig> configs = new HashMap<>();
    for (Map map : configMaps) {
      MechanicConfig config = new MechanicConfig(map);
      configs.put(config.getMechanic(), config);
    }
    return configs;
  }

  public static enum Mechanic {
    BATTLECRY,
    CARD_DRAW_EFFECT,
    CHARGE,
    CHOOSE_ONE,
    COMBO,
    COPY_EFFECT,
    DEAL_DAMAGE,
    DEATHRATTLE,
    DESTROY_EFFECT,
    DISCARD_EFFECT,
    DIVINE_SHIELD,
    ELUSIVE,
    ENRAGE,
    EQUIP,
    FORGETFUl,
    FREEZE,
    GENERATE_EFFECT,
    IMMUNE,
    INSPIRE,
    JOUST,
    MIND_CONTROL_EFFECT,
    OVERLOAD,
    POISON,
    RESTORE_HEALTH,
    RETURN_EFFECT,
    SECRET,
    SHUFFLE_INTO_DECK,
    SILENCE,
    SPELL_DAMAGE,
    STEALTH,
    SUMMON,
    TAUNT,
    TAKE_CONTROL,
    TRANSFORM,
    TRIGGERED_EFFECT,
    WINDFURY,
  }
}
