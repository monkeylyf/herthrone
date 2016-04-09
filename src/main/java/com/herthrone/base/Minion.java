package com.herthrone.base;

import com.herthrone.action.AttackAction;

import java.util.Optional;

/**
 * Created by yifeng on 4/8/16.
 */
public class Minion implements BaseCreature {

  private final Attribute attackAttr;
  private final Attribute healthAttr;
  private final Attribute crystalManaCostAttr;

  public Minion(final int attack, final int health, final int crystalManaCost) {
    this.attackAttr = new Attribute(attack);
    this.healthAttr = new Attribute(health);
    this.crystalManaCostAttr = new Attribute(crystalManaCost);
  }

  @Override
  public Attribute getHealthAttr() {
    return null;
  }

  @Override
  public Attribute getAttackAttr() {
    return null;
  }

  @Override
  public Attribute getArmorAttr() {
    return null;
  }

  @Override
  public void causeDamage(BaseCreature creature) {

  }

  @Override
  public void takeDamage(int damage) {

  }

  @Override
  public void equipWeapon(Weapon weapon) {
    throw new IllegalArgumentException("Minions cannot equip weapon.");
  }

  @Override
  public void disarm() {

  }

  @Override
  public boolean canDamage() {
    return false;
  }

  @Override
  public AttackAction yieldAttackAction(BaseCreature creature) {
    return null;
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
