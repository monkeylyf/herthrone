package com.herthrone.configuration;

import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.Constant;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yifeng on 4/18/16.
 */
public class EffectConfig {

  private final ConstEffectType effect;
  private final String type;
  private final int value;
  private final boolean unique;
  private final boolean permanent;
  private final TargetConfig target;
  private final List<String> choices;

  public EffectConfig(Map map) {
    this.effect = ConstEffectType.valueOf(Constant.upperCaseValue(map, "effect"));
    this.type = (String) map.get("type");
    this.value = (int) map.get("value");
    this.unique = (map.containsKey("unique")) ? (boolean) map.get("unique") : false;
    this.permanent = (map.containsKey("permanent")) ? (boolean) map.get("permanent") : false;
    this.target = new TargetConfig((Map) map.get("target"));
    this.choices = (map.containsKey("choices")) ? (List) map.get("choices") : Collections.emptyList();
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

  public TargetConfig getTarget() {
    return target;
  }

  public boolean isUnique() {
    return unique;
  }

  public boolean isPermanent() {
    return this.permanent;
  }

  public List<String> getChoices() {
    return this.choices;
  }

  public String toString() {
    return String.format("Effect <%s> Type <%s> Value <%d> Targets: <%s>", this.effect, this.type, this.value, this.target);
  }
}