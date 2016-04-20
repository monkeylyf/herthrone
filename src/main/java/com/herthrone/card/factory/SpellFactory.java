package com.herthrone.card.factory;

import com.herthrone.base.Attribute;
import com.herthrone.base.Spell;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.exception.SpellNotFoundException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class SpellFactory {

  private final EffectFactory effectFactory;

  public SpellFactory(final EffectFactory effectFactory) {
    this.effectFactory = effectFactory;
  }

  public Spell createSpellByName(final String name) throws FileNotFoundException, SpellNotFoundException {
    SpellConfig config = ConfigLoader.getSpellConfigByName(name);
    List<ActionFactory> actionFactories = new ArrayList<>();
    for (EffectConfig effectConfig : config.getEffects()) {
      ActionFactory actionFactory = this.effectFactory.getActionFactoryByConfig(effectConfig);
      actionFactories.add(actionFactory);
    }
    return createSpell(name, config.getClassName(), config.getCrystal(), config.getType(), actionFactories);
  }

  public Spell createSpell(final String name, final String className, final int crystal, final String type, final List<ActionFactory> actionFactories) {
    return new Spell() {

      private final Attribute crystalManaCostAttr = new Attribute(crystal);

      @Override
      public List<ActionFactory> getActionFactories() {
        return this.getActionFactories();
      }

      @Override
      public String getCardName() {
        return name;
      }

      @Override
      public String getType() {
        return type;
      }

      @Override
      public String getClassName() {
        return className;
      }

      @Override
      public Attribute getCrystalManaCost() {
        return this.crystalManaCostAttr;
      }
    };
  }

}
