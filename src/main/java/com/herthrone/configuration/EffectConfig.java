package com.herthrone.configuration;

import com.google.common.base.Objects;
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
  private final String type;  // TODO: get rid of all String.
  private final int value;
  private final boolean unique;
  private final boolean permanent;
  private final List<String> choices;
  private final TargetConfig target;

  public EffectConfig(Map map) {
    this.effect = ConstEffectType.valueOf(Constant.upperCaseValue(map, "effect"));
    this.type = (String) map.get("type");
    this.value = (int) map.get("value");
    this.permanent = (map.containsKey("permanent")) ? (boolean) map.get("permanent") : false;
    this.unique = (map.containsKey("unique")) ? (boolean) map.get("unique") : false;
    this.choices = (map.containsKey("choices")) ? (List) map.get("choices") : Collections.emptyList();
    this.target = new TargetConfig((Map) map.get("target"));
  }

  public ConstEffectType getEffect() {
    return effect;
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
    return permanent;
  }

  public List<String> getChoices() {
    return choices;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("effect", effect)
        .add("type", type)
        .add("value", value)
        .add("target", target)
        .toString();
  }
}