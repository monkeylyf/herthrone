package com.herthrone.helper;

import com.google.common.base.Preconditions;
import com.herthrone.base.Creature;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.game.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by yifengliu on 6/20/16.
 */
public class RandomMinionGenerator {

  private static Random random = new Random();

  public static boolean getBool() {
    return random.nextBoolean();
  }

  public static Creature randomExcept(final List<Creature> creatures, final Creature exceptionCreature) {
    Preconditions.checkArgument(creatures.size() > 0, "Expects non-empty list");
    Preconditions.checkArgument(creatures.contains(exceptionCreature), "Invalid creature pool");
    final int size = creatures.size();
    Creature candidate;
    do {
      candidate = creatures.get(random.nextInt(size));
    } while (candidate == exceptionCreature);

    return candidate;
  }

  public static String randomUnique(final List<String> pool, final List<Creature> exceptionCreatures) {
    final Set<String> exceptionCreatureNames = exceptionCreatures.stream()
        .map(creature -> creature.getCardName())
        .collect(Collectors.toSet());
    List<String> candidates = pool.stream()
        .filter(c -> !exceptionCreatureNames.contains(c))
        .collect(Collectors.toList());
    return randomOne(candidates);
  }

  public static <T> T randomOne(final List<T> creatures) {
    final int index = random.nextInt(creatures.size());
    return creatures.get(index);
  }


  public static Creature randomCreature(final TargetConfig config, final Side side) {
    List<Creature> creatureCandidatePool = new ArrayList<>();
    switch (config.scope) {
      case OWN:
        creatureCandidatePool.addAll(getCandidatePoolOnOneSide(config, side));
        break;
      case OPPONENT:
        creatureCandidatePool.addAll(getCandidatePoolOnOneSide(config, side.getOpponentSide()));
        break;
      case ALL:
        creatureCandidatePool.addAll(getCandidatePoolOnOneSide(config, side));
        creatureCandidatePool.addAll(getCandidatePoolOnOneSide(config, side.getOpponentSide()));
        break;
      default:
        throw new RuntimeException("Unknown target scope: " + config.type);
    }

    return randomOne(creatureCandidatePool);
  }

  private static List<Creature> getCandidatePoolOnOneSide(final TargetConfig config, final Side side) {
    List<Creature> creaturePool = new ArrayList<>();
    switch (config.type) {
      case HERO:
        creaturePool.add(side.hero);
        break;
      case MINION:
        creaturePool.addAll(side.board.asList());
        break;
      case ALL:
        creaturePool.add(side.hero);
        creaturePool.addAll(side.board.asList());
        break;
      default:
        throw new RuntimeException("Unknown target type: " + config.type);
    }

    return creaturePool;
  }
}
