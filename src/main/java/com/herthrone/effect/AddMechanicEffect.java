package com.herthrone.effect;

import com.google.common.base.Preconditions;
import com.herthrone.base.Effect;
import com.herthrone.base.Mechanic;
import com.herthrone.base.Minion;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.ConstMechanic;

public class AddMechanicEffect implements Effect {

  private final Mechanic.StaticMechanic staticMechanic;
  private final ConstMechanic mechanic;

  public AddMechanicEffect(final ConstMechanic mechanic,
                           final Mechanic.StaticMechanic staticMechanic) {
    this.staticMechanic = staticMechanic;
    this.mechanic = mechanic;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.ADD_MECHANIC;
  }

  @Override
  public void act() {
    staticMechanic.booleanMechanics().initialize(mechanic);
    // Windfury is special case.
    // If a minion was just played onto the board, this minion can not move even windfury is added.
    // If a minion has already attacked, by adding windfury it can attack one more time.
    // If a minion hasn't attack, by adding windfury it can attack twice.
    if (mechanic.equals(ConstMechanic.WINDFURY)) {
      Preconditions.checkArgument(staticMechanic instanceof Minion, "Expects Minion");
      final Minion minion = (Minion) staticMechanic;
      minion.attackMovePoints().getPermanentBuff().increase(1);
      if (minion.attackMovePoints().getTemporaryBuff().value() == -1) {
        minion.attackMovePoints().getTemporaryBuff().increase(-1);
      }
    }
  }
}
