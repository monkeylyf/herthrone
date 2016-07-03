package com.herthrone.factory;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Spell;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.EffectConfig;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;
import com.herthrone.game.Binder;
import com.herthrone.object.ValueAttribute;

import java.util.List;
import java.util.Map;

/**
 * Created by yifeng on 4/14/16.
 */
public class SpellFactory {

  public static Spell create(final ConstSpell spell) {
    SpellConfig config = ConfigLoader.getSpellConfigByName(spell);
    return create(config.name, config.displayName, config.className,
        config.crystal, config.type, config.targetConfigOptional, config.effects);
  }

  static Spell create(final ConstSpell name, final String displayName, final ConstClass className,
                      final int crystal, final ConstType type,
                      final Optional<TargetConfig> targetConfig, final List<EffectConfig> effects) {
    return new Spell() {

      private final ValueAttribute crystalManaCostAttr = new ValueAttribute(crystal);
      private final Binder binder = new Binder();

      @Override
      public Map<String, String> view() {
        return ImmutableMap.<String, String>builder()
            .put(Constant.CARD_NAME, cardName())
            .put(Constant.CRYSTAL, manaCost().toString())
            .put(Constant.TYPE, type().toString()).build();
      }

      @Override
      public String cardName() {
        return name.toString();
      }

      @Override
      public String displayName() {
        return displayName;
      }

      @Override
      public ConstType type() {
        return type;
      }

      @Override
      public ConstClass className() {
        return className;
      }

      @Override
      public ValueAttribute manaCost() {
        return crystalManaCostAttr;
      }

      @Override
      public boolean isCollectible() {
        return true;
      }

      @Override
      public Binder binder() {
        return binder;
      }

      @Override
      public Optional<TargetConfig> getTargetConfig() {
        return targetConfig;
      }

      @Override
      public List<EffectConfig> getEffects() {
        return effects;
      }
    };
  }

}
