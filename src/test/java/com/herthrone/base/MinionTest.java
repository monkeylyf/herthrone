package com.herthrone.base;

import com.herthrone.card.factory.EffectFactory;
import com.herthrone.card.factory.MinionFactory;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.exception.CardNotFoundException;
import com.herthrone.exception.MinionNotFoundException;
import com.herthrone.game.Battlefield;
import com.herthrone.game.Constants;
import com.herthrone.game.Container;
import com.herthrone.game.GameManager;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Collections;

/**
 * Created by yifeng on 4/15/16.
 */
public class MinionTest extends TestCase {

  private Hero hero1;
  private Hero hero2;
  private Container<BaseCard> hand1;
  private Container<BaseCard> hand2;
  private Container<BaseCard> deck1;
  private Container<BaseCard> deck2;
  private Container<Minion> board1;
  private Container<Minion> board2;
  private Battlefield battlefield1;
  private Battlefield battlefield2;

  private EffectFactory effectFactory1;
  private EffectFactory effectFactory2;
  private MinionFactory minionFactory1;
  private MinionFactory minionFactory2;

  private Minion minion1;
  private Minion minion2;

  private MinionConfig yetiConfig;

  private GameManager gm;

  @Before
  public void setUp() throws FileNotFoundException, CardNotFoundException {
    this.gm = new GameManager(Constants.Hero.GULDAN, Constants.Hero.GULDAN, Collections.emptyList(), Collections.emptyList());
    this.hero1 = this.gm.getHero1();
    this.hero2 = this.gm.getHero2();
    this.hand1 = this.gm.getHand1();
    this.hand2 = this.gm.getHand2();
    this.deck1 = this.gm.getDeck1();
    this.deck1 = this.gm.getDeck2();
    this.board1 = this.gm.getBoard1();
    this.board2 = this.gm.getBoard2();
    this.battlefield1 = this.gm.getBattlefield1();
    this.battlefield2 = this.gm.getBattlefield2();

    this.minionFactory1 = this.gm.factory1.minionFactory;
    this.minionFactory2 = this.gm.factory2.minionFactory;
    this.effectFactory1 = this.gm.factory1.effectFactory;
    this.effectFactory2 = this.gm.factory2.effectFactory;

    this.minion1 = this.minionFactory1.createMinionByName(Constants.Minion.CHILLWIND_YETI);
    this.minion2 = this.minionFactory1.createMinionByName(Constants.Minion.CHILLWIND_YETI);

    this.yetiConfig = ConfigLoader.getMinionConfigByName(Constants.Minion.CHILLWIND_YETI);
  }

  @Test
  public void testMinionStats() throws FileNotFoundException, MinionNotFoundException {
    MinionConfig config = ConfigLoader.getMinionConfigByName(Constants.Minion.CHILLWIND_YETI);
    assertEquals(config.getHealth(), this.minion1.getHealthAttr().getVal());
    assertEquals(config.getHealth(), this.minion2.getHealthAttr().getVal());
    assertFalse(this.minion1.isDead());
    assertFalse(this.minion2.isDead());
  }

  @Test
  public void testMinionAttack() {
    attackEachOther();
    assertEquals(this.yetiConfig.getHealth() - this.yetiConfig.getAttack(), this.minion1.getHealthAttr().getVal());
    assertEquals(this.yetiConfig.getHealth() - this.yetiConfig.getAttack(), this.minion2.getHealthAttr().getVal());
    attackEachOther();
    assertEquals(this.yetiConfig.getHealth() - this.yetiConfig.getAttack() * 2, this.minion1.getHealthAttr().getVal());
    assertEquals(this.yetiConfig.getHealth() - this.yetiConfig.getAttack() * 2, this.minion2.getHealthAttr().getVal());
    assertTrue(this.minion1.isDead());
    assertTrue(this.minion2.isDead());
  }

  private void attackEachOther() {
    this.gm.factory1.attackFactory.getPhysicalDamageAction(this.minion1, this.minion2).act();
  }
}


