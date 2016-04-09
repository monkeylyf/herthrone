package com.hearthrone.card;

import com.herthrone.base.BaseCreature;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.card.hero.HeroFactory;
import com.herthrone.card.heropower.ArmorUp;
import com.herthrone.card.weapon.Constants;
import com.herthrone.card.weapon.WeaponFactory;
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

  private BaseCreature hero1;
  private BaseCreature hero2;

  private Spell heroPower;

  private Weapon weapon1;
  private Weapon weapon2;

  @Before
  public void setUp() {
    this.heroPower = new ArmorUp();

    this.hero1 = HeroFactory.createHeroByName("FOO");
    this.hero2 = HeroFactory.createHeroByName("BAR");

    this.weapon1 = WeaponFactory.createWeapon(0, this.weaponAttackVal1, this.weaponDurability1, Constants.FIERY_WAR_AEX);
    this.weapon2 = WeaponFactory.createWeapon(0, this.weaponAttackVal2, this.weaponDurability2, Constants.FIERY_WAR_AEX);
  }

  @Test
  public void testHeroHealth() {
    assertEquals(HeroFactory.HEALTH, this.hero1.getHealthAttr().getVal());
    assertEquals(HeroFactory.HEALTH, this.hero2.getHealthAttr().getVal());
  }

  @Test
  public void testAttackAction() {
    this.hero1.equipWeapon(weapon1);
    this.hero2.equipWeapon(weapon2);

    this.hero1.yieldAttackAction(this.hero2).act();

    // Even if hero2 has weapon, hero1 won't take any damage from hero2 while attacking.
    assertEquals(HeroFactory.HEALTH, this.hero1.getHealthAttr().getVal());
    assertEquals(HeroFactory.HEALTH - this.weaponAttackVal1, this.hero2.getHealthAttr().getVal());

    assertEquals(this.weaponDurability1 - 1, this.weapon1.getDurabilityAttr().getVal());
    assertEquals(this.weaponDurability2, this.weapon2.getDurabilityAttr().getVal());
  }

  @Test
  public void testAttackActionUtilWeaponExpires() {
    this.hero1.equipWeapon(this.weapon1);
    this.hero2.equipWeapon(this.weapon2);

    while (this.hero1.canDamage() || this.hero2.canDamage()) {
      if (this.hero1.canDamage()) {
        this.hero1.yieldAttackAction(this.hero2).act();
      }
      if (this.hero2.canDamage()) {
        this.hero2.yieldAttackAction(this.hero1).act();
      }
    }

    assertEquals(HeroFactory.HEALTH - this.weaponAttackVal2 * this.weaponDurability2, this.hero1.getHealthAttr().getVal());
    assertEquals(HeroFactory.HEALTH - this.weaponAttackVal1 * this.weaponDurability1, this.hero2.getHealthAttr().getVal());

    assertEquals(0, this.weapon1.getDurabilityAttr().getVal());
    assertEquals(0, this.weapon2.getDurabilityAttr().getVal());
  }

  @Test
  public void testCanAttack() {
    assertFalse(this.hero1.canDamage());
    assertFalse(this.hero2.canDamage());

    this.hero1.equipWeapon(this.weapon1);
    this.hero2.equipWeapon(this.weapon2);

    assertTrue(this.hero1.canDamage());
    assertTrue(this.hero2.canDamage());

    while (this.hero1.canDamage() || this.hero2.canDamage()) {
      if (this.hero1.canDamage()) {
        this.hero1.yieldAttackAction(this.hero2).act();
      }
      if (this.hero2.canDamage()) {
        this.hero2.yieldAttackAction(this.hero1).act();
      }
    }
    assertFalse(this.hero1.canDamage());
    assertFalse(this.hero2.canDamage());
  }

  @Test
  public void testArmorUp() {
    assertEquals(0, this.hero1.getArmorAttr().getVal());
    this.heroPower.cast(this.hero1);
    assertEquals(2, this.hero1.getArmorAttr().getVal());
    this.heroPower.cast(this.hero1);
    assertEquals(4, this.hero1.getArmorAttr().getVal());

    assertEquals(0, this.hero2.getArmorAttr().getVal());
    this.heroPower.cast(this.hero2);
    assertEquals(2, this.hero2.getArmorAttr().getVal());
    this.heroPower.cast(this.hero2);
    assertEquals(4, this.hero2.getArmorAttr().getVal());
  }

  @Test
  public void testArmorUpAttackMixture() {
    assertEquals(0, this.hero1.getArmorAttr().getVal());

    this.hero1.equipWeapon(this.weapon1);
    this.hero2.equipWeapon(this.weapon2);

    this.heroPower.cast(this.hero1);
    assertEquals(2, this.hero1.getArmorAttr().getVal());
    this.hero2.yieldAttackAction(this.hero1).act();
    assertEquals(0, this.hero1.getArmorAttr().getVal());
    assertEquals(HeroFactory.HEALTH + 2 - this.weaponAttackVal2, this.hero1.getHealthAttr().getVal());
  }

}
