package com.herthrone.factory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Creature;
import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;
import com.herthrone.game.Binder;
import com.herthrone.game.Container;
import com.herthrone.game.Side;
import com.herthrone.object.BooleanMechanics;
import com.herthrone.object.TriggeringMechanics;
import com.herthrone.object.ValueAttribute;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;


/**
 * Created by yifeng on 4/13/16.
 */
public class MinionFactory {

  private static final Logger logger = Logger.getLogger(MinionFactory.class.getName());

  public static Minion create(final ConstMinion minionName) {
    final MinionConfig config = ConfigLoader.getMinionConfigByName(minionName);
    Preconditions.checkNotNull(config, String.format("Minion %s undefined", minionName.toString()));
    return create(config.health, config.attack, config.crystal, config.className,
        config.name, config.displayName, config.isCollectible, config.mechanics);
  }

  private static Minion create(final int health, final int attack, final int crystalManaCost,
                               final ConstClass className, final ConstMinion name,
                               final String displayName, final boolean isCollectible,
                               final Map<ConstTrigger, List<MechanicConfig>> mechanics) {
    return new Minion() {

      private final ValueAttribute healthAttr = new ValueAttribute(health);
      private final ValueAttribute healthUpperAttr = new ValueAttribute(health);
      private final ValueAttribute attackAttr = new ValueAttribute(attack);
      private final ValueAttribute crystalManaCostAttr = new ValueAttribute(crystalManaCost);
      private final BooleanMechanics booleanMechanics = new BooleanMechanics(mechanics);
      private final TriggeringMechanics effectMechanics = new TriggeringMechanics(mechanics);
      private final ValueAttribute movePoints = new ValueAttribute(
          booleanMechanics.isOn(ConstMechanic.WINDFURY) ?
              Constant.WINDFURY_INIT_MOVE_POINTS : Constant.INIT_MOVE_POINTS,
          booleanMechanics.isOn(ConstMechanic.CHARGE)
          );
      private final Binder binder = new Binder();
      private OptionalInt seqId = OptionalInt.empty();

      @Override
      public TriggeringMechanics getTriggeringMechanics() {
        return effectMechanics;
      }

      @Override
      public int getSequenceId() {
        Preconditions.checkArgument(seqId.isPresent(), cardName() + " sequence ID not set yet");
        return seqId.getAsInt();
      }

      @Override
      public void setSequenceId(final int sequenceId) {
        Preconditions.checkArgument(!seqId.isPresent(), "Minion sequence ID already set");
        seqId = OptionalInt.of(sequenceId);
        logger.debug(String.format("%s ID set to %d", cardName(), sequenceId));
      }

      @Override
      public void silence() {

      }

      @Override
      public void destroy() {
        final int health = healthAttr.value();
        healthAttr.decrease(health);
      }

      @Override
      public void playOnBoard(final Container<Minion> board) {
        summonOnBoard(board);
        TriggerFactory.passiveTrigger(this, ConstTrigger.ON_PLAY);
      }

      @Override
      public void playOnBoard(final Container<Minion> board, final Creature target) {
        // TODO: on-play mechanics happen before summon triggered events.
        summonOnBoard(board);
        TriggerFactory.activeTrigger(this, ConstTrigger.ON_PLAY, target);
      }

      @Override
      public void summonOnBoard(final Container<Minion> board) {
        final List<Effect> onSummonEffects = board.stream()
            .sorted(EffectFactory.compareBySequenceId)
            .flatMap(minion -> minion.getTriggeringMechanics().get(ConstTrigger.ON_SUMMON).stream())
            .flatMap(mechanic -> EffectFactory.pipeMechanicEffect(mechanic, this).stream())
            .collect(Collectors.toList());

        // Put minion onto board.
        board.add(this);

        final boolean boardHasAura = board.stream().anyMatch(
            minion -> minion.getTriggeringMechanics().has(ConstTrigger.ON_PRESENCE));
        if (boardHasAura) {
          logger.debug("Updating aura effects on all minions");
          board.stream().forEach(Minion::refresh);
        }

        if (getTriggeringMechanics().has(ConstTrigger.ON_SPELL_DAMAGE)) {
          binder().getSide().hand.stream()
              .filter(card -> card instanceof Spell)
              .map(card -> (Spell) card)
              .forEach(Spell::refresh);
        }
        // Execute effects.
        binder().getSide().getEffectQueue().enqueue(onSummonEffects);
      }

      @Override
      public ConstMinion minionConstName() {
        return name;
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
        return ConstType.MINION;
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
        return isCollectible;
      }

      @Override
      public Binder binder() {
        return binder;
      }

      @Override
      public Map<String, String> view() {
        return ImmutableMap.<String, String>builder()
            .put(Constant.CARD_NAME, cardName())
            .put(Constant.HEALTH, health().toString() + "/" + maxHealth().toString())
            .put(Constant.ATTACK, attack().toString())
            .put(Constant.CRYSTAL, manaCost().toString())
            .put(Constant.TYPE, className().toString())
            .put(Constant.MOVE_POINTS, attackMovePoints().toString())
            .build();
      }

      @Override
      public ValueAttribute health() {
        return healthAttr;
      }

      @Override
      public ValueAttribute maxHealth() {
        return healthUpperAttr;
      }

      @Override
      public ValueAttribute attack() {
        return attackAttr;
      }

      @Override
      public ValueAttribute attackMovePoints() {
        return movePoints;
      }

      @Override
      public BooleanMechanics booleanMechanics() {
        return booleanMechanics;
      }

      @Override
      public void dealDamage(final Creature creature) {
        // TODO: but this is not the only way to reveal a minion in stealth.
        // http://hearthstone.gamepedia.com/Stealth
        booleanMechanics.resetIfPresent(ConstMechanic.STEALTH);
        boolean isDamaged = creature.takeDamage(attackAttr.value());
        if (isDamaged) {
          if (booleanMechanics().isOn(ConstMechanic.FREEZE)) {
            creature.booleanMechanics().initialize(ConstMechanic.FROZEN, 1);
          }

          if (booleanMechanics().isOn(ConstMechanic.POISON) && creature instanceof Minion) {
            ((Minion) creature).destroy();
          }
        }
      }

      @Override
      public boolean takeDamage(final int damage) {
        final boolean isDamaged = booleanMechanics.isOff(ConstMechanic.DIVINE_SHIELD);
        if (isDamaged) {
          healthAttr.decrease(damage);
        } else {
          logger.debug(ConstMechanic.DIVINE_SHIELD + " absorbed the damage");
          booleanMechanics.resetIfPresent(ConstMechanic.DIVINE_SHIELD);
        }

        if (isDamaged) {
          TriggerFactory.passiveTrigger(this, ConstTrigger.ON_TAKE_DAMAGE);
        }
        if (isDead()) {
          death();
        }

        return isDamaged;
      }

      @Override
      public boolean canDamage() {
        return attackAttr.value() > 0;
      }

      @Override
      public boolean isDead() {
        return healthAttr.value() <= 0;
      }

      @Override
      public void death() {
        final Side side = binder.getSide();
        side.board.remove(this);

        // Remove aura effects if the dead one has it.
        getTriggeringMechanics().get(ConstTrigger.ON_PRESENCE).stream()
            .forEach(config -> {
              side.board.stream()
                  .filter(minion -> {
                    final ConstType type = config.effect.get().target.type;
                    return type.equals(ConstType.MINION) || type.equals(minion.type());
                  })
                  .forEach(minion -> EffectFactory.removeAuraEffect(
                      config.effect.get(), this, minion));
            });
        // Remove spell damage effects if the dead one has it.
        binder().getSide().hand.stream()
            .filter(card -> card instanceof Spell)
            .map(card -> (Spell) card)
            .forEach(Spell::refresh);

        TriggerFactory.passiveTrigger(this, ConstTrigger.ON_DEATH);
     }

      @Override
      public boolean canMove() {
        return movePoints.value() > 0 && booleanMechanics().isOff(ConstMechanic.FROZEN);
      }

      @Override
      public int healthLoss() {
        return maxHealth().value() - health().value();
      }

      @Override
      public void endTurn() {
        this.movePoints.endTurn();
      }

      @Override
      public void startTurn() {
        booleanMechanics().resetIfPresent(ConstMechanic.FROZEN);
      }

      @Override
      public void refresh() {
        final Container<Minion> board = binder().getSide().board;
        // Refresh aura effects.
        final List<Minion> auraMinions = binder().getSide().board.stream()
            .filter(minion -> minion.getTriggeringMechanics().has(ConstTrigger.ON_PRESENCE))
            .collect(Collectors.toList());
        for (final Minion auraMinion : auraMinions) {
          final List<MechanicConfig> onPresenceConfigs = auraMinion.getTriggeringMechanics().get(ConstTrigger.ON_PRESENCE);
          if (this != auraMinion) {
            onPresenceConfigs.stream().forEach(
                config -> {
                  final ConstType type = config.effect.get().target.type;
                  if (type.equals(ConstType.MINION) || type().equals(type)) {
                    EffectFactory.addAuraEffect(config.effect.get(), auraMinion, this);
                  }
                });
          }
        }
      }

      @Override
      public String toString() {
        return view().toString();
      }
    };
  }

}