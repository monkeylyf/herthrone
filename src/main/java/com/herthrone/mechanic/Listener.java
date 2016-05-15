package com.herthrone.mechanic;

/**
 * Triggered events.
 * <p>
 * Events can be triggered by different actions by user or state changes of minions.
 */
public interface Listener {

  void onAttack();

  void onHeal();

  void onDeath();

  void onHeroPower();

  void onStartTurn();

  void onFinishTurn();

  void onPlayMinion();

  void onDrawCard();

  void onTakeDamage();
}
