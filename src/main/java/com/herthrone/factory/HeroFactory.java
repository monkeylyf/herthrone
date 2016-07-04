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
import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;
import com.herthrone.game.Binder;
import com.herthrone.game.Side;
import com.herthrone.object.BooleanAttribute;
import com.herthrone.object.BooleanMechanics;
import com.herthrone.object.ValueAttribute;

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
    return HeroFactory.create(heroConfig.name, heroConfig.displayName, heroConfig.className,
        HeroFactory.HEALTH, heroConfig.heroPower);
  }

  public static Hero create(final ConstHero name, final String displayName,
                            final ConstClass className, final int health,
                            final ConstSpell heroPowerName)  {
    return new Hero() {

      private final ValueAttribute healthAttr = new ValueAttribute(health);
      private final ValueAttribute healthUpperAttr = new ValueAttribute(health);
      private final ValueAttribute armorAttr = new ValueAttribute(0);
      private final ValueAttribute attackAttr = new ValueAttribute(0);
      private final ValueAttribute crystalManaCostAttr = new ValueAttribute(0);
      private final ValueAttribute attackMovePoints = new ValueAttribute(HERO_INIT_MOVE_POINTS);
      private final ValueAttribute heroPowerMovePoints = new ValueAttribute(HERO_INIT_MOVE_POINTS);
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
      public ValueAttribute manaCost() {
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
      public ValueAttribute armor() {
        return armorAttr;
      }

      @Override
      public ValueAttribute heroPowerMovePoints() {
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
        final List<MechanicConfig> onEquip = weapon.getEffectMechanics().get(ConstTrigger.ON_PLAY);
        onEquip.stream()
            .forEach(mechanic -> EffectFactory.pipeMechanicEffectIfPresentAndMeetCondition(
                Optional.of(mechanic), binder().getSide(), this));
        equip(weapon);
      }

      @Override
      public void useHeroPower(final Creature creature) {
        Preconditions.checkArgument(heroPowerMovePoints.value() > 0, HERO_POWER_ERROR_MESSAGE);
        EffectFactory.getActionsByConfig(heroPower, creature).stream().forEach(Effect::act);
        heroPowerMovePoints.decrease(1);

        final Side side = binder().getSide();
        List<Effect> useHeroPowerMechanics = side.board.stream()
            .filter(minion -> minion.getEffectMechanics().get(ConstTrigger.ON_USE_HERO_POWER).size() > 0)
            .sorted(EffectFactory.compareBySequenceId)
            .flatMap(minion -> minion.getEffectMechanics().get(ConstTrigger.ON_USE_HERO_POWER).stream())
            .map(mechanic -> EffectFactory.pipeMechanicEffect(mechanic, this))
            .collect(Collectors.toList());

        side.getEffectQueue().enqueue(useHeroPowerMechanics);
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
