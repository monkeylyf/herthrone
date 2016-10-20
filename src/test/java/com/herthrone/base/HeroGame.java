package com.herthrone.base;

import com.herthrone.BaseGame;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.HeroFactory;
import com.herthrone.factory.WeaponFactory;
import com.herthrone.service.BoardSide;
import com.herthrone.service.ContainerType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class HeroGame extends BaseGame {

  private Hero garrosh1;
  private Hero garrosh2;
  private Minion yeti;
  private Weapon weapon1;
  private Weapon weapon2;
  private int weaponAttack;
  private int weaponDurability;
  private int armorGain = 2;

  @Before
  public void setUp() {
    setUpGame(ConstHero.GARROSH_HELLSCREAM, ConstHero.GARROSH_HELLSCREAM);
    this.garrosh1 = game.activeSide.hero;
    this.garrosh2 = game.inactiveSide.hero;

    this.weapon1 = weapon.create(ConstWeapon.FIERY_WAR_AXE);
    this.weapon2 = WeaponFactory.create(ConstWeapon.FIERY_WAR_AXE);
    this.weaponAttack = weapon1.getAttackAttr().value();
    this.weaponDurability = weapon1.getDurabilityAttr().value();

    game.startTurn();
    this.yeti = minion.addToHandAndPlay(ConstMinion.CHILLWIND_YETI);
  }

  @Test
  public void testHeroHealth() {
    assertThat(garrosh1.health().value()).isEqualTo(HeroFactory.HEALTH);
    assertThat(garrosh2.health().value()).isEqualTo(HeroFactory.HEALTH);
  }

  @Test
  public void testAttackAction() {
    garrosh1.equip(weapon1);
    garrosh2.equip(weapon2);

    hero1AttackHero2();

    // Even if garrosh2 has weapon, garrosh1 won't take any damage from garrosh2 while attacking.
    assertThat(garrosh1.health().value()).isEqualTo(HeroFactory.HEALTH);
    assertThat(garrosh2.health().value()).isEqualTo(HeroFactory.HEALTH - weaponAttack);

    assertThat(weapon1.getDurabilityAttr().value()).isEqualTo(weaponDurability - 1);
    assertThat(weapon2.getDurabilityAttr().value()).isEqualTo(weaponDurability);
  }

  private void hero1AttackHero2() {
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(garrosh1, garrosh2);
  }

  @Test
  public void testAttackActionUtilWeaponExpires() {
    garrosh1.equip(weapon1);
    garrosh2.equip(weapon2);

    while (garrosh1.canDamage() || garrosh2.canDamage()) {
      if (garrosh1.canDamage()) {
        hero1AttackHero2();
      }
      if (garrosh2.canDamage()) {
        hero2AttackHero1();
      }
    }

    assertThat(garrosh1.health().value()).isEqualTo(HeroFactory.HEALTH - weaponAttack * weaponDurability);
    assertThat(garrosh2.health().value()).isEqualTo(HeroFactory.HEALTH - weaponAttack * weaponDurability);

    assertThat(weapon1.getDurabilityAttr().value()).isEqualTo(0);
    assertThat(weapon2.getDurabilityAttr().value()).isEqualTo(0);
  }

  private void hero2AttackHero1() {
    EffectFactory.AttackFactory.pipePhysicalDamageEffect(garrosh2, garrosh1);
  }

  @Test
  public void testCanAttack() {
    assertThat(garrosh1.canDamage()).isFalse();
    assertThat(garrosh2.canDamage()).isFalse();

    garrosh1.equip(weapon1);
    garrosh2.equip(weapon2);

    assertThat(garrosh1.canDamage()).isTrue();
    assertThat(garrosh2.canDamage()).isTrue();

    while (garrosh1.canDamage() || garrosh2.canDamage()) {
      if (garrosh1.canDamage()) {
        hero1AttackHero2();
      }
      if (garrosh2.canDamage()) {
        hero2AttackHero1();
      }
    }
    assertThat(garrosh1.canDamage()).isFalse();
    assertThat(garrosh2.canDamage()).isFalse();
  }

  @Test
  public void testArmorUp() {
    assertThat(garrosh1.armor().value()).isEqualTo(0);
    heroPower.use();
    assertThat(garrosh1.armor().value()).isEqualTo(armorGain);
    heroPower.use();
    assertThat(garrosh1.armor().value()).isEqualTo(armorGain * 2);
  }

  @Test
  public void testArmorUpAttackMixture() {
    assertThat(garrosh1.armor().value()).isEqualTo(0);
    garrosh1.equip(weapon1);
    garrosh2.equip(weapon2);

    heroPower.use();
    hero2AttackHero1();
    assertThat(garrosh1.armor().value()).isEqualTo(0);
    assertThat(garrosh1.health().value()).isEqualTo(HeroFactory.HEALTH + armorGain - weaponAttack);
  }

  @Test
  public void testMinionAttackHero() {
    garrosh1.equip(weapon1);

    EffectFactory.AttackFactory.pipePhysicalDamageEffect(yeti, garrosh1);
    assertThat(yeti.healthLoss()).isEqualTo(0);
    assertThat(garrosh1.healthLoss()).isEqualTo(yeti.attack().value());
  }

  @Test
  public void testHeroAttackMinion() {
    garrosh2.equip(weapon1);
    action.attack(BoardSide.FOE, ContainerType.HERO, 0, BoardSide.OWN, ContainerType.BOARD, 0);
    assertThat(yeti.healthLoss()).isEqualTo(weapon1.getAttackAttr().value());
    assertThat(garrosh2.healthLoss()).isEqualTo(yeti.attack().value());
  }

}
