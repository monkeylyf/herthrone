package com.herthrone.factory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Creature;
import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstTarget;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;
import com.herthrone.game.Binder;
import com.herthrone.game.Container;
import com.herthrone.game.Side;
import com.herthrone.object.BooleanAttribute;
import com.herthrone.object.BooleanMechanics;
import com.herthrone.object.EffectMechanics;
import com.herthrone.object.ValueAttribute;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by yifeng on 4/13/16.
 */
public class MinionFactory {

  private static final int MINION_INIT_MOVE_POINTS = 1;
  private static final int WINDFURY_INIT_MOVE_POINTS = 2;
  private static final Logger logger = Logger.getLogger(MinionFactory.class.getName());

  public static Minion create(final ConstMinion minionName) {
    MinionConfig config = ConfigLoader.getMinionConfigByName(minionName);
    Preconditions.checkNotNull(config, String.format("Minion %s undefined", minionName.toString()));
    return create(config.health, config.attack, config.crystal, config.className,
        config.name, config.displayName, config.isCollectible, config.mechanics);
  }

  private static Minion create(final int health, final int attack, final int crystalManaCost,
                               final ConstClass className, final ConstMinion name,
                               final String displayName, final boolean isCollectible,
                               final Map<ConstTrigger, List<MechanicConfig>> mechanics) {
    final Minion minion = new Minion() {

      private final ValueAttribute healthAttr = new ValueAttribute(health);
      private final ValueAttribute healthUpperAttr = new ValueAttribute(health);
      private final ValueAttribute attackAttr = new ValueAttribute(attack);
      private final ValueAttribute crystalManaCostAttr = new ValueAttribute(crystalManaCost);
      private final BooleanMechanics booleanMechanics = new BooleanMechanics(mechanics);
      private final EffectMechanics effectMechanics = new EffectMechanics(mechanics);
      private final ValueAttribute movePoints = new ValueAttribute(
          booleanMechanics.has(ConstMechanic.WINDFURY) ?
              WINDFURY_INIT_MOVE_POINTS : MINION_INIT_MOVE_POINTS);
      private final Binder binder = new Binder();
      // use OptionalInt
      private Optional<Integer> seqId = Optional.absent();

      @Override
      public EffectMechanics getEffectMechanics() {
        return effectMechanics;
      }

      @Override
      public int getSequenceId() {
        Preconditions.checkArgument(seqId.isPresent(), cardName() + " sequence ID not set yet");
        return seqId.get();
      }

      @Override
      public void setSequenceId(final int sequenceId) {
        Preconditions.checkArgument(!seqId.isPresent(), "Minion sequence ID already set");
        seqId = Optional.of(sequenceId);
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
        // On-play mechanics.
        final List<MechanicConfig> onPlayMechanics = getEffectMechanics().get(ConstTrigger.ON_PLAY);
        onPlayMechanics.stream()
            .filter(mechanicConfig -> !mechanicConfig.triggerOnlyWithTarget)
            .forEach(mechanic -> EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                Optional.of(mechanic), binder().getSide(), this, getProperTarget(mechanic.effect
                    .get().target, binder().getSide(), this)));
      }

      private Creature getProperTarget(final TargetConfig targetConfig, final Side side,
                                       final Creature creature) {
        switch (targetConfig.type) {
          case HERO:
            if (targetConfig.scope.equals(ConstTarget.OWN)) {
              return side.hero;
            } else if (targetConfig.scope.equals(ConstTarget.OPPONENT)) {
              return side.getOpponentSide().hero;
            } else {
              // TODO: A no-target skill can have effects on both sides.
              return creature;
            }
          default:
            return creature;
        }
      }

      @Override
      public void playOnBoard(final Container<Minion> board, final Creature target) {
        // TODO: on-play mechanics happen before summon triggered events.
        summonOnBoard(board);
        // On-play mechanics.
        List<MechanicConfig> onPlayEffects = getEffectMechanics().get(ConstTrigger.ON_PLAY);
        onPlayEffects.stream()
            .forEach(mechanic -> EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                Optional.of(mechanic), binder().getSide(), this, target));
      }

      @Override
      public void summonOnBoard(final Container<Minion> board) {
        final List<Effect> onSummonEffects = board.stream()
            .sorted(EffectFactory.compareBySequenceId)
            .flatMap(minion -> minion.getEffectMechanics().get(ConstTrigger.ON_SUMMON).stream())
            .map(mechanic -> EffectFactory.pipeMechanicEffect(mechanic, this))
            .collect(Collectors.toList());

        // Add the aura effect to minions that are already on board.
        //final List<MechanicConfig> onPresenceConfigs = getEffectMechanics().get(ConstTrigger.ON_PRESENCE);
        //for (final MechanicConfig onPresenceConfig : onPresenceConfigs) {
        //  board.stream().forEach(minion -> EffectFactory.addAuraEffect(onPresenceConfig.effect.get(), this, minion));}
        // Add aura effect from minion that already on-board to this minion.
        //for (final Minion minion : board.asList()) {
        // final List<MechanicConfig> ExistingOnPresenceConfigs = minion.getEffectMechanics().get(ConstTrigger.ON_PRESENCE);
          //ExistingOnPresenceConfigs.stream().forEach(config -> EffectFactory.addAuraEffect(config.effect.get(), minion, this));
        //}

        // Put minion onto board.
        board.add(this);

        final boolean boardHasAura = board.stream().anyMatch(
            minion -> minion.getEffectMechanics().has(ConstTrigger.ON_PRESENCE));
        if (boardHasAura) {
          logger.debug("Updating aura effects on all minions");
          board.stream().forEach(minion -> minion.refresh());
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
        return this.movePoints;
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
          if (BooleanAttribute.isPresentAndOn(booleanMechanics.get(ConstMechanic.FREEZE))) {
            creature.booleanMechanics().initialize(ConstMechanic.FROZEN, 1);
          }

          if (BooleanAttribute.isPresentAndOn(booleanMechanics.get(ConstMechanic.POISON)) &&
              creature instanceof Minion) {
            ((Minion) creature).destroy();
          }
        }
      }

      @Override
      public boolean takeDamage(final int damage) {
        final Optional<BooleanAttribute> divineShield = booleanMechanics.get(ConstMechanic.DIVINE_SHIELD);
        final boolean willTakeDamage = !BooleanAttribute.isPresentAndOn(divineShield);
        if (willTakeDamage) {
          healthAttr.decrease(damage);
        } else {
          logger.debug(ConstMechanic.DIVINE_SHIELD + " absorbed the damage");
          booleanMechanics.resetIfPresent(ConstMechanic.DIVINE_SHIELD);
        }

        if (isDead()) {
          death();
        }

        return willTakeDamage;
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

        final List<MechanicConfig> onDeathMechanics = effectMechanics.get(ConstTrigger.ON_DEATH);
        onDeathMechanics.stream()
            .forEach(mechanic -> EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                Optional.of(mechanic), binder().getSide(), this, this));


        final List<MechanicConfig> onPresenceConfigs = getEffectMechanics().get(
            ConstTrigger.ON_PRESENCE);
        for (final MechanicConfig onPresenceConfig : onPresenceConfigs) {
          side.board.stream().forEach(minion -> EffectFactory.removeAuraEffect(
              onPresenceConfig.effect.get(), this, minion));
        }
      }

      @Override
      public boolean canMove() {
        return movePoints.value() > 0 &&
            BooleanAttribute.isAbsentOrOff(booleanMechanics.get(ConstMechanic.CHARGE));
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
      }

      @Override
      public void refresh() {
        final Container<Minion> board = binder().getSide().board;
        // Refresh aura effects.
        final List<Minion> auraMinions = binder().getSide().board.stream()
            .filter(minion -> minion.getEffectMechanics().has(ConstTrigger.ON_PRESENCE))
            .collect(Collectors.toList());
        for (final Minion auraMinion : auraMinions) {
          final List<MechanicConfig> onPresenceConfigs = auraMinion.getEffectMechanics().get(ConstTrigger.ON_PRESENCE);
          if (this != auraMinion) {
            onPresenceConfigs.stream().forEach(
                config -> EffectFactory.addAuraEffect(config.effect.get(), auraMinion, this));
          }
        }
      }

      @Override
      public String toString() {
        return view().toString();
      }
    };

    if (!mechanics.containsKey(ConstMechanic.CHARGE)) {
      // Minion with no charge ability waits until next turn to move.
      // TODO: also when a minion switch side due to TAKE_CONTROLL effect, the CHARGE also apply
      // the right after it switches side.
      minion.attackMovePoints().getTemporaryBuff().reset();
    }

    return minion;
  }

}