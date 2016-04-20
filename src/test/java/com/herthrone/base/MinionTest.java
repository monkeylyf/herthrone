package com.herthrone.base;

import com.herthrone.card.factory.ActionFactory;
import com.herthrone.card.factory.EffectFactory;
import com.herthrone.card.factory.HeroFactory;
import com.herthrone.card.factory.MinionFactory;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.container.Board;
import com.herthrone.container.Container;
import com.herthrone.exception.HeroNotFoundException;
import com.herthrone.exception.MinionNotFoundException;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * Created by yifeng on 4/15/16.
 */
public class MinionTest extends TestCase {

  private static final String YETI = "Chillwind Yeti";

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
  private EffectFactory effectFactory1;
  private EffectFactory effectFactory2;
  private MinionFactory minionFactory1;
  private MinionFactory minionFactory2;

  private Minion minion1;
  private Minion minion2;

  @Before
  public void setUp() throws FileNotFoundException, HeroNotFoundException, MinionNotFoundException {
    final int handCapacity = Integer.parseInt(ConfigLoader.getResource().getString("hand_max_capacity"));
    final int deckCapacity = Integer.parseInt(ConfigLoader.getResource().getString("deck_max_capacity"));
    final int boardCapacity = Integer.parseInt(ConfigLoader.getResource().getString("board_max_capacity"));

    this.hero1 = HeroFactory.createHeroByName("Gul'dan");
    this.hero2 = HeroFactory.createHeroByName("Gul'dan");

    this.hand1 = new Container<>(handCapacity);
    this.hand2 = new Container<>(handCapacity);
    this.deck1 = new Container<>(deckCapacity);
    this.deck2 = new Container<>(deckCapacity);
    this.board1 = new Container<>(boardCapacity);
    this.board2 = new Container<>(boardCapacity);
    this.secrets1 = new Container<>();
    this.secrets2 = new Container<>();

    this.battlefield1 = new Battlefield(this.hero1, this.hero2, this.hand1, this.hand2, this.deck1, this.deck2, this.board1, this.board2, this.secrets1, this.secrets2);
    this.battlefield2 = new Battlefield(this.hero2, this.hero1, this.hand2, this.hand1, this.deck2, this.deck1, this.board2, this.board1, this.secrets2, this.secrets1);

    this.minionFactory1 = new MinionFactory(this.battlefield1);
    this.minionFactory2 = new MinionFactory(this.battlefield2);
    this.effectFactory1 = new EffectFactory(this.minionFactory1, this.battlefield1);
    this.effectFactory2 = new EffectFactory(this.minionFactory2, this.battlefield2);

    this.minion1 = this.minionFactory1.createMinionByName(MinionTest.YETI);
    this.minion2 = this.minionFactory1.createMinionByName(MinionTest.YETI);
  }

  @Test
  public void testMinionStats() throws FileNotFoundException, MinionNotFoundException {
    MinionConfig config = ConfigLoader.getMinionConfigByName(MinionTest.YETI);
    assertEquals(config.getHealth(), this.minion1.getHealthAttr().getVal());
    assertEquals(config.getHealth(), this.minion2.getHealthAttr().getVal());
  }
}


