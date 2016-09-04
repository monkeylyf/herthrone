package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.object.TriggeringMechanics;

import java.util.List;
import java.util.Map;

public class AddTriggeringMechanicEffect implements Effect {

  private final TriggeringMechanics triggeringMechanics;
  private final Map<ConstTrigger, List<MechanicConfig>> mechanicsMap;

  public AddTriggeringMechanicEffect(final TriggeringMechanics triggeringMechanics,
                                     final Map<ConstTrigger, List<MechanicConfig>> mechanicsMap) {
    this.triggeringMechanics = triggeringMechanics;
    this.mechanicsMap = mechanicsMap;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.ADD_MECHANIC_TRIGGER;
  }

  @Override
  public void act() {
    for (Map.Entry<ConstTrigger, List<MechanicConfig>> entry : mechanicsMap.entrySet()) {
      final ConstTrigger trigger = entry.getKey();
      final List<MechanicConfig> mechanicConfigs = entry.getValue();
      if (triggeringMechanics.has(trigger)) {
        triggeringMechanics.get(trigger).addAll(mechanicConfigs);
      } else {
        mechanicConfigs.forEach(config -> triggeringMechanics.update(trigger, config));
      }
    }
  }
}
