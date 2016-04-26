package com.herthrone.card.factory;

import com.herthrone.stats.Attribute;
import com.herthrone.base.Spell;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.exception.SpellNotFoundException;

import java.io.FileNotFoundException;
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
    return createSpell(name, config.getClassName(), config.getCrystal(), config.getType(), config.getEffects());
  }

  public Spell createHeroPowerByName(final String name) throws FileNotFoundException, SpellNotFoundException {
    SpellConfig config = ConfigLoader.getHeroPowerConfigByName(name);
    return createSpell(name, config.getClassName(), config.getCrystal(), config.getType(), config.getEffects());
  }

  public Spell createSpell(final String name, final String className, final int crystal, final String type, final List<EffectConfig> effects) {
    return new Spell() {

      @Override
      public List<EffectConfig> getEffects() {
        return effects;
      }

      private final Attribute crystalManaCostAttr = new Attribute(crystal);

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

      @Override
      public boolean isCollectible() {
        return true;
      }
    };
  }

}
