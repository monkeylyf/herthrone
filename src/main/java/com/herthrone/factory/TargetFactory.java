package com.herthrone.factory;

import com.herthrone.base.Card;
import com.herthrone.base.Creature;
import com.herthrone.base.Destroyable;
import com.herthrone.base.Mechanic;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstTarget;
import com.herthrone.constant.ConstType;
import com.herthrone.game.Container;
import com.herthrone.game.Side;
import com.herthrone.helper.RandomMinionGenerator;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TargetFactory {

  private static final Logger logger = Logger.getLogger(TargetFactory.class.getName());

  static Stream<Creature> getTarget(final Mechanic.ActiveMechanic triggerrer,
                                    final Creature selectedTarget, final Side side,
                                    final TargetConfig targetConfig) {
    if (targetConfig.scope.equals(ConstTarget.NOT_PROVIDED)) {
      return Collections.singletonList(selectedTarget).stream();
    }
    logger.debug("Trigger with configured targets: " + targetConfig);
    return TargetFactory.getTargets(triggerrer, targetConfig, side).stream()
        .filter(target -> !targetConfig.type.equals(ConstType.OTHER) || target != selectedTarget);
  }

  public static List<Creature> getTargets(final Mechanic.ActiveMechanic triggerrer,
                                          final TargetConfig targetConfig, final Side side) {
    final List<Creature> candidates = new ArrayList<>();
    switch (targetConfig.scope) {
      case OWN:
        candidates.addAll(getTargetsBySide(triggerrer, targetConfig, side));
        break;
      case FOE:
        candidates.addAll(getTargetsBySide(triggerrer, targetConfig, side.getFoeSide()));
        break;
      case ALL:
        candidates.addAll(getTargetsBySide(triggerrer, targetConfig, side));
        candidates.addAll(getTargetsBySide(triggerrer, targetConfig, side.getFoeSide()));
        break;
      default:
        throw new RuntimeException("Unknown scope: " + targetConfig.scope);
    }

    return targetConfig.isRandom ?
        Collections.singletonList(RandomMinionGenerator.randomOne(candidates)) :
        candidates;
  }

  private static List<Creature> getTargetsBySide(final Mechanic.ActiveMechanic triggerrer,
                                                 final TargetConfig targetConfig, final Side side) {
    switch (targetConfig.type) {
      case HAND:
        return Collections.singletonList(side.hero);
      case HERO:
        return Collections.singletonList(side.hero);
      case MINION:
        final List<Creature> sortedMinions = side.board.stream()
            .sorted(EffectFactory.compareBySequenceId)
            .collect(Collectors.toList());
        if (targetConfig.randomTarget.isPresent()) {
          // If value is set in config, select randomly.
          final int n = targetConfig.randomTarget.getAsInt();
          if (n > side.board.size()) {
            throw new RuntimeException("Requires " + n + " but there is " + side.board);
          }
          logger.debug("Randomly select " + n + " minions on board");
          return RandomMinionGenerator.randomN(sortedMinions, n);
        } else {
          return sortedMinions;
        }
      case WEAPON:
        return Collections.singletonList(side.hero);
      case DECK:
        return Collections.singletonList(side.hero);
      case ALL:
        final List<Creature> targets = side.board.stream().sorted(
            EffectFactory.compareBySequenceId).collect(Collectors.toList());
        targets.add(side.hero);
        return targets;
      case TOTEM:
        return side.board.stream()
            .filter(minion -> minion.type().equals(ConstType.TOTEM)).collect(Collectors.toList());
      case OTHER:
        final List<Creature> allTargets = side.board.stream().sorted(
            EffectFactory.compareBySequenceId).collect(Collectors.toList());
        allTargets.add(side.hero);
        return allTargets;
      case SELF:
        return Collections.singletonList((Creature) triggerrer);
      default:
        return Collections.singletonList(side.hero);
        //throw new NoTargetFoundException("Unsupported target type: " + targetConfig.type);
    }
  }

  static List<Destroyable> getDestroyablesBySide(final TargetConfig target, final Side side) {
    switch (target.type) {
      case MINION:
        return side.board.stream().collect(Collectors.toList());
      case WEAPON:
        return (side.hero.getWeapon().isPresent()) ?
          Collections.singletonList(side.hero.getWeapon().get()) : Collections.emptyList();
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

  public static List<Side> getSide(final TargetConfig target, final Side side) {
    switch (target.scope) {
      case OWN:
        return Collections.singletonList(side);
      case FOE:
        return Collections.singletonList(side.getFoeSide());
      case ALL:
        return Arrays.asList(side, side.getFoeSide());
      default:
        throw new RuntimeException("Unknown target scope: " + target.scope);
    }
  }

  public static Container<Card> getContainer(final TargetConfig target, final Side side) {
    switch (target.type) {
      case HAND:
        return side.hand;
      case DECK:
        return side.deck;
      default:
        throw new RuntimeException("Unsupported " + target.type + " type for generate effect");
    }
  }

}
