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

  private static final String TYPE = "type";
  private static final String EFFECT = "effect";
  private static final String VALUE = "value";
  private static final String PERMANENT = "permanent";
  private static final String UNIQUE = "unique";
  private static final String CHOICES = "choices";
  private static final String TARGET = "target";
  private final ConstEffectType effect;
  private final String type;  // TODO: get rid of all String.
  private final int value;
  private final boolean unique;
  private final boolean permanent;
  private final List<String> choices;
  private final TargetConfig target;

  public EffectConfig(Map map) {
    this.effect = ConstEffectType.valueOf(Constant.upperCaseValue(map, EFFECT));
    this.type = (String) map.get(TYPE);
    this.value = (int) map.get(VALUE);
    this.permanent = (map.containsKey(PERMANENT)) ? (boolean) map.get(PERMANENT) : false;
    this.unique = (map.containsKey(UNIQUE)) ? (boolean) map.get(UNIQUE) : false;
    this.choices = ((map.containsKey(CHOICES)) ? (List) map.get(CHOICES) : Collections.emptyList());
    this.target = new TargetConfig((Map) map.get(TARGET));
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
    return Objects.toStringHelper(this).add("effect", effect).add("type", type).add("value", value).add("target", target).toString();
  }
}