package com.herthrone.base;

import com.herthrone.configuration.MechanicConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstTrigger;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.HeroFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.factory.WeaponFactory;
import com.herthrone.game.GameManager;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by yifeng on 4/4/16.
 */
@RunWith(JUnit4.class)
public class HeroTest extends TestCase {

  private final int weaponAttackVal1 = 2;
  private final int weaponAttackVal2 = 3;
  private final int weaponDurability1 = 2;
  private final int weaponDurability2 = 3;
  private final int armorGain = 2;

  private Hero hero1;
  private Hero hero2;
  private Minion yeti;
  private Weapon weapon1;
  private Weapon weapon2;
  private GameManager gm;

  @Before
  public void setUp() {
    this.gm = new GameManager(ConstHero.GARROSH_HELLSCREAM, ConstHero.GARROSH_HELLSCREAM,
        Collections.emptyList(), Collections.emptyList());
    this.hero1 = gm.activeSide.hero;
    this.hero2 = gm.inactiveSide.hero;

    this.yeti = MinionFactory.create(ConstMinion.CHILLWIND_YETI);
    gm.activeSide.bind(yeti);
    final Map<ConstTrigger, List<MechanicConfig>> emptyMap = Collections.emptyMap();
    this.weapon1 = WeaponFactory.create(ConstWeapon.FIERY_WAR_AXE, "", ConstClass.WARRIOR,
        weaponAttackVal1, weaponDurability1, 0, true, emptyMap);
    gm.activeSide.bind(weapon1);
    this.weapon2 = WeaponFactory.create(ConstWeapon.FIERY_WAR_AXE, "", ConstClass.WARRIOR,
        weaponAttackVal2, weaponDurability2, 0, true, emptyMap);
    gm.inactiveSide.bind(weapon2);
  }

  @Test
  public void testHeroHealth() {
    assertThat(hero1.health().value()).isEqualTo(HeroFactory.HEALTH);
    assertThat(hero2.health().value()).isEqualTo(HeroFactory.HEALTH);
  }

  @Test
  public void testAttackAction() {
    hero1.equip(weapon1);
    hero2.equip(weapon2);

    hero1AttackHero2();

    // Even if hero2 has weapon, hero1 won't take any damage from hero2 while attacking.
    assertThat(hero1.health().value()).isEqualTo(HeroFactory.HEALTH);
    assertThat(hero2.health().value()).isEqualTo(HeroFactory.HEALTH - weaponAttackVal1);

    assertThat(weapon1.getDurabilityAttr().value()).isEqualTo(weaponDurability1 - 1);
    assertThat(weapon2.getDurabilityAttr().value()).isEqualTo(weaponDurability2);
  }

  private void hero1AttackHero2() {
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(hero1, hero2);
  }

  @Test
  public void testAttackActionUtilWeaponExpires() {
    hero1.equip(weapon1);
    hero2.equip(weapon2);

    while (hero1.canDamage() || hero2.canDamage()) {
      if (hero1.canDamage()) {
        hero1AttackHero2();
      }
      if (hero2.canDamage()) {
        hero2AttackHero1();
      }
    }

    assertThat(hero1.health().value()).isEqualTo(HeroFactory.HEALTH - weaponAttackVal2 * weaponDurability2);
    assertThat(hero2.health().value()).isEqualTo(HeroFactory.HEALTH - weaponAttackVal1 * weaponDurability1);

    assertThat(weapon1.getDurabilityAttr().value()).isEqualTo(0);
    assertThat(weapon2.getDurabilityAttr().value()).isEqualTo(0);
  }

  private void hero2AttackHero1() {
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(hero2, hero1);
  }

  @Test
  public void testCanAttack() {
    assertThat(hero1.canDamage()).isFalse();
    assertThat(hero2.canDamage()).isFalse();

    hero1.equip(weapon1);
    hero2.equip(weapon2);

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
    assertThat(hero1.canDamage()).isFalse();
    assertThat(hero2.canDamage()).isFalse();
  }

  @Test
  public void testArmorUp() {
    assertEquals(0, hero1.armor().value());
    hero1ArmorUp();
    assertEquals(armorGain, hero1.armor().value());
    hero1ArmorUp();
    assertEquals(armorGain * 2, hero1.armor().value());

    assertEquals(0, hero2.armor().value());
    hero2ArmorUp();
    assertEquals(armorGain, hero2.armor().value());
    hero2ArmorUp();
    assertEquals(armorGain * 2, hero2.armor().value());
  }

  private void hero1ArmorUp() {
    EffectFactory.pipeEffects(hero1.getHeroPower(), hero1);
  }

  private void hero2ArmorUp() {
    EffectFactory.pipeEffects(hero2.getHeroPower(), hero2);
  }

  @Test
  public void testArmorUpAttackMixture() {
    assertEquals(0, hero1.armor().value());

    hero1.equip(weapon1);
    hero2.equip(weapon2);

    hero1ArmorUp();
    assertEquals(armorGain, hero1.armor().value());
    hero2AttackHero1();
    assertEquals(0, hero1.armor().value());
    assertEquals(HeroFactory.HEALTH + armorGain - weaponAttackVal2, hero1.health().value());
  }

  @Test
  public void testHeroAttackMinion() {
    hero1.equip(weapon1);

    EffectFactory.AttackFactory.pipePhysicalDamageEffect(yeti, hero1);
    assertEquals(0, yeti.healthLoss());
    assertEquals(yeti.attack().value(), hero1.healthLoss());
  }

  @Test
  public void testMinionAttackHero() {
    hero1.equip(weapon1);
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(hero1, yeti);

    assertEquals(weapon1.getAttackAttr().value(), yeti.healthLoss());
    assertEquals(yeti.attack().value(), hero1.healthLoss());
  }

}
