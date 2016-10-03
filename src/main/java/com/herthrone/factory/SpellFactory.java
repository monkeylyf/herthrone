package com.herthrone.factory;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Spell;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;
import com.herthrone.game.Binder;
import com.herthrone.game.Side;
import com.herthrone.object.AuraBuff;
import com.herthrone.object.TriggeringMechanics;
import com.herthrone.object.ValueAttribute;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpellFactory {

  private static final Logger logger = Logger.getLogger(SpellFactory.class.getName());

  public static Spell create(final ConstSpell spell) {
    final SpellConfig config = ConfigLoader.getSpellConfigByName(spell);
    return create(config.name, config.displayName, config.className, config.crystal, config.type,
        config.singleTargetConfigOptional, config.targetConfigV2, config.effects);
  }

  static Spell create(final ConstSpell name, final String displayName, final ConstClass className,
                      final int crystal, final ConstType type,
                      final Optional<TargetConfig> targetConfig, final Optional<TargetConfig>
                          targetConfigV2, final List<MechanicConfig> effects) {
    return new Spell() {

      private final ValueAttribute crystalManaCostAttr = new ValueAttribute(crystal);
      private final Binder binder = new Binder();
      private final AuraBuff auraBuff = new AuraBuff();
      private final TriggeringMechanics triggeringMechanics = TriggeringMechanics.create(
          ConstTrigger.ON_PLAY,
          effects.stream().map(MechanicConfig::clone).collect(Collectors.toList()));

      @Override
      public Optional<TargetConfig> getTargetV2() {
        return targetConfigV2;
      }

      @Override
      public String toString() {
        return view().toString();
      }

      @Override
      public Map<String, String> view() {
        return ImmutableMap.<String, String>builder()
            .put(Constant.CARD_NAME, cardName())
            .put(Constant.CRYSTAL, manaCost().toString())
            .put(Constant.TYPE, type().toString())
            .put(Constant.CLASS, className().toString())
            .build();
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
      public void refresh() {
        final Side side = binder().getSide();
        final int accumulatedSpellDamage = auraBuff.accumulatedBuffValue;
        auraBuff.reset();
        side.board.stream()
            .forEach(minion ->
              minion.getTriggeringMechanics().get(ConstTrigger.ON_SPELL_DAMAGE)
                  .forEach(config -> auraBuff.add(minion, config.value)
            ));
        final int spellDamageBuffDelta = auraBuff.accumulatedBuffValue - accumulatedSpellDamage;

        if (spellDamageBuffDelta != 0) {
          logger.debug("Updating spell damage buff: " + spellDamageBuffDelta);
          getTriggeringMechanics().get(ConstTrigger.ON_PLAY)
              .forEach(effect -> effect.value += spellDamageBuffDelta);
        }
      }

      @Override
      public TriggeringMechanics getTriggeringMechanics() {
        return triggeringMechanics;
      }

    };
  }

}
