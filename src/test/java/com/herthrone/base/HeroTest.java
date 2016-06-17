package com.herthrone.base;

import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.HeroFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.game.Battlefield;
import com.herthrone.game.GameManager;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;

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
    this.hero1 = gm.battlefield1.mySide.hero;
    this.hero2 = gm.battlefield1.opponentSide.hero;
    this.battlefield1 = gm.battlefield1;
    this.battlefield2 = gm.battlefield2;

    this.minionFactory1 = gm.factory1.minionFactory;
    this.minionFactory2 = gm.factory2.minionFactory;
    this.effectFactory1 = gm.factory1.effectFactory;
    this.effectFactory2 = gm.factory2.effectFactory;

    this.armorUp = ConfigLoader.getHeroPowerConfigByName(ConstSpell.ARMOR_UP);

    this.weapon1 = gm.factory1.weaponFactory.createWeapon(0, weaponAttackVal1, weaponDurability1, ConstWeapon.FIERY_WAR_AXE, ConstClass.WARRIOR, true);
    this.weapon2 = gm.factory2.weaponFactory.createWeapon(0, weaponAttackVal2, weaponDurability2, ConstWeapon.FIERY_WAR_AXE, ConstClass.WARRIOR, true);
  }

  @Test
  public void testHeroHealth() {
    assertEquals(HeroFactory.HEALTH, hero1.getHealthAttr().getVal());
    assertEquals(HeroFactory.HEALTH, hero2.getHealthAttr().getVal());
  }

  @Test
  public void testAttackAction() {
    hero1.arm(weapon1);
    hero2.arm(weapon2);

    hero1AttackHero2();

    // Even if hero2 has weapon, hero1 won't take any damage from hero2 while attacking.
    assertEquals(HeroFactory.HEALTH, hero1.getHealthAttr().getVal());
    assertEquals(HeroFactory.HEALTH - weaponAttackVal1, hero2.getHealthAttr().getVal());

    assertEquals(weaponDurability1 - 1, weapon1.getDurabilityAttr().getVal());
    assertEquals(weaponDurability2, weapon2.getDurabilityAttr().getVal());
  }

  private void hero1AttackHero2() {
    gm.factory1.attackFactory.getPhysicalDamageAction(hero1, hero2).act();
  }

  @Test
  public void testAttackActionUtilWeaponExpires() {
    hero1.arm(weapon1);
    hero2.arm(weapon2);

    while (hero1.canDamage() || hero2.canDamage()) {
      if (hero1.canDamage()) {
        hero1AttackHero2();
      }
      if (hero2.canDamage()) {
        hero2AttackHero1();
      }
    }

    assertEquals(HeroFactory.HEALTH - weaponAttackVal2 * weaponDurability2, hero1.getHealthAttr().getVal());
    assertEquals(HeroFactory.HEALTH - weaponAttackVal1 * weaponDurability1, hero2.getHealthAttr().getVal());

    assertEquals(0, weapon1.getDurabilityAttr().getVal());
    assertEquals(0, weapon2.getDurabilityAttr().getVal());
  }

  private void hero2AttackHero1() {
    gm.factory2.attackFactory.getPhysicalDamageAction(hero2, hero1).act();
  }

  @Test
  public void testCanAttack() {
    assertFalse(hero1.canDamage());
    assertFalse(hero2.canDamage());

    hero1.arm(weapon1);
    hero2.arm(weapon2);

    assertThat(hero1.canDamage()).isTrue();
    assertThat(hero2.canDamage()).isTrue();

    while (hero1.canDamage() || hero2.canDamage()) {
      if (hero1.canDamage()) {
        hero1AttackHero2();
      }
      if (hero2.canDamage()) {
        hero2AttackHero1();
      }
    }
    assertFalse(hero1.canDamage());
    assertFalse(hero2.canDamage());
  }

  @Test
  public void testArmorUp() {
    assertEquals(0, hero1.getArmorAttr().getVal());
    hero1ArmorUp();
    assertEquals(armorGain, hero1.getArmorAttr().getVal());
    hero1ArmorUp();
    assertEquals(armorGain * 2, hero1.getArmorAttr().getVal());

    assertEquals(0, hero2.getArmorAttr().getVal());
    hero2ArmorUp();
    assertEquals(armorGain, hero2.getArmorAttr().getVal());
    hero2ArmorUp();
    assertEquals(armorGain * 2, hero2.getArmorAttr().getVal());
  }

  private void hero1ArmorUp() {
    effectFactory1.getActionsByConfig(armorUp, hero1).stream().forEach(Effect::act);
  }

  private void hero2ArmorUp() {
    effectFactory1.getActionsByConfig(armorUp, hero2).stream().forEach(Effect::act);
  }

  @Test
  public void testArmorUpAttackMixture() {
    assertEquals(0, hero1.getArmorAttr().getVal());

    hero1.arm(weapon1);
    hero2.arm(weapon2);

    hero1ArmorUp();
    assertEquals(armorGain, hero1.getArmorAttr().getVal());
    hero2AttackHero1();
    assertEquals(0, hero1.getArmorAttr().getVal());
    assertEquals(HeroFactory.HEALTH + armorGain - weaponAttackVal2, hero1.getHealthAttr().getVal());
  }

  @Test
  public void testHeroAttackMinion() {
    hero1.arm(weapon1);
    Minion yeti = minionFactory2.createMinionByName(ConstMinion.CHILLWIND_YETI);

    gm.factory1.attackFactory.getPhysicalDamageAction(yeti, hero1).act();
    assertEquals(0, yeti.getHealthLoss());
    assertEquals(yeti.getAttackAttr().getVal(), hero1.getHealthLoss());
  }

  @Test
  public void testMinionAttackHero() {
    hero1.arm(weapon1);
    Minion yeti = minionFactory2.createMinionByName(ConstMinion.CHILLWIND_YETI);
    gm.factory1.attackFactory.getPhysicalDamageAction(hero1, yeti).act();

    assertEquals(weapon1.getAttackAttr().getVal(), yeti.getHealthLoss());
    assertEquals(yeti.getAttackAttr().getVal(), hero1.getHealthLoss());
  }

}
