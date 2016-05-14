package com.herthrone.configuration;

import com.herthrone.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yifeng on 4/12/16.
 */
public class SpellConfig implements BaseConfig<Constant.Spell> {

  private final Constant.Spell name;
  private final Constant.Clazz className;
  //private final Constant.Type type;
  private final int crystal;
  private final List<EffectConfig> effects;

  public SpellConfig(Map map) {
    this.name = Constant.Spell.valueOf(Constant.upperCaseValue(map, "name"));
    this.className = Constant.Clazz.valueOf(Constant.upperCaseValue(map, "class"));
    //this.type = Constant.Type.valueOf(Constant.upperCaseValue(map, "type"));
    this.crystal = (int) map.get("crystal");
    this.effects = new ArrayList<>();

    List<Object> actions = (List) map.get("actions");
    for (Object action : actions) {
      Map actionMap = (Map) action;
      EffectConfig config = new EffectConfig(actionMap);
      this.effects.add(config);
    }
  }


  public List<EffectConfig> getEffects() {
    return effects;
  }

  @Override
  public Constant.Spell getName() {
    return this.name;
  }

  @Override
  public Constant.Clazz getClassName() {
    return this.className;
  }

  @Override
  public Constant.Type getType() {
    return Constant.Type.SPELL;
  }

  @Override
  public int getCrystal() {
    return this.crystal;
  }
}
