package com.herthrone.base;

import com.herthrone.action.*;

/**
 * Created by yifeng on 4/2/16.
 */
public class Hero implements BaseHero {

  public static final int HEALTH = 30;
  public static final int ATTACK = 0;
  public static final int ARMOR = 0;
  public static final int CRYSTAL_MANA_COST = 0;

  private final Attribute healthAttr;
  private final Attribute armorAttr;
  private final Attribute attackAttr;
  private final Attribute crystalManaCostAttr;
  private final Spell power;
  private Weapon weapon = null;

  public Hero(Spell power) {
    this.power = power;
    this.attackAttr = new Attribute(Hero.ATTACK);
    this.armorAttr = new Attribute(Hero.ARMOR);
    this.crystalManaCostAttr = new Attribute(Hero.CRYSTAL_MANA_COST);
    this.healthAttr = new Attribute(Hero.HEALTH);
  }

  @Override
  public void equipWeapon(final Weapon weapon) {
    this.weapon = weapon;
  }

  @Override
  public void disarm() {
    this.weapon = null;
  }

  @Override
  public Attribute getHealthAttr() {
    return this.healthAttr;
  }

  @Override
  public Attribute getAttackAttr() {
    return this.attackAttr;
  }

  @Override
  public void causeDamage(BaseCreature attackee) {
    attackee.getAttackAttr().decrease(this.weapon.use());
    attackee.takeDamage(this.weapon.use());
    if (this.weapon.getDurability().getVal() == 0) {
      disarm();
    }
  }

  @Override
  public void takeDamage(final int damage) {
    if (this.armorAttr.getVal() >= damage) {
      this.armorAttr.decrease(damage);
    } else {
      this.healthAttr.decrease(damage - this.armorAttr.getVal());
      this.armorAttr.reset();
    }
  }

  public Action AttackAction(BaseCreature baseCreature) {
    return null;
    //return new AttackAction(this, baseCreature);
  }

  @Override
  public String getCardName() {
    return null;
  }

  @Override
  public Attribute getCrystalManaCost() {
    return null;
  }
}
