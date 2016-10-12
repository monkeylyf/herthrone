package com.herthrone.helper;

import com.google.common.base.Preconditions;
import com.herthrone.base.Creature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

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
        .map(Creature::cardName)
        .collect(Collectors.toSet());
    final List<String> candidates = pool.stream()
        .filter(c -> !exceptionCreatureNames.contains(c))
        .collect(Collectors.toList());
    return randomOne(candidates);
  }

  public static <T> T randomOne(final List<T> elements) {
    final int index = random.nextInt(elements.size());
    return elements.get(index);
  }

  public static <T> List<T> randomN(final List<T> elements, final int n) {
    if (elements.size() == n) {
      return elements;
    }
    Preconditions.checkArgument(n < elements.size());
    final List<T> copied = new ArrayList<T>(elements);
    Collections.shuffle(copied);
    return copied.subList(0, n);
  }

}
