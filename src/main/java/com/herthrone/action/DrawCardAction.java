package com.herthrone.action;

import com.herthrone.base.BaseCard;
import com.herthrone.base.Player;

/**
 * Created by yifeng on 4/5/16.
 */
public class DrawCardAction implements Action {

  private final Player player;

  public DrawCardAction(final Player player) {
    this.player = player;
  }

  @Override
  public void act() {
    BaseCard baseCard = this.player.deck.topCard();
    this.player.hand.add(baseCard);
  }
}
