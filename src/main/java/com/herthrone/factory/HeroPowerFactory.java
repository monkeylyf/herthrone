package com.herthrone.factory;

import com.herthrone.base.Spell;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.constant.ConstSpell;

/**
 * Created by yifengliu on 6/30/16.
 */
public class HeroPowerFactory {

  public static Spell create(final ConstSpell heroPower) {
    final SpellConfig config = ConfigLoader.getHeroPowerConfigByName(heroPower);
    return SpellFactory.create(config.name, config.displayName, config.className, config.crystal,
        config.type, config.targetConfigOptional, config.effects);
  }
}
