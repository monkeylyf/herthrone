package com.herthrone.base;

import com.google.common.base.Optional;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstType;
import com.herthrone.factory.AttackFactory;
import com.herthrone.factory.EffectFactory;
import com.herthrone.factory.MinionFactory;
import com.herthrone.game.Battlefield;
import com.herthrone.game.GameManager;
import com.herthrone.game.Side;
import com.herthrone.stats.BooleanAttribute;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by yifengliu on 5/10/16.
 */
public class MechanicTest extends TestCase {

  private Hero hero;
  private Side side;
  private AttackFactory attackFactory;
  private Battlefield battlefield;
  private MinionFactory minionFactory;
  private EffectFactory effectFactory;

  private GameManager gm;

  @Before
  public void setUp() {
    this.gm = new GameManager(ConstHero.GARROSH_HELLSCREAM, ConstHero.GARROSH_HELLSCREAM, Collections.emptyList(), Collections.emptyList());
    this.side = gm.battlefield1.mySide;
    this.hero = side.hero;
    this.battlefield = gm.battlefield1;
    this.attackFactory = gm.factory1.attackFactory;
    this.minionFactory = gm.factory1.minionFactory;
    this.effectFactory = gm.factory1.effectFactory;
  }

  @Test
  public void testCharge() {
    final ConstMinion minionName = ConstMinion.WOLFRIDER;
    final MinionConfig config = ConfigLoader.getMinionConfigByName(minionName);
    final Optional<MechanicConfig> mechanic = config.getMechanic(ConstMechanic.CHARGE);
    assertThat(mechanic.isPresent()).isTrue();
    final Minion minion = minionFactory.createMinionByName(minionName);
    assertThat(minion.getAttackMovePoints().getVal()).isGreaterThan(0);
  }

  @Test
  public void testBattlecry() {
    final Minion yeti = minionFactory.createMinionByName(ConstMinion.CHILLWIND_YETI);
    side.deck.add(yeti);

    assertThat(side.deck.size()).isEqualTo(1);
    assertThat(side.board.size()).isEqualTo(0);
    assertThat(side.hand.size()).isEqualTo(0);

    final ConstMinion minionName = ConstMinion.GNOMISH_INVENTOR;
    final Minion minion = minionFactory.createMinionByName(minionName);

    gm.playCard(minion);

    assertThat(side.board.get(0).getCardName()).isEqualTo(minionName.toString());
    assertThat(side.deck.size()).isEqualTo(0);
    assertThat(side.board.size()).isEqualTo(1);
    assertThat(side.hand.size()).isEqualTo(1);
  }

  @Test
  public void testBattlecryDrawCardWithFatigue() {
    assertThat(side.deck.size()).isEqualTo(0);
    assertThat(side.board.size()).isEqualTo(0);
    assertThat(side.hand.size()).isEqualTo(0);
    assertThat(side.hero.getHealthLoss()).isEqualTo(0);

    final ConstMinion minionName = ConstMinion.GNOMISH_INVENTOR;
    final Minion minion = minionFactory.createMinionByName(ConstMinion.GNOMISH_INVENTOR);

    gm.playCard(minion);

    assertThat(side.board.get(0).getCardName()).isEqualTo(minionName.toString());
    assertThat(side.deck.size()).isEqualTo(0);
    assertThat(side.board.size()).isEqualTo(1);
    assertThat(side.hand.size()).isEqualTo(0);
    // Battlecry draw card causing fatigue damage.
    assertThat(side.hero.getHealthLoss()).isEqualTo(1);
  }

  @Test
  public void testElusive() {
    final Minion faerieDragon = minionFactory.createMinionByName(ConstMinion.FAERIE_DRAGON);
    assertThat(GameManager.isMinionTargetable(faerieDragon, side.board, ConstType.SPELL)).isFalse();

    final Minion yeti = minionFactory.createMinionByName(ConstMinion.CHILLWIND_YETI);
    assertThat(GameManager.isMinionTargetable(yeti, side.board, ConstType.SPELL)).isTrue();
  }

  @Test
  public void testTaunt() {
    final Minion yeti = minionFactory.createMinionByName(ConstMinion.CHILLWIND_YETI);
    final Minion senjin = minionFactory.createMinionByName(ConstMinion.SENJIN_SHIELDMASTA);
    final Minion grizzly = minionFactory.createMinionByName(ConstMinion.IRONFUR_GRIZZLY);
    side.board.add(yeti);
    side.board.add(senjin);
    side.board.add(grizzly);

    assertThat(GameManager.isMinionTargetable(yeti, side.board, ConstType.ATTACK)).isFalse();
    assertThat(GameManager.isHeroTargetable(hero, side.board, ConstType.ATTACK)).isFalse();
    assertThat(GameManager.isMinionTargetable(senjin, side.board, ConstType.ATTACK)).isTrue();
    assertThat(GameManager.isMinionTargetable(grizzly, side.board, ConstType.ATTACK)).isTrue();
  }

  @Test
  public void testDivineShield() {
    final Minion yeti = minionFactory.createMinionByName(ConstMinion.CHILLWIND_YETI);
    final Minion scarletCrusader = minionFactory.createMinionByName(ConstMinion.SCARLET_CRUSADER);

    assertThat(scarletCrusader.getBooleanMechanics().get(ConstMechanic.DIVINE_SHIELD).isPresent()).isTrue();
    final BooleanAttribute divineShield = scarletCrusader.getBooleanMechanics().get(ConstMechanic
        .DIVINE_SHIELD).get();
    assertThat(divineShield.isOn()).isTrue();

    attackFactory.getPhysicalDamageAction(yeti, scarletCrusader).act();

    // Yeti takes damage. Crusader takes no damage because of divine shield.
    assertThat(divineShield.isOn()).isFalse();
    assertThat(scarletCrusader.getHealthLoss()).isEqualTo(0);
    assertThat(yeti.getHealthLoss()).isGreaterThan(0);

    attackFactory.getPhysicalDamageAction(yeti, scarletCrusader).act();

    // Crusader has no more divine shield and takes damage.
    assertThat(scarletCrusader.isDead()).isTrue();
    assertThat(yeti.isDead()).isTrue();
  }

  @Test
  public void testFreeze() {
    final Minion waterElemental = minionFactory.createMinionByName(ConstMinion.WATER_ELEMENTAL);
    final Minion scarletCrusader = minionFactory.createMinionByName(ConstMinion.SCARLET_CRUSADER);
    final Minion yeti = minionFactory.createMinionByName(ConstMinion.CHILLWIND_YETI);

    // Scarlet crusader has divine shield so take no damage. No damage no frozen.
    attackFactory.getPhysicalDamageAction(waterElemental, scarletCrusader).act();
    assertThat(scarletCrusader.getHealthLoss()).isEqualTo(0);
    assertThat(scarletCrusader.getBooleanMechanics().get(ConstMechanic.FROZEN).isPresent()).isFalse();

    // Yeti takes damage and gets frozen.
    attackFactory.getPhysicalDamageAction(waterElemental, yeti).act();
    final Optional<BooleanAttribute> frozen = yeti.getBooleanMechanics().get(ConstMechanic.FROZEN);
    assertThat(yeti.getHealthLoss()).isGreaterThan(0);
    assertThat(frozen.isPresent()).isTrue();
    assertThat(frozen.get().isOn()).isTrue();
  }
}
