package com.herthrone.base;

import com.herthrone.constant.ConstMinion;
import com.herthrone.factory.MinionFactory;
import com.herthrone.object.ManaCrystal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class ManaCrystalTest {

  private ManaCrystal manaCrystal;

  @Before
  public void setUp() {
    this.manaCrystal = new ManaCrystal();
  }

  @Test
  public void canPlay() {
    // Turn 1.
    manaCrystal.startTurn();
    final Minion worgenInfiltrator = MinionFactory.create(ConstMinion.WORGEN_INFILTRATOR);
    assertThat(manaCrystal.canPlay(worgenInfiltrator)).isTrue();
    final Minion lootHoarder = MinionFactory.create(ConstMinion.LOOT_HOARDER);
    assertThat(manaCrystal.canPlay(lootHoarder)).isFalse();
  }

  @Test
  public void consume() {
    // Turn 1.
    manaCrystal.startTurn();
    manaCrystal.consume(1);
    assertThat(manaCrystal.getCrystal()).isEqualTo(0);
  }

  @Test
  public void increaseUpperBound() {
    manaCrystal.increaseUpperBound();
    assertThat(manaCrystal.getCrystalUpperBound()).isEqualTo(1);

    final int incrementalValue = 3;
    manaCrystal.increaseUpperBound(incrementalValue);
    assertThat(manaCrystal.getCrystalUpperBound()).isEqualTo(incrementalValue + 1);
  }

  @Test
  public void overload() {
    // Turn 1.
    manaCrystal.startTurn();

    final int overloadValue = 2;
    manaCrystal.overload(overloadValue);

    manaCrystal.endTurn();
    manaCrystal.startTurn();

    // Turn 2. All mana crystal completely locked.
    assertThat(manaCrystal.getCrystal()).isEqualTo(0);

    manaCrystal.endTurn();
    manaCrystal.startTurn();

    // Turn 3. Previously-lock mana crystals all unlocked.
    assertThat(manaCrystal.getCrystal()).isEqualTo(3);
    // Multi-overload.
    manaCrystal.overload(1);
    manaCrystal.overload(1);
    manaCrystal.overload(2);

    manaCrystal.endTurn();
    manaCrystal.startTurn();
    // Turn 4. All mana crystal completely locked.
    assertThat(manaCrystal.getCrystal()).isEqualTo(0);
  }

  @Test
  public void getCrystal() {
    assertThat(manaCrystal.getCrystal()).isEqualTo(0);
  }

  @Test
  public void getCrystalUpperBound() {
    assertThat(manaCrystal.getCrystalUpperBound()).isEqualTo(0);
  }

  @Test
  public void getLockedCrystal() {
    // Turn 1.
    manaCrystal.startTurn();

    assertThat(manaCrystal.getLockedCrystal()).isEqualTo(0);
    final int overloadValue = 2;
    manaCrystal.overload(overloadValue);
    assertThat(manaCrystal.getLockedCrystal()).isEqualTo(0);
    assertThat(manaCrystal.getCrystal()).isEqualTo(1);

    manaCrystal.endTurn();
    manaCrystal.startTurn();

    // Turn 2.
    assertThat(manaCrystal.getLockedCrystal()).isEqualTo(overloadValue);
  }

}