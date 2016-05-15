package com.herthrone.configuration;

import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.Constant;

import java.util.List;
import java.util.Map;

/**
 * Created by yifeng on 4/18/16.
 */
public class EffectConfig {

  private final ConstEffectType effect;
  private final String type;
  private final int value;
  private final List<String> target;
  private final boolean unique;
  private final boolean permanent;

  public EffectConfig(Map map) {
    this.effect = ConstEffectType.valueOf(Constant.upperCaseValue(map, "effect"));
    this.type = (String) map.get("type");
    this.value = (int) map.get("value");
    this.target = (List) map.get("target");
    this.unique = (map.containsKey("unique")) ? (boolean) map.get("unique") : false;
    this.permanent = (map.containsKey("permanent")) ? (boolean) map.get("permanent") : false;
  }

  public ConstEffectType getEffect() {
    return this.effect;
  }

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

  public boolean isPermanent() {
    return this.permanent;
  }

  public String toString() {
    return String.format("Effect <%s> Type <%s> Value <%d> Targets: <%s>", this.effect, this.type, this.value, this.target);
  }
}