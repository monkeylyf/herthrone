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
import com.herthrone.objects.BooleanAttribute;
import com.herthrone.objects.BooleanMechanics;
import com.herthrone.objects.EffectMechanics;
import com.herthrone.objects.IntAttribute;
import org.apache.log4j.Logger;

import java.util.Comparator;
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

  public static Minion create(final ConstMinion minionName, final Side side) {
    final Minion minion = create(minionName);
    minion.getBinder().bind(side);
    return minion;
  }

  public static Minion create(final ConstMinion minionName) {
    MinionConfig config = ConfigLoader.getMinionConfigByName(minionName);
    Preconditions.checkNotNull(config, String.format("Minion %s undefined", minionName.toString()));
    return createMinion(config.getHealth(), config.getAttack(), config.getCrystal(),
                        config.getClassName(), config.getName(), config.getMechanics(),
                        config.isCollectible());
  }

  private static Minion createMinion(final int health, final int attack, final int crystalManaCost,
                                     final ConstClass className, final ConstMinion name,
                                     final Map<ConstMechanic, MechanicConfig> mechanics,
                                     final boolean isCollectible) {
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
        Preconditions.checkArgument(seqId.isPresent(), getCardName() + " sequence ID not set yet");
        return seqId.get().intValue();
      }

      @Override
      public void setSequenceId(final int sequenceId) {
        Preconditions.checkArgument(!seqId.isPresent(), "Minion sequence ID already set");
        seqId = Optional.of(sequenceId);
        logger.debug(String.format("%s ID set to %d", getCardName(), sequenceId));
      }

      @Override
      public void silence() {

      }

      @Override
      public void destroy() {
        final int health = healthAttr.getVal();
        healthAttr.decrease(health);
      }

      @Override
      public void playOnBoard(final Container<Minion> board) {
        // TODO: battlecry happens before summon triggered events.
        summonOnBoard(board);
        Optional<MechanicConfig> battlecry = getEffectMechanics().get(ConstMechanic.BATTLECRY);
        EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(battlecry, this);
        // Combo condition check that there must be one replay record before this action.
        if (getBinder().getSide().replay.size() > 1) {
          Optional<MechanicConfig> combo = getEffectMechanics().get(ConstMechanic.COMBO);
          EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(combo, this);
        }
      }

      @Override
      public void summonOnBoard(Container<Minion> board) {
        final Comparator<Minion> bySequenceId = (m1, m2) -> Integer.compare(
            m1.getSequenceId(), m2.getSequenceId());
        List<Effect> onSummonEffects = board.stream()
            .sorted(bySequenceId)
            .map(minion -> minion.getEffectMechanics().get(ConstMechanic.ON_SUMMON))
            .filter(mechanicOptional -> mechanicOptional.isPresent())
            .map(mechanicOptional -> EffectFactory.pipeMechanicEffect(mechanicOptional.get(), this))
            .collect(Collectors.toList());
        // Add to board after scanning through existing board otherwise this minion itself triggers
        // its summon effect if there is one.
        board.add(this);
        getBinder().getSide().getEffectQueue().enqueue(onSummonEffects);
      }

      @Override
      public String getCardName() {
        return name.toString();
      }

      @Override
      public ConstType getType() {
        return ConstType.MINION;
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
        return isCollectible;
      }

      @Override
      public Binder getBinder() {
        return binder;
      }

      @Override
      public Map<String, String> view() {
        return ImmutableMap.<String, String>builder()
            .put(Constant.CARD_NAME, getCardName())
            .put(Constant.HEALTH, getHealthAttr().toString() + "/" + getHealthUpperAttr().toString())
            .put(Constant.ATTACK, getAttackAttr().toString())
            .put(Constant.CRYSTAL, getCrystalManaCost().toString())
            //.put(Constant.DESCRIPTION, "TODO")
            .put(Constant.TYPE, getClassName().toString())
            .put(Constant.MOVE_POINTS, getAttackMovePoints().toString())
            .build();
      }

      @Override
      public IntAttribute getHealthAttr() {
        return healthAttr;
      }

      @Override
      public IntAttribute getHealthUpperAttr() {
        return healthUpperAttr;
      }

      @Override
      public IntAttribute getAttackAttr() {
        return attackAttr;
      }

      @Override
      public IntAttribute getAttackMovePoints() {
        return this.movePoints;
      }

      @Override
      public BooleanMechanics getBooleanMechanics() {
        return booleanMechanics;
      }

      @Override
      public void causeDamage(final Creature creature) {
        // TODO: but this is not the only way to reveal a minion in stealth.
        // http://hearthstone.gamepedia.com/Stealth
        booleanMechanics.resetIfPresent(ConstMechanic.STEALTH);
        boolean isDamaged = creature.takeDamage(attackAttr.getVal());
        if (isDamaged) {
          if (BooleanAttribute.isPresentAndOn(booleanMechanics.get(ConstMechanic.FREEZE))) {
            creature.getBooleanMechanics().initialize(ConstMechanic.FROZEN, 1);
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
        return attackAttr.getVal() > 0;
      }

      @Override
      public boolean isDead() {
        return healthAttr.getVal() <= 0;
      }

      @Override
      public void death() {
        final Side side = binder.getSide();
        side.board.remove(this);

        Optional<MechanicConfig> deathrattleConfig = effectMechanics.get(ConstMechanic.DEATHRATTLE);
        EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(deathrattleConfig, this);
      }

      @Override
      public boolean canMove() {
        return movePoints.getVal() > 0 &&
            BooleanAttribute.isAbsentOrOff(booleanMechanics.get(ConstMechanic.CHARGE));
      }

      @Override
      public int getHealthLoss() {
        return getHealthUpperAttr().getVal() - getHealthAttr().getVal();
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
      minion.getAttackMovePoints().buff.temp.decrease(minion.getAttackMovePoints().getVal());
    }

    return minion;
  }

}