package com.herthrone.factory;

import com.herthrone.base.Creature;
import com.herthrone.base.Destroyable;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.game.Side;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yifengliu on 7/10/16.
 */
public class TargetFactory {

  private static final Logger logger = Logger.getLogger(TargetFactory.class.getName());

  static List<Creature> getProperTargets(final TargetConfig targetConfig, final Side side,
                                         final Creature caster) {
    switch (targetConfig.scope) {
      case OWN:
        return getProperTargetsBySide(targetConfig, side, caster);
      case OPPONENT:
        return getProperTargetsBySide(targetConfig, side.getOpponentSide(), caster);
      case ALL:
        final List<Creature> targets = getProperTargetsBySide(targetConfig, side, caster);
        targets.addAll(getProperTargetsBySide(targetConfig, side.getOpponentSide(), caster));
        return targets;
      default:
        throw new RuntimeException("Unknown scope: " + targetConfig.scope);
    }
  }

  private static List<Creature> getProperTargetsBySide(final TargetConfig targetConfig,
                                                       final Side side, final Creature caster) {
    switch (targetConfig.type) {
      case HERO:
        return Arrays.asList(side.hero);
      case MINION:
        return side.board.stream().sorted(
            EffectFactory.compareBySequenceId).collect(Collectors.toList());
      case ALL:
        final List<Creature> targets = side.board.stream().sorted(
            EffectFactory.compareBySequenceId).collect(Collectors.toList());
        targets.add(side.hero);
        return targets;
      default:
        return Arrays.asList(caster);
    }
  }

  static List<Destroyable> getDestroyablesBySide(final TargetConfig target, final Side side) {
    switch (target.type) {
      case MINION:
        return side.board.stream().collect(Collectors.toList());
      case WEAPON:
        return (side.hero.getWeapon().isPresent()) ?
          Arrays.asList(side.hero.getWeapon().get()) : Collections.emptyList();
      case ALL:
        final List<Destroyable> destroyables = side.board.stream().collect(Collectors.toList());
        if (side.hero.getWeapon().isPresent()) {
          destroyables.add(side.hero.getWeapon().get());
        }
        return destroyables;
      default:
        throw new RuntimeException("Unknown type: " + target.type);
    }
  }

  static List<Destroyable> getDestroyables(final TargetConfig target, final Side side) {
    switch (target.scope) {
      case OWN:
        return getDestroyablesBySide(target, side);
      case OPPONENT:
        return getDestroyablesBySide(target, side.getOpponentSide());
      case ALL:
        final List<Destroyable> targets = getDestroyablesBySide(target, side);
        targets.addAll(getDestroyablesBySide(target, side.getOpponentSide()));
        return targets;
      default:
        throw new RuntimeException("Unknown scope: " + target.scope);
    }
  }

  public static List<Side> getSide(final TargetConfig target, final Side side) {
    switch (target.scope) {
      case OWN:
        return Arrays.asList(side);
      case OPPONENT:
        return Arrays.asList(side.getOpponentSide());
      case ALL:
        return Arrays.asList(side, side.getOpponentSide());
      default:
        throw new RuntimeException("Unknown target scope: " + target.scope);
    }
  }
}
