package com.herthrone.object;

import com.herthrone.configuration.MechanicConfig;
import com.herthrone.constant.ConstMechanic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yifengliu on 6/14/16.
 */
public class EffectMechanics {

  private final Map<ConstMechanic, List<MechanicConfig>> mechanics;

  public EffectMechanics(final Map<ConstMechanic, List<MechanicConfig>> mechanics) {
    this.mechanics = new HashMap<>();

    ConstMechanic.getEffectMechanics().stream().forEach(mechanic -> {
      if (mechanics.containsKey(mechanic)) {
        this.mechanics.put(mechanic, mechanics.get(mechanic));
      }
    });
  }

  public boolean has(final ConstMechanic mechanic) {
    return mechanics.containsKey(mechanic);
  }

  public List<MechanicConfig> get(final ConstMechanic mechanic) {
    return mechanics.containsKey(mechanic) ? mechanics.get(mechanic) : Collections.emptyList();
  }

  @Override
  public String toString() {
    return mechanics.toString();
  }
}
