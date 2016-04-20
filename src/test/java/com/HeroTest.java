package com;

import com.herthrone.Constants;
import com.herthrone.GameManager;
import com.herthrone.base.*;
import com.herthrone.card.factory.*;
import com.herthrone.base.Container;
import com.herthrone.exception.CardNotFoundException;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
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
  private Container<BaseCard> hand1;
  private Container<BaseCard> hand2;
  private Container<BaseCard> deck1;
  private Container<BaseCard> deck2;
  private Container<Minion> board1;
  private Container<Minion> board2;
  private Container<Secret> secrets1;
  private Container<Secret> secrets2;
  private Battlefield battlefield1;
  private Battlefield battlefield2;

  private ActionFactory armorUpActionGenerator1;
  private ActionFactory armorUpActionGenerator2;
  private MinionFactory minionFactory1;
  private MinionFactory minionFactory2;
  private EffectFactory effectFactory1;
  private EffectFactory effectFactory2;

  private Weapon weapon1;
  private Weapon weapon2;

  private GameManager gm;

  @Before
  public void setUp() throws FileNotFoundException, CardNotFoundException {
    this.gm = new GameManager(Constants.Hero.GARROSH_HELLSCREAM, Constants.Hero.GARROSH_HELLSCREAM, Collections.emptyList(), Collections.emptyList());
    this.hero1 = this.gm.getHero1();
    this.hero2 = this.gm.getHero2();
    this.hand1 = this.gm.getHand1();
    this.hand2 = this.gm.getHand2();
    this.deck1 = this.gm.getDeck1();
    this.deck1 = this.gm.getDeck2();
    this.board1 = this.gm.getBoard1();
    this.board2 = this.gm.getBoard2();
    this.secrets1 = this.gm.getSecrets1();
    this.secrets2 = this.gm.getSecrets2();
    this.battlefield1 = this.gm.getBattlefield1();
    this.battlefield2 = this.gm.getBattlefield2();

    this.minionFactory1 = new MinionFactory(this.battlefield1);
    this.minionFactory2 = new MinionFactory(this.battlefield2);
    this.effectFactory1 = new EffectFactory(this.minionFactory1, this.battlefield1);
    this.effectFactory2 = new EffectFactory(this.minionFactory2, this.battlefield2);

    this.armorUpActionGenerator1 = this.effectFactory1.getArmorActionGenerator(this.armorGain);
    this.armorUpActionGenerator2 = this.effectFactory2.getArmorActionGenerator(this.armorGain);


    this.weapon1 = WeaponFactory.createWeapon(0, this.weaponAttackVal1, this.weaponDurability1, Constants.Weapon.FIERY_WAR_AEX, "Warrior");
    this.weapon2 = WeaponFactory.createWeapon(0, this.weaponAttackVal2, this.weaponDurability2, Constants.Weapon.FIERY_WAR_AEX, "Warrior");
  }

  @Test
  public void testHeroHealth() {
    assertEquals(HeroFactory.HEALTH, this.hero1.getHealthAttr().getVal());
    assertEquals(HeroFactory.HEALTH, this.hero2.getHealthAttr().getVal());
  }

  private void hero1AttackHero2() {
    this.gm.factory1.attackFactory.getPhysicalDamageAction(this.hero1, this.hero2).act();
  }

  private void hero2AttackHero1() {
    this.gm.factory2.attackFactory.getPhysicalDamageAction(this.hero2, this.hero1).act();
  }

  @Test
  public void testAttackAction() {
    this.hero1.equipWeapon(weapon1);
    this.hero2.equipWeapon(weapon2);

    hero1AttackHero2();

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

    this.hero1.equipWeapon(this.weapon1);
    this.hero2.equipWeapon(this.weapon2);

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
    this.armorUpActionGenerator1.yieldActions().stream().forEach(action -> action.act());
    assertEquals(this.armorGain, this.hero1.getArmorAttr().getVal());
    this.armorUpActionGenerator1.yieldActions().forEach(action -> action.act());
    assertEquals(this.armorGain * 2, this.hero1.getArmorAttr().getVal());

    assertEquals(0, this.hero2.getArmorAttr().getVal());
    this.armorUpActionGenerator2.yieldActions().forEach(action -> action.act());
    assertEquals(this.armorGain, this.hero2.getArmorAttr().getVal());
    this.armorUpActionGenerator2.yieldActions().forEach(action -> action.act());
    assertEquals(this.armorGain * 2, this.hero2.getArmorAttr().getVal());
  }

  @Test
  public void testArmorUpAttackMixture() {
    assertEquals(0, this.hero1.getArmorAttr().getVal());

    this.hero1.equipWeapon(this.weapon1);
    this.hero2.equipWeapon(this.weapon2);

    this.armorUpActionGenerator1.yieldActions().forEach(action -> action.act());
    assertEquals(this.armorGain, this.hero1.getArmorAttr().getVal());
    hero2AttackHero1();
    assertEquals(0, this.hero1.getArmorAttr().getVal());
    assertEquals(HeroFactory.HEALTH + this.armorGain - this.weaponAttackVal2, this.hero1.getHealthAttr().getVal());
  }

}
