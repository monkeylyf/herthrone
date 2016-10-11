package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.object.ActiveMechanics;

import java.util.List;
import java.util.Map;

public class AddTriggeringMechanicEffect implements Effect {

  private final ActiveMechanics activeMechanics;
  private final Map<ConstTrigger, List<MechanicConfig>> mechanicsMap;

  public AddTriggeringMechanicEffect(final ActiveMechanics activeMechanics,
                                     final Map<ConstTrigger, List<MechanicConfig>> mechanicsMap) {
    this.activeMechanics = activeMechanics;
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
      if (activeMechanics.has(trigger)) {
        activeMechanics.get(trigger).addAll(mechanicConfigs);
      } else {
        mechanicConfigs.forEach(config -> activeMechanics.update(trigger, config));
      }
    }
  }
}
