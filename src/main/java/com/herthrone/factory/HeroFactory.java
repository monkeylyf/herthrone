package com.herthrone.factory;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Creature;
import com.herthrone.base.Effect;
import com.herthrone.base.Hero;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.HeroConfig;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;
import com.herthrone.game.Binder;
import com.herthrone.game.Side;
import com.herthrone.objects.BooleanAttribute;
import com.herthrone.objects.BooleanMechanics;
import com.herthrone.objects.IntAttribute;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yifeng on 4/8/16.
 */
public class HeroFactory {

  public static final int HEALTH = 30;
  private static final int HERO_INIT_MOVE_POINTS = 1;

  public static final String HERO_POWER_ERROR_MESSAGE = "Cannot use hero power in this turn";

  public static Hero create(final ConstHero hero) {
    HeroConfig heroConfig = ConfigLoader.getHeroConfigByName(hero);
    return HeroFactory.create(
        HeroFactory.HEALTH, heroConfig.getName(), heroConfig.getHeroPower(), heroConfig.getClassName());
  }

  public static Hero create(final int health, final ConstHero name, final ConstSpell
      heroPowerName, final ConstClass className) {
    return new Hero() {
      private final IntAttribute healthAttr = new IntAttribute(health);
      private final IntAttribute healthUpperAttr = new IntAttribute(health);
      private final IntAttribute armorAttr = new IntAttribute(0);
      private final IntAttribute attackAttr = new IntAttribute(0);
      private final IntAttribute crystalManaCostAttr = new IntAttribute(0);
      private final IntAttribute attackMovePoints = new IntAttribute(HERO_INIT_MOVE_POINTS);
      private final IntAttribute heroPowerMovePoints = new IntAttribute(HERO_INIT_MOVE_POINTS);
      private final BooleanMechanics booleanMechanics = new BooleanMechanics();
      private final Binder binder = new Binder();
      private Spell heroPower = SpellFactory.createHeroPowerByName(heroPowerName);
      private Optional<Weapon> weaponOptional = Optional.absent();

      @Override
      public Map<String, String> view() {
        return ImmutableMap.<String, String>builder()
            .put(Constant.CARD_NAME, getCardName())
            .put(Constant.HEALTH, getHealthAttr().toString() + "/" + getHealthUpperAttr().toString())
            .put(Constant.ARMOR, getArmorAttr().toString())
            .put(Constant.WEAPON, (getWeapon().isPresent()) ? getWeapon().toString() : "unarmed")
            .put(Constant.ATTACK, getAttackAttr().toString())
            .put(Constant.CRYSTAL, getCrystalManaCost().toString())
            .put(Constant.MOVE_POINTS, getAttackMovePoints().toString())
            .build();
      }

      @Override
      public String getCardName() {
        return name.toString();
      }

      @Override
      public ConstType getType() {
        return ConstType.HERO;
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
        return false;
      }

      @Override
      public Binder getBinder() {
        return binder;
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
        return attackMovePoints;
      }

      @Override
      public BooleanMechanics getBooleanMechanics() {
        return booleanMechanics;
      }

      @Override
      public void causeDamage(final Creature attackee) {
        attackee.takeDamage(weaponOptional.get().use());
        if (weaponOptional.get().getDurabilityAttr().getVal() == 0) {
          unequip();
        }
      }

      @Override
      public boolean takeDamage(int damage) {
        final int healthBeforeDamage = getHealthLoss();
        if (armorAttr.getVal() >= damage) {
          armorAttr.decrease(damage);
        } else {
          healthAttr.decrease(damage - armorAttr.getVal());
          armorAttr.reset();
        }
        return healthBeforeDamage != getHealthLoss();
      }

      @Override
      public boolean canDamage() {
        return weaponOptional.isPresent();
      }

      @Override
      public boolean isDead() {
        return healthAttr.getVal() <= 0;
      }

      @Override
      public void death() {
        throw new GameEndException(getCardName() + " is death");
      }

      @Override
      public boolean canMove() {
        return weaponOptional.isPresent() &&
            attackMovePoints.getVal() > 0 &&
            BooleanAttribute.isAbsentOrOff(booleanMechanics.get(ConstMechanic.FROZEN));
      }

      @Override
      public int getHealthLoss() {
        return getHealthUpperAttr().getVal() - getHealthAttr().getVal();
      }

      @Override
      public IntAttribute getArmorAttr() {
        return armorAttr;
      }

      @Override
      public IntAttribute getHeroPowerMovePoints() {
        return heroPowerMovePoints;
      }

      @Override
      public Optional<Weapon> getWeapon() {
        return weaponOptional;
      }

      @Override
      public void equip(Weapon newWeapon) {
        if (weaponOptional.isPresent()) {
          unequip();
        }
        weaponOptional = Optional.of(newWeapon);
      }

      @Override
      public Spell getHeroPower() {
        return heroPower;
      }

      @Override
      public void UpdateHeroPower(final Spell heroPower) {
        this.heroPower = heroPower;
      }

      @Override
      public void unequip() {
        weaponOptional = Optional.absent();
      }

      @Override
      public void playToEquip(final Weapon weapon) {
        Optional<MechanicConfig> onEquip = weapon.getEffectMechanics().get(ConstMechanic.ON_EQUIP);
        EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(onEquip, this);
        equip(weapon);
      }

      @Override
      public void useHeroPower(final Creature creature) {
        Preconditions.checkArgument(heroPowerMovePoints.getVal() > 0, HERO_POWER_ERROR_MESSAGE);
        EffectFactory.getActionsByConfig(heroPower, creature).stream().forEach(Effect::act);
        heroPowerMovePoints.decrease(1);

        final Side side = getBinder().getSide();
        List<Effect> inspireEffects = side.board.stream()
            .sorted(EffectFactory.compareBySequenceId)
            .map(minion -> minion.getEffectMechanics().get(ConstMechanic.INSPIRE))
            .filter(mechanicOptional -> mechanicOptional.isPresent())
            .map(mechanicOptional -> EffectFactory.pipeMechanicEffect(mechanicOptional.get(), this))
            .collect(Collectors.toList());

        side.getEffectQueue().enqueue(inspireEffects);
      }

      @Override
      public void endTurn() {
        this.attackMovePoints.endTurn();
      }

      @Override
      public void startTurn() {

      }

      @Override
      public String toString() {
        final Objects.ToStringHelper stringHelper = Objects.toStringHelper(this)
            .add("hero", getCardName())
            .add("health", getHealthAttr().getVal())
            .add("health_upper", getHealthUpperAttr().getVal())
            .add("attack", getAttackAttr().getVal());

        if (weaponOptional.isPresent()) {
          final Weapon weapon = weaponOptional.get();
          stringHelper
              .add("weapon_attack", weapon.getAttackAttr().getVal())
              .add("weapon_durability", weapon.getDurabilityAttr().getVal());
        }
        return stringHelper.toString();
      }
    };
  }

  public static class GameEndException extends RuntimeException {

    public GameEndException(final String message) {
      super(message);
    }
  }
}
