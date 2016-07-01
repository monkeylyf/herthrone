package com.herthrone.configuration;

import com.google.common.base.Optional;
import com.herthrone.base.Config;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;
import com.herthrone.helper.StringHelper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yifeng on 4/12/16.
 */
public class SpellConfig implements Config<ConstSpell> {

  private static final String NAME = "name";
  private static final String CLASS = "class";
  private static final String CRYSTAL = "crystal";
  private static final String MECHANICS = "mechanics";
  private static final String DISPLAY = "display";
  private static final String TARGET = "target";
  private final ConstSpell name;
  private final ConstClass className;
  private final String displayName;
  private final int crystal;
  private final List<EffectConfig> effects;
  private final Optional<TargetConfig> target;

  public SpellConfig(Map map) {
    this.name = ConstSpell.valueOf(Constant.upperCaseValue(map, NAME));
    this.className = ConstClass.valueOf(Constant.upperCaseValue(map, CLASS));
    this.crystal = (int) map.get(CRYSTAL);
    this.displayName = (map.containsKey(DISPLAY)) ?
        (String) map.get(DISPLAY) : StringHelper.lowerUnderscoreToUpperWhitespace(name);
    this.target = (map.containsKey(TARGET)) ?
        Optional.of(new TargetConfig((Map) map.get(TARGET))) : Optional.absent();
    this.effects = ((List<Object>) map.get(MECHANICS)).stream()
        .map(object -> new EffectConfig((Map) object))
        .collect(Collectors.toList());
  }

  public List<EffectConfig> getEffects() {
    return effects;
  }

  @Override
  public ConstSpell name() {
    return name;
  }

  @Override
  public String displayName() {
    return null;
  }

  @Override
  public ConstClass className() {
    return className;
  }

  @Override
  public ConstType type() {
    return ConstType.SPELL;
  }

  @Override
  public int manaCost() {
    return crystal;
  }

  public Optional<TargetConfig> getTargetConfig() {
    return target;
  }
}
