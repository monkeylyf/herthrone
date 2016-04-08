package com.herthrone.base;

/**
 * Created by yifeng on 4/2/16.
 */
public class Battlefield {

  private final Player playerA;
  private final Player playerB;

  private int round;

  public Battlefield(Player playerA, Player playerB) {
    this.playerA = playerA;
    this.playerB = playerB;

    this.playerA.registerOpponent(playerB);
    this.playerB.registerOpponent(playerA);

    this.round = 0;
  }

  public void play() {
    while (!this.playerA.isDefeated() && !this.playerB.isDefeated()) {
      this.playerA.move();
      this.playerB.move();
    }
    sumUp();
  }

  private void sumUp() {
    if (this.playerA.isDefeated() && this.playerB.isDefeated()) {
      // Draw game.
    } else if (this.playerA.isDefeated()) {
      // this.playerA.user.wins += 1;
      // this.playerB.user.losses += 1;
    } else {
      // this.playerB.user.wins += 1;
      // this.playerA.user.losses += 1;
    }
  }
}
