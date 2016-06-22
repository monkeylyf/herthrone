package com.herthrone.factory;

import com.google.common.base.Preconditions;
import com.herthrone.action.PhysicalDamageEffect;
import com.herthrone.base.Creature;
import com.herthrone.base.Effect;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.game.Battlefield;
import com.herthrone.game.Side;
import com.herthrone.helper.RandomMinionGenerator;
import org.apache.log4j.Logger;

/**
 * Created by yifeng on 4/20/16.
 */
public class AttackFactory {

  static Logger logger = Logger.getLogger(AttackFactory.class.getName());

  private final Battlefield battlefield;

  public AttackFactory(final Battlefield battlefield) {
    this.battlefield = battlefield;
  }

  public void getPhysicalDamageAction(final Creature attacker, final Creature attackee) {
    Effect effect;
    if (attacker.getBooleanMechanics().has(ConstMechanic.FORGETFUL)) {
      final Side opponentSide = battlefield.getSideCreatureIsOn(attackee);
      effect = getForgetfulPhysicalDamageAction(attacker, attackee, opponentSide);
    } else {
      effect = new PhysicalDamageEffect(attacker, attackee);
    }
    battlefield.getEffectQueue().enqueue(effect);
  }

  private Effect getForgetfulPhysicalDamageAction(final Creature attacker, final Creature attackee,
                                                  final Side side) {
    final boolean isForgetfulToPickNewTarget = RandomMinionGenerator.getBool();

    if (isForgetfulToPickNewTarget) {
      logger.debug("Forgetful triggered");
      final Creature substituteAttackee = RandomMinionGenerator.randomExcept(
          side.allCreatures(), attackee);
      logger.debug(String.format("Change attackee from %s to %s",
          attackee.toString(), substituteAttackee.toString()));
      Preconditions.checkArgument(substituteAttackee != attackee);
      return new PhysicalDamageEffect(attacker, substituteAttackee);
    } else {
      logger.debug("Forgetful not triggered");
      return new PhysicalDamageEffect(attacker, attackee);
    }
  }
}