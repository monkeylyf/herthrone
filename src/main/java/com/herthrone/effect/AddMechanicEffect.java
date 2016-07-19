package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.base.Mechanic;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.ConstMechanic;

public class AddMechanicEffect implements Effect {

  private final Mechanic.BooleanMechanic booleanMechanic;
  private final ConstMechanic mechanic;

  public AddMechanicEffect(final ConstMechanic mechanic,
                           final Mechanic.BooleanMechanic booleanMechanic) {
    this.booleanMechanic = booleanMechanic;
    this.mechanic = mechanic;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.ADD_MECHANIC;
  }

  @Override
  public void act() {
    booleanMechanic.booleanMechanics().initialize(mechanic);
  }
}
