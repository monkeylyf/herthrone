package com.herthrone.objects;

import com.google.common.base.Optional;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.constant.ConstMechanic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yifengliu on 6/14/16.
 */
public class EffectMechanics {

  private final Map<ConstMechanic, MechanicConfig> effectMechanics;

  public EffectMechanics(final Map<ConstMechanic, MechanicConfig> mechanics) {
    this.effectMechanics = new HashMap<>();

    ConstMechanic.getEffectMechanics().stream().forEach(mechanic -> {
      if (mechanics.containsKey(mechanic)) {
        effectMechanics.put(mechanic, mechanics.get(mechanic));
      }
    });
  }

  public boolean has(final ConstMechanic mechanic) {
    return effectMechanics.containsKey(mechanic);
  }

  public Optional<MechanicConfig> get(final ConstMechanic mechanic) {
    final MechanicConfig config = effectMechanics.get(mechanic);
    return Optional.fromNullable(config);
  }

  @Override
  public String toString() {
    return effectMechanics.toString();
  }
}
