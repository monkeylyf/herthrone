package com.herthrone.configuration;

import com.google.common.base.Optional;
import com.herthrone.base.Config;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;

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
  private static final String TARGET = "target";
  private final ConstSpell name;
  private final ConstClass className;
  private final int crystal;
  private final List<EffectConfig> effects;
  private final Optional<TargetConfig> target;

  public SpellConfig(Map map) {
    this.name = ConstSpell.valueOf(Constant.upperCaseValue(map, NAME));
    this.className = ConstClass.valueOf(Constant.upperCaseValue(map, CLASS));
    this.crystal = (int) map.get(CRYSTAL);
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
  public ConstSpell getName() {
    return name;
  }

  @Override
  public ConstClass getClassName() {
    return className;
  }

  @Override
  public ConstType getType() {
    return ConstType.SPELL;
  }

  @Override
  public int getCrystal() {
    return crystal;
  }

  public Optional<TargetConfig> getTargetConfig() {
    return target;
  }
}
