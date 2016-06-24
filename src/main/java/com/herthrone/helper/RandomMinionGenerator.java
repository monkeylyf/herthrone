package com.herthrone.helper;

import com.google.common.base.Preconditions;
import com.herthrone.base.Creature;

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

  public static Creature randomExcept(final List<Creature> creatures, final Creature
      exceptionCreature) {
    Preconditions.checkArgument(creatures.size() > 0, "Expects non-empty list");
    Preconditions.checkArgument(creatures.contains(exceptionCreature), "Invalid creature pool");
    final int size = creatures.size();
    Creature candidate;
    do {
      candidate = creatures.get(random.nextInt(size));
    } while (candidate == exceptionCreature);

    return candidate;
  }

  public static String randomUnique(final List<String> pool, final List<Creature>
      exceptionCreatures) {
    final Set<String> exceptionCreatureNames = exceptionCreatures.stream().map(creature ->
        creature.getCardName()).collect(Collectors.toSet());
    List<String> candidates = pool.stream()
        .filter(c -> !exceptionCreatureNames.contains(c)).collect(Collectors.toList());
    return randomOne(candidates);
  }

  public static <T> T randomOne(final List<T> creatures) {
    final int index = random.nextInt(creatures.size());
    return creatures.get(index);
  }
}
