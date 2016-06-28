package com.herthrone.game;

import com.herthrone.base.Creature;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.helper.RandomMinionGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifengliu on 5/18/16.
 */
public class Target {

  public final TargetConfig config;
  public final int index;

  public Target(final TargetConfig config, final int index) {
    this.index = index;
    this.config = config;
  }

  /**
   * TODO: This is shitty code. redo when I am sober.
   *
   * @param config
   * @param battlefield
   * @return
   */
  public static List<Target> scanTargets(final TargetConfig config, final Battlefield battlefield) {
    List<Target> targets = new ArrayList<>();

    switch (config.scope) {
      case OWN:
        targets.addAll(scanTarget(config, battlefield.mySide));
        break;
      case OPPONENT:
        targets.addAll(scanTarget(config, battlefield.opponentSide));
        break;
      case ALL:
        targets.addAll(scanTarget(config, battlefield.mySide));
        targets.addAll(scanTarget(config, battlefield.opponentSide));
        break;
      default:
        throw new RuntimeException("Unknown scope: " + config.scope.toString());
    }
    return targets;
  }

  private static List<Target> scanTarget(final TargetConfig config, final Side side) {
    List<Target> targets = new ArrayList<>();
    switch (config.type) {
      case HERO:
        targets.add(new Target(config, -1));
        break;
      case MINION:
        for (int i = 0; i < side.board.size(); ++i) {
          targets.add(new Target(config, i));
        }
        break;
      case ALL:
        targets.add(new Target(config, -1));
        for (int i = 0; i < side.board.size(); ++i) {
          targets.add(new Target(config, i));
        }
        break;
    }

    return targets;
  }

  public static Creature getRandomTarget(final TargetConfig config, final Side side) {
    // TODO: redo this class.
    List<Target> targetPool = scanTargets(config, side, side.getOpponentSide());
    Target randomTarget = RandomMinionGenerator.randomOne(targetPool);
    return side.board.get(randomTarget.index);
  }

  public static List<Target> scanTargets(final TargetConfig config, final Side mySide, final Side opponentSide) {
    List<Target> targets = new ArrayList<>();

    switch (config.scope) {
      case OWN:
        targets.addAll(scanTarget(config, mySide));
        break;
      case OPPONENT:
        targets.addAll(scanTarget(config, opponentSide));
        break;
      case ALL:
        targets.addAll(scanTarget(config, mySide));
        targets.addAll(scanTarget(config, opponentSide));
        break;
      default:
        throw new RuntimeException("Unknown scope: " + config.scope.toString());
    }
    return targets;
  }

  public Creature toTargetCreature(final Battlefield battlefield) {
    switch (config.scope) {
      case OWN:
        return toTargetCreature(battlefield.mySide);
      case OPPONENT:
        return toTargetCreature(battlefield.opponentSide);
      default:
        throw new RuntimeException("Unknown scope: " + config.scope.toString());
    }
  }

  private Creature toTargetCreature(final Side side) {
    switch (config.type) {
      case HERO:
        return side.hero;
      case MINION:
        return side.board.get(index);
      case ALL:
        return (index == -1) ? side.hero : side.board.get(index);
      default:
        throw new RuntimeException("Unknown type: " + config.type.toString());
    }
  }
}
