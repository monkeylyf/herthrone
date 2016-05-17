package com.herthrone.card.factory;

import com.herthrone.base.Spell;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstHeroPower;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.stats.IntAttribute;

import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class SpellFactory {

  private final EffectFactory effectFactory;

  public SpellFactory(final EffectFactory effectFactory) {
    this.effectFactory = effectFactory;
  }

  public Spell createSpellByName(final ConstSpell spell) {
    SpellConfig config = ConfigLoader.getSpellConfigByName(spell);
    return createSpell(config.getName(), config.getClassName(), config.getCrystal(), config.getType(), config.getEffects());
  }

  public Spell createHeroPowerByName(final ConstHeroPower heroPower) {
    SpellConfig config = ConfigLoader.getHeroPowerConfigByName(heroPower);
    return createSpell(config.getName(), config.getClassName(), config.getCrystal(), config.getType(), config.getEffects());
  }

  public Spell createSpell(final ConstSpell name, final ConstClass className, final int crystal, final ConstType type, final List<EffectConfig> effects) {
    return new Spell() {

      private final IntAttribute crystalManaCostAttr = new IntAttribute(crystal);

      @Override
      public List<EffectConfig> getEffects() {
        return effects;
      }

      @Override
      public String getCardName() {
        return name.toString();
      }

      @Override
      public ConstType getType() {
        return type;
      }

      @Override
      public ConstClass getClassName() {
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
