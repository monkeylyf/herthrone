package com;

import com.herthrone.Constants;
import com.herthrone.GameManager;
import com.herthrone.base.*;
import com.herthrone.card.factory.ActionFactory;
import com.herthrone.card.factory.EffectFactory;
import com.herthrone.card.factory.MinionFactory;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.exception.CardNotFoundException;
import com.herthrone.exception.SpellNotFoundException;
import junit.framework.TestCase;
import org.junit.Before;

import java.io.FileNotFoundException;
import java.util.Collections;

/**
 * Created by yifeng on 4/20/16.
 */
public class SpellTest extends TestCase{

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

  private MinionFactory minionFactory1;
  private MinionFactory minionFactory2;
  private EffectFactory effectFactory1;
  private EffectFactory effectFactory2;

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
  }

  public void testFireBall() throws FileNotFoundException, SpellNotFoundException {
    final String spellName = "FireBall";
    SpellConfig fireBallConfig = ConfigLoader.getSpellConfigByName(spellName);
    System.out.println(fireBallConfig.getClassName());
    System.out.println(fireBallConfig.getEffects());
    Spell fireBall = this.gm.factory1.spellFactory.createSpellByName(spellName);

    System.out.println(fireBall.getCardName());
    System.out.println(fireBall.getClassName());
  }
}
