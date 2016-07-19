package com.herthrone.object;

import com.herthrone.configuration.MechanicConfig;
import com.herthrone.constant.ConstTrigger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yifengliu on 6/14/16.
 */
public class TriggeringMechanics {

  private final Map<ConstTrigger, List<MechanicConfig>> mechanics;

  public TriggeringMechanics(final Map<ConstTrigger, List<MechanicConfig>> mechanics) {
    this.mechanics = mechanics.entrySet().stream()
        .filter(entry -> !entry.getKey().equals(ConstTrigger.NO_TRIGGER))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public List<MechanicConfig> get(final ConstTrigger trigger) {
    return mechanics.containsKey(trigger) ? mechanics.get(trigger) : Collections.emptyList();
  }

  @Override
  public String toString() {
    return mechanics.toString();
  }

  public boolean has(final ConstTrigger trigger) {
    return mechanics.containsKey(trigger);
  }

  public static TriggeringMechanics create(final ConstTrigger trigger,
                                           final List<MechanicConfig> mechanicConfigs) {
    return new TriggeringMechanics(Collections.singletonMap(trigger, mechanicConfigs));
  }
}
