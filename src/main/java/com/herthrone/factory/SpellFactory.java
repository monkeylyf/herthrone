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
import com.herthrone.objects.IntAttribute;

import java.util.List;
import java.util.Map;

/**
 * Created by yifeng on 4/14/16.
 */
public class SpellFactory {

  public static Spell create(final ConstSpell spell) {
    SpellConfig config = ConfigLoader.getSpellConfigByName(spell);
    return create(
        config.getName(),
        config.getClassName(),
        config.getCrystal(),
        config.getType(),
        config.getTargetConfig(),
        config.getEffects()
    );
  }

  private static Spell create(final ConstSpell name, final ConstClass className,
                              final int crystal, final ConstType type,
                              final Optional<TargetConfig> targetConfig,
                              final List<EffectConfig> effects) {
    return new Spell() {

      private final IntAttribute crystalManaCostAttr = new IntAttribute(crystal);
      private final Binder binder = new Binder();

      @Override
      public Map<String, String> view() {
        return ImmutableMap.<String, String>builder()
            .put(Constant.CARD_NAME, getCardName())
            .put(Constant.CRYSTAL, getCrystalManaCost().toString())
            //.put(Constant.DESCRIPTION, "TODO")
            .put(Constant.TYPE, getType().toString())
            .build();
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
        return crystalManaCostAttr;
      }

      @Override
      public boolean isCollectible() {
        return true;
      }

      @Override
      public Binder getBinder() {
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

  public static Spell createHeroPowerByName(final ConstSpell heroPower) {
    SpellConfig config = ConfigLoader.getHeroPowerConfigByName(heroPower);
    return create(
        config.getName(),
        config.getClassName(),
        config.getCrystal(),
        config.getType(),
        config.getTargetConfig(),
        config.getEffects()
    );
  }

}
