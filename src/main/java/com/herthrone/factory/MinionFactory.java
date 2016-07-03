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
import com.herthrone.object.IntAttribute;
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
                               final Map<ConstMechanic, MechanicConfig> mechanics) {
    final Minion minion = new Minion() {

      private final IntAttribute healthAttr = new IntAttribute(health);
      private final IntAttribute healthUpperAttr = new IntAttribute(health);
      private final IntAttribute attackAttr = new IntAttribute(attack);
      private final IntAttribute crystalManaCostAttr = new IntAttribute(crystalManaCost);
      private final IntAttribute movePoints = new IntAttribute(
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
        // Battlecry mechanic.
        Optional<MechanicConfig> battlecry = getEffectMechanics().get(ConstMechanic.BATTLECRY);
        if (battlecry.isPresent() && !battlecry.get().triggerOnlyWithTarget) {
          System.out.println("shit");
          System.out.println(battlecry.isPresent());
          System.out.println(battlecry.get().triggerOnlyWithTarget);
          EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(battlecry, binder().getSide(), this);
        } else {
          logger.debug("Battlecry with no target specified. Battlecry not triggered");
        }

        // Combo mechanic.
        // Combo condition check that there must be one replay record before this action.
        Optional<MechanicConfig> combo = getEffectMechanics().get(ConstMechanic.COMBO);
        if (combo.isPresent() && !combo.get().triggerOnlyWithTarget) {
          if (binder().getSide().replay.size() > 1) {
            EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(combo, binder().getSide(), this);
          } else {
            logger.debug("First play and not a combo. Combo not triggered");
          }
        } else {
          logger.debug("Combo with no target specified. Combo not triggered");
        }
      }

      @Override
      public void playOnBoard(Container<Minion> board, Creature target) {
        // TODO: battlecry happens before summon triggered events.
        summonOnBoard(board);
        Optional<MechanicConfig> battlecry = getEffectMechanics().get(ConstMechanic.BATTLECRY);
        EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(battlecry, binder().getSide(), target);
        // Combo condition check that there must be one replay record before this action.
        if (binder().getSide().replay.size() > 1) {
          Optional<MechanicConfig> combo = getEffectMechanics().get(ConstMechanic.COMBO);
          EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(combo, binder().getSide(), this);
        }
      }

      @Override
      public void summonOnBoard(final Container<Minion> board) {
        List<Effect> onSummonEffects = board.stream()
            .sorted(EffectFactory.compareBySequenceId)
            .map(minion -> minion.getEffectMechanics().get(ConstMechanic.ON_SUMMON))
            .filter(mechanicOptional -> mechanicOptional.isPresent())
            .map(mechanicOptional -> EffectFactory.pipeMechanicEffect(mechanicOptional.get(), this))
            .collect(Collectors.toList());
        // Add to board after scanning through existing board otherwise this minion itself triggers
        // its summon effect if there is one.
        board.add(this);
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
      public IntAttribute manaCost() {
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
      public IntAttribute health() {
        return healthAttr;
      }

      @Override
      public IntAttribute maxHealth() {
        return healthUpperAttr;
      }

      @Override
      public IntAttribute attack() {
        return attackAttr;
      }

      @Override
      public IntAttribute attackMovePoints() {
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

        Optional<MechanicConfig> deathrattleConfig = effectMechanics.get(ConstMechanic.DEATHRATTLE);
        EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(deathrattleConfig, binder().getSide(), this);
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
      minion.attackMovePoints().buff.temporaryBuff.reset();
    }

    return minion;
  }

}