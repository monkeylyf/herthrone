package com.herthrone.card.factory;

import com.herthrone.action.ActionFactory;
import com.herthrone.base.Attribute;
import com.herthrone.base.Battlefield;
import com.herthrone.base.Minion;
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

  private final Battlefield battlefield;

  public SpellFactory(final Battlefield battlefield) {
    this.battlefield = battlefield;
  }

  public Spell createSpell(final String name, final String className, final int crystal, final String type, final List<EffectConfig> effectConfigs) {
    return new Spell() {

      private final Attribute crystalManaCostAttr = new Attribute(crystal);

      @Override
      public List<ActionFactory> getActionFactories() {
        return null;
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

      @Override
      public void cast(Minion creature) {

      }
    };
  }

  public Spell createSpellByName(final String name) throws FileNotFoundException, SpellNotFoundException {
    SpellConfig config = ConfigLoader.getSpellConfigByName(name);
    return null;
  }
}
