package com.hearthrone.card;

import com.herthrone.action.ActionFactory;
import com.herthrone.base.Battlefield;
import com.herthrone.base.Hero;
import com.herthrone.base.Spell;
import com.herthrone.base.Weapon;
import com.herthrone.card.factory.HeroFactory;
import com.herthrone.card.factory.HeroPowerFactory;
import com.herthrone.card.weapon.Constants;
import com.herthrone.card.factory.WeaponFactory;
import com.herthrone.container.Board;
import com.herthrone.container.Deck;
import com.herthrone.container.Hand;
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

  private Hero hero1;
  private Hero hero2;
  private Hand hand1;
  private Hand hand2;
  private Deck deck1;
  private Deck deck2;
  private Board board1;
  private Board board2;
  private Battlefield battlefield1;
  private Battlefield battlefield2;

  private ActionFactory heroPower1;
  private ActionFactory heroPower2;
  private HeroPowerFactory heroPowerFactory1;
  private HeroPowerFactory heroPowerFactory2;

  private Weapon weapon1;
  private Weapon weapon2;

  @Before
  public void setUp() {
    this.hero1 = HeroFactory.createHeroByName("FOO");
    this.hero2 = HeroFactory.createHeroByName("BAR");
    this.hand1 = new Hand();
    this.hand2 = new Hand();
    this.deck1 = new Deck();
    this.deck2 = new Deck();
    this.board1 = new Board();
    this.board2 = new Board();

    this.battlefield1 = new Battlefield(this.hero1, this.hero2, this.hand1, this.hand2, this.deck1, this.deck2, this.board1, this.board2);
    this.battlefield2 = new Battlefield(this.hero2, this.hero1, this.hand2, this.hand1, this.deck2, this.deck1, this.board2, this.board1);

    this.heroPowerFactory1 = new HeroPowerFactory(this.battlefield1);
    this.heroPowerFactory2 = new HeroPowerFactory(this.battlefield2);

    this.heroPower1 = this.heroPowerFactory1.getArmorUp();
    this.heroPower2 = this.heroPowerFactory2.getArmorUp();


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
    this.heroPower1.yieldActions().stream().forEach(action -> action.act());
    assertEquals(2, this.hero1.getArmorAttr().getVal());
    this.heroPower1.yieldActions().forEach(action -> action.act());
    assertEquals(4, this.hero1.getArmorAttr().getVal());

    assertEquals(0, this.hero2.getArmorAttr().getVal());
    this.heroPower2.yieldActions().forEach(action -> action.act());
    assertEquals(2, this.hero2.getArmorAttr().getVal());
    this.heroPower2.yieldActions().forEach(action -> action.act());
    assertEquals(4, this.hero2.getArmorAttr().getVal());
  }

  @Test
  public void testArmorUpAttackMixture() {
    assertEquals(0, this.hero1.getArmorAttr().getVal());

    this.hero1.equipWeapon(this.weapon1);
    this.hero2.equipWeapon(this.weapon2);

    this.heroPower1.yieldActions().forEach(action -> action.act());
    assertEquals(2, this.hero1.getArmorAttr().getVal());
    this.hero2.yieldAttackAction(this.hero1).act();
    assertEquals(0, this.hero1.getArmorAttr().getVal());
    assertEquals(HeroFactory.HEALTH + 2 - this.weaponAttackVal2, this.hero1.getHealthAttr().getVal());
  }

}
