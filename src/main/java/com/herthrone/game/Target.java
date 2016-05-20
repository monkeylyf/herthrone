package com.herthrone.game;

import com.herthrone.base.Minion;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstTarget;

import java.util.ArrayList;
import java.util.Collections;
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

  public Minion toTarget(final Battlefield battlefield) {
    switch (config.scope) {
      case OWN:
        return toTarget(battlefield.mySide);
      case ENEMY:
        return toTarget(battlefield.opponentSide);
      default:
        throw new RuntimeException("Unknown scope: " + config.scope.toString());
    }
  }

  private Minion toTarget(final Side side) {
    switch (config.type) {
      case HERO:
        return side.hero;
      case MINION:
        return side.board.get(index);
      case CREATURE:
        return (index == -1) ? side.hero : side.board.get(index);
      default:
        throw new RuntimeException("Unknown type: " + config.type.toString());
    }
  }

  /**
   * TODO: This is shitty code. redo when I am sober.
   * @param config
   * @param battlefield
   * @return
   */
  public static List<Target> scanTargets(final TargetConfig config, final Battlefield battlefield) {
    List<Target> targets = new ArrayList<>();

    switch (config.scope) {
      case OWN:
        targets.addAll(scanTarget(config, battlefield.mySide)); break;
      case ENEMY:
        targets.addAll(scanTarget(config, battlefield.opponentSide)); break;
      case ALL:
        targets.addAll(scanTarget(config, battlefield.mySide));
        targets.addAll(scanTarget(config, battlefield.opponentSide)); break;
      default:
        throw new RuntimeException("Unknow scope: " + config.scope.toString());
    }
    return targets;
  }

  private static List<Target> scanTarget(final TargetConfig config, final Side side) {
    List<Target> targets = new ArrayList<>();
    switch (config.type) {
      case HERO:
        targets.add(new Target(config, -1)); break;
      case MINION:
        for (int i = 0; i < side.board.size(); ++i) {
          targets.add(new Target(config, i));
        }
        break;
      case CREATURE:
        targets.add(new Target(config, -1));
        for (int i = 0; i < side.board.size(); ++i) {
          targets.add(new Target(config, i));
        }
        break;
    }

    return targets;
  }

}
