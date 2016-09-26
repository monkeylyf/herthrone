package com.herthrone.base;

import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstWeapon;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.HeroFactory;
import com.herthrone.factory.WeaponFactory;
import com.herthrone.game.Game;
import com.herthrone.service.Command;
import com.herthrone.service.CommandType;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class HeroTest extends TestCase {

  private Hero garrosh1;
  private Hero garrosh2;
  private Minion yeti;
  private Weapon weapon1;
  private Weapon weapon2;
  private int weaponAttack;
  private int weaponDurability;
  private Game game;
  private int armorGain = 2;

  @Before
  public void setUp() {
    final List<Enum> cards = Collections.nCopies(30, ConstMinion.CHILLWIND_YETI);
    this.game = new Game("gameId", ConstHero.GARROSH_HELLSCREAM, ConstHero.GARROSH_HELLSCREAM,
        cards, cards);
    this.garrosh1 = game.activeSide.hero;
    this.garrosh2 = game.inactiveSide.hero;

    this.weapon1 = WeaponFactory.create(ConstWeapon.FIERY_WAR_AXE);
    this.weapon2 = WeaponFactory.create(ConstWeapon.FIERY_WAR_AXE);
    this.weaponAttack = weapon1.getAttackAttr().value();
    this.weaponDurability = weapon1.getDurabilityAttr().value();

    game.startTurn();
    garrosh1PlayYeti();
  }

  private void garrosh1PlayYeti() {
    final Command playYetiCommand = Command.newBuilder()
        .setBoardPosition(0)
        .setType(CommandType.PLAY_CARD)
        .setDoerId("o:h:0")
        .build();
    game.command(playYetiCommand);
    this.yeti = game.activeSide.board.get(0);
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
    assertEquals(0, garrosh1.armor().value());
    hero1ArmorUp();
    assertEquals(armorGain, garrosh1.armor().value());
    hero1ArmorUp();
    assertEquals(armorGain * 2, garrosh1.armor().value());

    assertEquals(0, garrosh2.armor().value());
    hero2ArmorUp();
    assertEquals(armorGain, garrosh2.armor().value());
    hero2ArmorUp();
    assertEquals(armorGain * 2, garrosh2.armor().value());
  }

  private void hero1ArmorUp() {
    EffectFactory.pipeEffects(garrosh1.getHeroPower(), garrosh1);
  }

  private void hero2ArmorUp() {
    EffectFactory.pipeEffects(garrosh2.getHeroPower(), garrosh2);
  }

  @Test
  public void testArmorUpAttackMixture() {
    assertEquals(0, garrosh1.armor().value());

    garrosh1.equip(weapon1);
    garrosh2.equip(weapon2);

    hero1ArmorUp();
    assertEquals(armorGain, garrosh1.armor().value());
    hero2AttackHero1();
    assertEquals(0, garrosh1.armor().value());
    assertEquals(HeroFactory.HEALTH + armorGain - weaponAttack, garrosh1.health().value());
  }

  @Test
  public void testMinionAttackHero() {
    garrosh1.equip(weapon1);

    EffectFactory.AttackFactory.pipePhysicalDamageEffect(yeti, garrosh1);
    assertEquals(0, yeti.healthLoss());
    assertEquals(yeti.attack().value(), garrosh1.healthLoss());
  }

  @Test
  public void testHeroAttackMinion() {
    garrosh2.equip(weapon1);
    final Command attackCommand = Command.newBuilder()
        .setType(CommandType.ATTACK)
        .setDoerId("f:r:0")
        .setTargetId("o:b:0")
        .build();

    game.command(attackCommand);
    assertEquals(weapon1.getAttackAttr().value(), yeti.healthLoss());
    assertEquals(yeti.attack().value(), garrosh2.healthLoss());
  }

}
