package com.herthrone.factory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Creature;
import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by yifeng on 4/13/16.
 */
public class MinionFactory {

  private static final int MINION_INIT_MOVE_POINTS = 1;
  private static final int WINDFURY_INIT_MOVE_POINTS = 2;
  static Logger logger = Logger.getLogger(MinionFactory.class.getName());

  public static Minion create(final ConstMinion minionName) {
    MinionConfig config = ConfigLoader.getMinionConfigByName(minionName);
    Preconditions.checkNotNull(config, String.format("Minion %s undefined", minionName.toString()));
    return create(config.health, config.attack, config.crystal, config.className,
        config.name, config.displayName, config.isCollectible, config.mechanics);
  }

  private static Minion create(final int health, final int attack, final int crystalManaCost,
                               final ConstClass className, final ConstMinion name,
                               final String displayName, final boolean isCollectible,
                               final Map<ConstMechanic, List<MechanicConfig>> mechanics) {
    final Minion minion = new Minion() {

      private final ValueAttribute healthAttr = new ValueAttribute(health);
      private final ValueAttribute healthUpperAttr = new ValueAttribute(health);
      private final ValueAttribute attackAttr = new ValueAttribute(attack);
      private final ValueAttribute crystalManaCostAttr = new ValueAttribute(crystalManaCost);
      private final ValueAttribute movePoints = new ValueAttribute(
          mechanics.containsKey(ConstMechanic.WINDFURY) ?
              WINDFURY_INIT_MOVE_POINTS : MINION_INIT_MOVE_POINTS);
      private final BooleanMechanics booleanMechanics = new BooleanMechanics(mechanics);
      private final EffectMechanics effectMechanics = new EffectMechanics(mechanics);
      private final Binder binder = new Binder();

      private Optional<Integer> seqId = Optional.absent();

      @Override
      public EffectMechanics getEffectMechanics() {
        return effectMechanics;
      }

      @Override
      public int getSequenceId() {
        Preconditions.checkArgument(seqId.isPresent(), cardName() + " sequence ID not set yet");
        return seqId.get().intValue();
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
        final List<MechanicConfig> onPlayMechanic = getEffectMechanics().get(ConstMechanic.ON_PLAY);
        onPlayMechanic.stream()
            .filter(mechanicConfig -> !mechanicConfig.triggerOnlyWithTarget)
            .forEach(mechanic -> EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                Optional.of(mechanic), binder().getSide(), this));
      }

      @Override
      public void playOnBoard(final Container<Minion> board, final Creature target) {
        // TODO: on-play mechanics happen before summon triggered events.
        summonOnBoard(board);
        // On-play mechanics.
        List<MechanicConfig> onPlayEffects = getEffectMechanics().get(ConstMechanic.ON_PLAY);
        onPlayEffects.stream()
            .forEach(mechanic -> EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                Optional.of(mechanic), binder().getSide(), target));
      }

      @Override
      public void summonOnBoard(final Container<Minion> board) {
        final List<Effect> onSummonEffects = board.stream()
            .filter(minion -> minion.getEffectMechanics().get(ConstMechanic.ON_SUMMON).size() > 0)
            .sorted(EffectFactory.compareBySequenceId)
            .flatMap(minion -> minion.getEffectMechanics().get(ConstMechanic.ON_SUMMON).stream())
            .map(mechanic -> EffectFactory.pipeMechanicEffect(mechanic, this))
            .collect(Collectors.toList());

        final List<MechanicConfig> onPresenceConfigs = getEffectMechanics().get(ConstMechanic.ON_PRESENCE);
        for (final MechanicConfig onPresenceConfig : onPresenceConfigs) {
          for (final Minion minion : board.asList()) {
            // Execute one by one because max_health must take effect before increase health
            // otherwise increase health will be capped if the minion has full health.
            binder().getSide().getEffectQueue().enqueue(EffectFactory.pipeMechanicEffect(onPresenceConfig, minion));
          }
        }

        // Put minion onto board.
        board.add(this);

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

        final List<MechanicConfig> onDeathMechanics = effectMechanics.get(ConstMechanic.ON_DEATH);
        onDeathMechanics.stream()
            .forEach(mechanic -> EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                Optional.of(mechanic), binder().getSide(), this));
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