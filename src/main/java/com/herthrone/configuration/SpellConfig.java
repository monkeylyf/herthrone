package com.herthrone.configuration;

import com.google.common.base.Optional;
import com.herthrone.base.Config;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class SpellConfig implements Config<ConstSpell> {

  private final ConstSpell name;
  private final ConstClass className;
  //private final Constant.Type type;
  private final int crystal;
  private final List<EffectConfig> effects;
  private final Optional<TargetConfig> target;

  public SpellConfig(Map map) {
    this.name = ConstSpell.valueOf(Constant.upperCaseValue(map, "name"));
    this.className = ConstClass.valueOf(Constant.upperCaseValue(map, "class"));
    // TODO: hero_power and spell are sharing the same concept as Spell. Maybe need to
    // be refactored later on? or it's not important...
    //this.type = Constant.Type.valueOf(Constant.upperCaseValue(map, "type"));
    this.crystal = (int) map.get("crystal");
    this.effects = new ArrayList<>();

    List<Object> actions = (List) map.get("actions");
    for (Object action : actions) {
      Map actionMap = (Map) action;
      EffectConfig config = new EffectConfig(actionMap);
      effects.add(config);
    }

    this.target = (map.containsKey("target")) ? Optional.of(new TargetConfig((Map) map.get("target"))) : Optional.absent();
    //this.effects = ((List) map.get("actions")).stream().map(map -> new EffectConfig((Map) map)).collect(Collectors.toList());
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
