package com.hearthrone.card;

import com.herthrone.action.CastSpell;
import com.herthrone.base.BaseHero;
import com.herthrone.base.Hero;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.card.heropower.ArmorUp;
import com.herthrone.card.weapon.FieryWarAxe;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by yifeng on 4/4/16.
 */
public class TestHero extends TestCase {

  private final int weaponAttackVal1 = 2;
  private final int weaponAttackVal2 = 3;
  private final int weaponDurability1 = 2;
  private final int weaponDurability2 = 3;

  private BaseHero hero1;
  private BaseHero hero2;

  private Spell heroPower1;
  private Spell heroPower2;

  private Weapon weapon1;
  private Weapon weapon2;

  @Before
  public void setUp() {
    this.heroPower1 = new ArmorUp();
    this.hero1 = new Hero(this.heroPower1);

    this.heroPower2 = new ArmorUp();
    this.hero2 = new Hero(this.heroPower2);

    this.weapon1 = new FieryWarAxe();
    this.weapon2 = new FieryWarAxe();
  }

  @Test
  public void testHeroHealth() {
    assertEquals(Hero.HEALTH, this.hero1.getHealthAttr().getVal());
    assertEquals(Hero.HEALTH, this.hero2.getHealthAttr().getVal());
  }

  @Test
  public void testAttackAction() {
    this.hero1.equipWeapon(weapon1);
    this.hero2.equipWeapon(weapon2);

    this.hero1.AttackAction(this.hero2).act();

    // Even if hero2 has weapon, hero1 won't take any damage from hero2 while attacking.
    assertEquals(Hero.defaultHealth, this.hero1.healthAttr.getVal());
    assertEquals(Hero.defaultHealth - this.weaponAttackVal1, this.hero2.healthAttr.getVal());

    assertEquals(this.weaponDurability1 - 1, this.weapon1.getDurability());
    assertEquals(this.weaponDurability2, this.weapon2.getDurability());
  }

  @Test
  public void testAttackActionUtilWeaponExpires() {
    this.hero1.equip(this.weapon1);
    this.hero2.equip(this.weapon2);

    while (this.hero1.canDamage() || this.hero2.canDamage()) {
      if (this.hero1.canDamage()) {
        this.hero1.AttackAction(this.hero2).act();
      }
      if (this.hero2.canDamage()) {
        this.hero2.AttackAction(this.hero1).act();
      }
    }

    assertEquals(Hero.defaultHealth - this.weaponAttackVal2 * this.weaponDurability2, this.hero1.healthAttr.getVal());
    assertEquals(Hero.defaultHealth - this.weaponAttackVal1 * this.weaponDurability1, this.hero2.healthAttr.getVal());

    assertEquals(0, this.weapon1.getDurability());
    assertEquals(0, this.weapon2.getDurability());
  }

  @Test
  public void testCanAttack() {
    assertFalse(this.hero1.canDamage());
    assertFalse(this.hero2.canDamage());

    this.hero1.equip(this.weapon1);
    this.hero2.equip(this.weapon2);

    assertTrue(this.hero1.canDamage());
    assertTrue(this.hero2.canDamage());

    while (this.hero1.canDamage() || this.hero2.canDamage()) {
      if (this.hero1.canDamage()) {
        this.hero1.AttackAction(this.hero2).act();
      }
      if (this.hero2.canDamage()) {
        this.hero2.AttackAction(this.hero1).act();
      }
    }
    assertFalse(this.hero1.canDamage());
    assertFalse(this.hero2.canDamage());
  }

  @Test
  public void testArmorUp() {
    assertEquals(0, this.hero1.armorAttr.getVal());
    this.heroPower1.cast(this.hero1);
    assertEquals(2, this.hero1.armorAttr.getVal());
    this.heroPower1.cast(this.hero1);
    assertEquals(4, this.hero1.armorAttr.getVal());

    assertEquals(0, this.hero2.armorAttr.getVal());
    this.heroPower1.cast(this.hero2);
    assertEquals(2, this.hero2.armorAttr.getVal());
    this.heroPower1.cast(this.hero2);
    assertEquals(4, this.hero2.armorAttr.getVal());
  }

  @Test
  public void testArmorUpAttackMixture() {
    assertEquals(0, this.hero1.armorAttr.getVal());

    this.hero1.equip(this.weapon1);
    this.hero2.equip(this.weapon2);

    this.hero1.power.cast(this.hero1);
    assertEquals(2, this.hero1.armorAttr.getVal());
    this.hero2.AttackAction(this.hero1).act();
    assertEquals(0, this.hero1.armorAttr.getVal());
    assertEquals(Hero.defaultHealth + 2 - this.weaponAttackVal2, this.hero1.healthAttr.getVal());
  }

}
