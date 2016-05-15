package com.herthrone.configuration;

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
public class SpellConfig implements BaseConfig<ConstSpell> {

  private final ConstSpell name;
  private final ConstClass className;
  //private final Constant.Type type;
  private final int crystal;
  private final List<EffectConfig> effects;

  public SpellConfig(Map map) {
    this.name = ConstSpell.valueOf(Constant.upperCaseValue(map, "name"));
    this.className = ConstClass.valueOf(Constant.upperCaseValue(map, "class"));
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
  public ConstSpell getName() {
    return this.name;
  }

  @Override
  public ConstClass getClassName() {
    return this.className;
  }

  @Override
  public ConstType getType() {
    return ConstType.SPELL;
  }

  @Override
  public int getCrystal() {
    return this.crystal;
  }
}
