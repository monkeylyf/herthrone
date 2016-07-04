package com.herthrone.object;

import com.herthrone.configuration.MechanicConfig;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstTrigger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yifengliu on 6/14/16.
 */
public class EffectMechanics {

  private final Map<ConstTrigger, List<MechanicConfig>> mechanics;

  public EffectMechanics(final Map<ConstTrigger, List<MechanicConfig>> mechanics) {
    // TODO: use lambda.
    this.mechanics = new HashMap<>();

    for (Map.Entry<ConstTrigger, List<MechanicConfig>> entry : mechanics.entrySet()) {
      if (!entry.getKey().equals(ConstTrigger.NO_TRIGGER)) {
        this.mechanics.put(entry.getKey(), entry.getValue());
      }
    }
  }

  public List<MechanicConfig> get(final ConstTrigger trigger) {
    return mechanics.containsKey(trigger) ? mechanics.get(trigger) : Collections.emptyList();
  }

  @Override
  public String toString() {
    return mechanics.toString();
  }
}
