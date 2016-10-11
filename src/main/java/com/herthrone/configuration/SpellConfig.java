package com.herthrone.configuration;

import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.service.Spell;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpellConfig extends ConfigLoader.AbstractConfig<ConstSpell> {

  private static final String MECHANICS = "mechanics";
  private static final String SELECT = "select";
  public final List<MechanicConfig> effects;
  public final ConstType type = ConstType.SPELL;
  public final TargetConfig selectTargetConfig;

  @SuppressWarnings("unchecked")
  SpellConfig(final Map map) {
    super(map);
    this.selectTargetConfig = (map.containsKey(SELECT)) ?
        new TargetConfig((Map) map.get(SELECT)) : TargetConfig.getDefaultTargetConfig();
    this.effects = ((List<Object>) map.get(MECHANICS)).stream()
        .map(object -> (Map) object)
        .map(MechanicConfig::new)
        .collect(Collectors.toList());
  }

  @Override
  protected ConstSpell loadName(final String name) {
    return ConstSpell.valueOf(name.toUpperCase());
  }

  public Spell toSpellProto() {
    return Spell.newBuilder()
        .setName(name.toString())
        .setDisplayName(displayName)
        .setClassType(className.toString())
        .setCrystal(crystal)
        .setDescription(description)
        .addAllMechanics(
            effects.stream()
                .map(MechanicConfig::toMechanicProto)
                .collect(Collectors.toList()))
        .build();
  }
}
