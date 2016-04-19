package com.herthrone.configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by yifeng on 4/18/16.
 */
public class EffectConfig {

  private final String effect;
  private final String type;
  private final int value;
  private final List<String> target;
  private final boolean unique;
  private final int duration;

  public EffectConfig(Map map) {
    this.effect = (String) map.get("effect");
    this.type = (String) map.get("type");
    this.value = (int) map.get("value");
    this.target = (List) map.get("target");
    this.unique = (map.containsKey("unique")) ? (boolean) map.get("unique") : false;
    this.duration = (map.containsKey("duration")) ? (int) map.get("duration") : Integer.MAX_VALUE;
  }

  public String getEffect() { return this.effect; }

  public String getType() {
    return type;
  }

  public int getValue() {
    return value;
  }

  public List<String> getTarget() {
    return target;
  }

  public boolean isUnique() {
    return unique;
  }

  public int getDuration() {
    return duration;
  }
}
