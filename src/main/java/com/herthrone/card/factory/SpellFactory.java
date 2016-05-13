package com.herthrone.card.factory;

import com.herthrone.base.Spell;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.stats.IntAttribute;

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

  public Spell createSpellByName(final String name) throws FileNotFoundException {
    SpellConfig config = ConfigLoader.getSpellConfigByName(name);
    return createSpell(name, config.getClassName(), config.getCrystal(), config.getType(), config.getEffects());
  }

  public Spell createHeroPowerByName(final String name) throws FileNotFoundException {
    SpellConfig config = ConfigLoader.getHeroPowerConfigByName(name);
    return createSpell(name, config.getClassName(), config.getCrystal(), config.getType(), config.getEffects());
  }

  public Spell createSpell(final String name, final String className, final int crystal, final String type, final List<EffectConfig> effects) {
    return new Spell() {

      private final IntAttribute crystalManaCostAttr = new IntAttribute(crystal);

      @Override
      public List<EffectConfig> getEffects() {
        return effects;
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
      public IntAttribute getCrystalManaCost() {
        return this.crystalManaCostAttr;
      }

      @Override
      public boolean isCollectible() {
        return true;
      }
    };
  }

}
