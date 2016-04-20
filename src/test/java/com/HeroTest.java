package com;

import com.herthrone.action.ActionFactory;
import com.herthrone.base.BaseCard;
import com.herthrone.base.Battlefield;
import com.herthrone.base.Hero;
import com.herthrone.base.Weapon;
import com.herthrone.card.factory.EffectFactory;
import com.herthrone.card.factory.HeroFactory;
import com.herthrone.card.factory.WeaponFactory;
import com.herthrone.card.weapon.Constants;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.container.Board;
import com.herthrone.container.Container;
import com.herthrone.exception.HeroNotFoundException;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

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
  private Board board1;
  private Board board2;
  private Battlefield battlefield1;
  private Battlefield battlefield2;

  private ActionFactory armorUpActionGenerator1;
  private ActionFactory armorUpActionGenerator2;
  private EffectFactory effectFactory1;
  private EffectFactory effectFactory2;

  private Weapon weapon1;
  private Weapon weapon2;

  @Before
  public void setUp() throws FileNotFoundException, HeroNotFoundException {
    final int handCapacity = Integer.parseInt(ConfigLoader.getResource().getString("hand_max_capacity"));
    final int deckCapacity = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    this.hero1 = HeroFactory.createHeroByName("Garrosh Hellscream");
    this.hero2 = HeroFactory.createHeroByName("Garrosh Hellscream");
    this.hand1 = new Container<>(handCapacity);
    this.hand2 = new Container<>(handCapacity);
    this.deck1 = new Container<>(deckCapacity);
    this.deck2 = new Container<>(deckCapacity);
    this.board1 = new Board();
    this.board2 = new Board();

    this.battlefield1 = new Battlefield(this.hero1, this.hero2, this.hand1, this.hand2, this.deck1, this.deck2, this.board1, this.board2);
    this.battlefield2 = new Battlefield(this.hero2, this.hero1, this.hand2, this.hand1, this.deck2, this.deck1, this.board2, this.board1);

    this.effectFactory1 = new EffectFactory(this.battlefield1.getMySide());
    this.effectFactory2 = new EffectFactory(this.battlefield2.getMySide());

    this.armorUpActionGenerator1 = this.effectFactory1.getArmorActionGenerator(this.armorGain);
    this.armorUpActionGenerator2 = this.effectFactory2.getArmorActionGenerator(this.armorGain);


    this.weapon1 = WeaponFactory.createWeapon(0, this.weaponAttackVal1, this.weaponDurability1, Constants.FIERY_WAR_AEX, "Warrior");
    this.weapon2 = WeaponFactory.createWeapon(0, this.weaponAttackVal2, this.weaponDurability2, Constants.FIERY_WAR_AEX, "Warrior");
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
    this.hero2.yieldAttackAction(this.hero1).act();
    assertEquals(0, this.hero1.getArmorAttr().getVal());
    assertEquals(HeroFactory.HEALTH + this.armorGain - this.weaponAttackVal2, this.hero1.getHealthAttr().getVal());
  }

}
