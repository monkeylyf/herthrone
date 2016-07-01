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
import com.herthrone.object.BooleanAttribute;
import com.herthrone.object.BooleanMechanics;
import com.herthrone.object.IntAttribute;

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
        HeroFactory.HEALTH, heroConfig.name(), heroConfig.displayName(),
        heroConfig.getHeroPower(), heroConfig.className());
  }

  public static Hero create(final int health, final ConstHero name, final String displayName,
                            final ConstSpell heroPowerName, final ConstClass className) {
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
      private Spell heroPower = HeroPowerFactory.createHeroPowerByName(heroPowerName);
      private Optional<Weapon> weaponOptional = Optional.absent();

      @Override
      public Map<String, String> view() {
        return ImmutableMap.<String, String>builder()
            .put(Constant.CARD_NAME, cardName())
            .put(Constant.HEALTH, health().toString() + "/" + maxHealth().toString())
            .put(Constant.ARMOR, armor().toString())
            .put(Constant.WEAPON, (getWeapon().isPresent()) ? getWeapon().toString() : "unarmed")
            .put(Constant.ATTACK, attack().toString())
            .put(Constant.CRYSTAL, manaCost().toString())
            .put(Constant.MOVE_POINTS, attackMovePoints().toString())
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
        return ConstType.HERO;
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
        return false;
      }

      @Override
      public Binder binder() {
        return binder;
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
        return attackMovePoints;
      }

      @Override
      public BooleanMechanics booleanMechanics() {
        return booleanMechanics;
      }

      @Override
      public void dealDamage(final Creature attackee) {
        attackee.takeDamage(weaponOptional.get().use());
        if (weaponOptional.get().getDurabilityAttr().value() == 0) {
          unequip();
        }
      }

      @Override
      public boolean takeDamage(int damage) {
        final int healthBeforeDamage = healthLoss();
        if (armorAttr.value() >= damage) {
          armorAttr.decrease(damage);
        } else {
          healthAttr.decrease(damage - armorAttr.value());
          armorAttr.reset();
        }
        return healthBeforeDamage != healthLoss();
      }

      @Override
      public boolean canDamage() {
        return weaponOptional.isPresent();
      }

      @Override
      public boolean isDead() {
        return healthAttr.value() <= 0;
      }

      @Override
      public void death() {
        throw new GameEndException(cardName() + " is death");
      }

      @Override
      public boolean canMove() {
        return weaponOptional.isPresent() &&
            attackMovePoints.value() > 0 &&
            BooleanAttribute.isAbsentOrOff(booleanMechanics.get(ConstMechanic.FROZEN));
      }

      @Override
      public int healthLoss() {
        return maxHealth().value() - health().value();
      }

      @Override
      public IntAttribute armor() {
        return armorAttr;
      }

      @Override
      public IntAttribute heroPowerMovePoints() {
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
      public void setHeroPower(final Spell heroPower) {
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
        Preconditions.checkArgument(heroPowerMovePoints.value() > 0, HERO_POWER_ERROR_MESSAGE);
        EffectFactory.getActionsByConfig(heroPower, creature).stream().forEach(Effect::act);
        heroPowerMovePoints.decrease(1);

        final Side side = binder().getSide();
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
            .add("hero", cardName())
            .add("health", health().value())
            .add("health_upper", maxHealth().value())
            .add("attack", attack().value());

        if (weaponOptional.isPresent()) {
          final Weapon weapon = weaponOptional.get();
          stringHelper
              .add("weapon_attack", weapon.getAttackAttr().value())
              .add("weapon_durability", weapon.getDurabilityAttr().value());
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
