package com.herthrone.configuration;

import com.google.common.base.Optional;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpellConfig extends ConfigLoader.AbstractConfig<ConstSpell> {

  private static final String MECHANICS = "mechanics";
  private static final String TARGET = "single_target";
  public final List<MechanicConfig> effects;
  public final Optional<TargetConfig> singleTargetConfigOptional;
  public final ConstType type = ConstType.SPELL;

  @SuppressWarnings("unchecked")
  SpellConfig(final Map map) {
    super(map);
    this.singleTargetConfigOptional = (map.containsKey(TARGET)) ?
        Optional.of(new TargetConfig((Map) map.get(TARGET))) : Optional.absent();
    this.effects = ((List<Object>) map.get(MECHANICS)).stream()
        .map(object -> (Map) object)
        .map(MechanicConfig::new)
        .collect(Collectors.toList());
  }

  @Override
  protected ConstSpell loadName(final String name) {
    return ConstSpell.valueOf(name.toUpperCase());
  }
}
