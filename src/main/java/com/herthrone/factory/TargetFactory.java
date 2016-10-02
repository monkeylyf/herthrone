package com.herthrone.factory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.herthrone.base.Card;
import com.herthrone.base.Creature;
import com.herthrone.base.Destroyable;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstMechanic;
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

  public static boolean isMinionTargetable(final Minion minion, final Container<Minion> board, final ConstType type) {
    if (minion.booleanMechanics().isOn(ConstMechanic.IMMUNE)) {
      return false;
    } else {
      switch (type) {
        case ATTACK:
          return isMinionTargetableByAttack(minion, board);
        case SPELL:
          return isMinionTargetableBySpell(minion, board);
        default:
          throw new RuntimeException(String.format("Unknown type %s for target", type.toString()));
      }
    }
  }

  private static boolean isMinionTargetableByAttack(final Minion minion, final Container<Minion> board) {
    // A stealth minion can not be targeted, even it is a taunt minion.
    if (minion.booleanMechanics().isOn(ConstMechanic.STEALTH)) {
      return false;
    } else if (minion.booleanMechanics().isOn(ConstMechanic.TAUNT)) {
      // A taunt minion is targetable.
      return true;
    } else {
      // If there is any other minions on the board with taunt but not stealth ability, this minion
      // cannot be targeted.
      return !board.stream()
          .anyMatch(minionOnBoard ->
              minionOnBoard.booleanMechanics().isOn(ConstMechanic.TAUNT) &&
              minionOnBoard.booleanMechanics().isOff(ConstMechanic.STEALTH));
    }
  }

  private static boolean isMinionTargetableBySpell(final Minion minion, final Container<Minion> board) {
    return !minion.booleanMechanics().isOn(ConstMechanic.ELUSIVE);
  }

  public static boolean isHeroTargetable(final Hero hero, final Container<Minion> board, final ConstType type) {
    if (hero.booleanMechanics().isOn(ConstMechanic.IMMUNE)) {
      return false;
    } else {
      switch (type) {
        case ATTACK:
          return isHeroTargetableByAttack(hero, board);
        case SPELL:
          return isHeroTargetableBySpell(hero, board);
        default:
          throw new RuntimeException(String.format("Unknown type %s for target", type.toString()));
      }
    }
  }

  private static boolean isHeroTargetableByAttack(final Hero hero, final Container<Minion> board) {
    return hero.booleanMechanics().isOn(ConstMechanic.TAUNT);
  }

  private static boolean isHeroTargetableBySpell(final Hero hero, final Container<Minion> board) {
    return true;
  }

  public static Creature getSingleTarget(final TargetConfig targetConfig, final Side side) {
    final List<Creature> targets = getProperTargets(targetConfig, side);
    Preconditions.checkArgument(targets.size() == 1);
    return targets.get(0);
  }

  static List<Creature> getProperTargets(final TargetConfig targetConfig, final Side side) {
    final List<Creature> candidates = new ArrayList<>();
    switch (targetConfig.scope) {
      case OWN:
        candidates.addAll(getProperTargetsBySide(targetConfig, side));
        break;
      case FOE:
        candidates.addAll(getProperTargetsBySide(targetConfig, side.getFoeSide()));
        break;
      case ALL:
        candidates.addAll(getProperTargetsBySide(targetConfig, side));
        candidates.addAll(getProperTargetsBySide(targetConfig, side.getFoeSide()));
        break;
      default:
        throw new RuntimeException("Unknown scope: " + targetConfig.scope);
    }

    return targetConfig.isRandom ?
        Collections.singletonList(RandomMinionGenerator.randomOne(candidates)) : candidates;
  }

  private static List<Creature> getProperTargetsBySide(final TargetConfig targetConfig,
                                                       final Side side) {
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

  static List<Destroyable> getDestroyables(final TargetConfig target, final Side side) {
    switch (target.scope) {
      case OWN:
        return getDestroyablesBySide(target, side);
      case FOE:
        return getDestroyablesBySide(target, side.getFoeSide());
      case ALL:
        final List<Destroyable> targets = getDestroyablesBySide(target, side);
        targets.addAll(getDestroyablesBySide(target, side.getFoeSide()));
        return targets;
      default:
        throw new RuntimeException("Unknown scope: " + target.scope);
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

  static Stream<Creature> getTarget(final Creature selectedTarget, final Side triggeringSide,
                                    final Optional<TargetConfig> targetConfigOptional) {
    if (targetConfigOptional.isPresent()) {
      final TargetConfig targetConfig = targetConfigOptional.get();
      logger.debug("Trigger with configured targets: " + targetConfig);
      return TargetFactory.getProperTargets(targetConfig, triggeringSide).stream()
          .filter(t -> !targetConfig.type.equals(ConstType.OTHER) || t != selectedTarget);
    } else {
      logger.debug("No target config found. Trigger with selected target");
      return Collections.singleton(selectedTarget).stream();
    }
  }
}
