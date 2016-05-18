package com;

import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.base.Weapon;
import com.herthrone.card.factory.Action;
import com.herthrone.card.factory.EffectFactory;
import com.herthrone.card.factory.HeroFactory;
import com.herthrone.card.factory.MinionFactory;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstHeroPower;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.game.Battlefield;
import com.herthrone.game.GameManager;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

/**
 * Created by yifeng on 4/4/16.
 */
public class HeroTest extends TestCase {


  private final int weaponAttackVal1 = 2;
  private final int weaponAttackVal2 = 3;
  private final int weaponDurability1 = 2;
  private final int weaponDurability2 = 3;
  private final int armorGain = 2;

  private Hero hero1;
  private Hero hero2;
  private Battlefield battlefield1;
  private Battlefield battlefield2;

  private MinionFactory minionFactory1;
  private MinionFactory minionFactory2;
  private EffectFactory effectFactory1;
  private EffectFactory effectFactory2;

  private SpellConfig armorUp;

  private Weapon weapon1;
  private Weapon weapon2;

  private GameManager gm;

  @Before
  public void setUp() {
    this.gm = new GameManager(ConstHero.GARROSH_HELLSCREAM, ConstHero.GARROSH_HELLSCREAM, Collections.emptyList(), Collections.emptyList());
    this.hero1 = this.gm.battlefield1.mySide.hero;
    this.hero2 = this.gm.battlefield1.opponentSide.hero;
    this.battlefield1 = this.gm.battlefield1;
    this.battlefield2 = this.gm.battlefield2;

    this.minionFactory1 = this.gm.factory1.minionFactory;
    this.minionFactory2 = this.gm.factory2.minionFactory;
    this.effectFactory1 = this.gm.factory1.effectFactory;
    this.effectFactory2 = this.gm.factory2.effectFactory;

    this.armorUp = ConfigLoader.getHeroPowerConfigByName(ConstHeroPower.ARMOR_UP);

    this.weapon1 = this.gm.factory1.weaponFactory.createWeapon(0, this.weaponAttackVal1, this.weaponDurability1, ConstWeapon.FIERY_WAR_AXE, ConstClass.WARRIOR, true);
    this.weapon2 = this.gm.factory2.weaponFactory.createWeapon(0, this.weaponAttackVal2, this.weaponDurability2, ConstWeapon.FIERY_WAR_AXE, ConstClass.WARRIOR, true);
  }

  @Test
  public void testHeroHealth() {
    assertEquals(HeroFactory.HEALTH, this.hero1.getHealthAttr().getVal());
    assertEquals(HeroFactory.HEALTH, this.hero2.getHealthAttr().getVal());
  }

  @Test
  public void testAttackAction() {
    this.hero1.arm(weapon1);
    this.hero2.arm(weapon2);

    hero1AttackHero2();

    // Even if hero2 has weapon, hero1 won't take any damage from hero2 while attacking.
    assertEquals(HeroFactory.HEALTH, this.hero1.getHealthAttr().getVal());
    assertEquals(HeroFactory.HEALTH - this.weaponAttackVal1, this.hero2.getHealthAttr().getVal());

    assertEquals(this.weaponDurability1 - 1, this.weapon1.getDurabilityAttr().getVal());
    assertEquals(this.weaponDurability2, this.weapon2.getDurabilityAttr().getVal());
  }

  @Test
  public void testAttackActionUtilWeaponExpires() {
    this.hero1.arm(this.weapon1);
    this.hero2.arm(this.weapon2);

    while (this.hero1.canDamage() || this.hero2.canDamage()) {
      if (this.hero1.canDamage()) {
        hero1AttackHero2();
      }
      if (this.hero2.canDamage()) {
        hero2AttackHero1();
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

    this.hero1.arm(this.weapon1);
    this.hero2.arm(this.weapon2);

    assertTrue(this.hero1.canDamage());
    assertTrue(this.hero2.canDamage());

    while (this.hero1.canDamage() || this.hero2.canDamage()) {
      if (this.hero1.canDamage()) {
        hero1AttackHero2();
      }
      if (this.hero2.canDamage()) {
        hero2AttackHero1();
      }
    }
    assertFalse(this.hero1.canDamage());
    assertFalse(this.hero2.canDamage());
  }

  @Test
  public void testArmorUp() {
    assertEquals(0, this.hero1.getArmorAttr().getVal());
    hero1ArmorUp();
    assertEquals(this.armorGain, this.hero1.getArmorAttr().getVal());
    hero1ArmorUp();
    assertEquals(this.armorGain * 2, this.hero1.getArmorAttr().getVal());

    assertEquals(0, this.hero2.getArmorAttr().getVal());
    hero2ArmorUp();
    assertEquals(this.armorGain, this.hero2.getArmorAttr().getVal());
    hero2ArmorUp();
    assertEquals(this.armorGain * 2, this.hero2.getArmorAttr().getVal());
  }

  @Test
  public void testArmorUpAttackMixture() {
    assertEquals(0, this.hero1.getArmorAttr().getVal());

    this.hero1.arm(this.weapon1);
    this.hero2.arm(this.weapon2);

    hero1ArmorUp();
    assertEquals(this.armorGain, this.hero1.getArmorAttr().getVal());
    hero2AttackHero1();
    assertEquals(0, this.hero1.getArmorAttr().getVal());
    assertEquals(HeroFactory.HEALTH + this.armorGain - this.weaponAttackVal2, this.hero1.getHealthAttr().getVal());
  }

  @Test
  public void testHeroAttackMinion() {
    this.hero1.arm(this.weapon1);
    Minion yeti = this.minionFactory2.createMinionByName(ConstMinion.CHILLWIND_YETI);

    this.gm.factory1.attackFactory.getPhysicalDamageAction(yeti, this.hero1).act();
    assertEquals(0, yeti.getHealthLoss());
    assertEquals(yeti.getAttackAttr().getVal(), this.hero1.getHealthLoss());
  }

  @Test
  public void testMinionAttackHero() {
    this.hero1.arm(this.weapon1);
    Minion yeti = this.minionFactory2.createMinionByName(ConstMinion.CHILLWIND_YETI);
    this.gm.factory1.attackFactory.getPhysicalDamageAction(this.hero1, yeti).act();

    assertEquals(this.weapon1.getAttackAttr().getVal(), yeti.getHealthLoss());
    assertEquals(yeti.getAttackAttr().getVal(), this.hero1.getHealthLoss());
  }

  private void hero1ArmorUp() {
    this.effectFactory1.getActionsByConfig(this.armorUp, this.hero1).stream().forEach(Action::act);
  }

  private void hero2ArmorUp() {
    this.effectFactory1.getActionsByConfig(this.armorUp, this.hero2).stream().forEach(Action::act);
  }

  private void hero1AttackHero2() {
    this.gm.factory1.attackFactory.getPhysicalDamageAction(this.hero1, this.hero2).act();
  }

  private void hero2AttackHero1() {
    this.gm.factory2.attackFactory.getPhysicalDamageAction(this.hero2, this.hero1).act();
  }

}
