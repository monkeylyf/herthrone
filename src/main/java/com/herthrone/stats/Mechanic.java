package com.herthrone.stats;

import com.google.common.collect.ImmutableMap;
import com.herthrone.configuration.MechanicConfig;

/**
 * Created by yifengliu on 5/10/16.
 */
public class Mechanic {

  private final ImmutableMap<String, MechanicConfig> mechanics;

  public Mechanic(final ImmutableMap<String, MechanicConfig> mechanics) {
    this.mechanics = mechanics;
  }

  public boolean contains(final String mechanicName) {
    return mechanics.containsKey(mechanicName);
  }

  public MechanicConfig get(final String mechanicName) {
    return mechanics.get(mechanicName);
  }
}
