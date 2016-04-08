package com.herthrone.action;

import com.herthrone.base.BaseCard;
import com.herthrone.container.Hand;

/**
 * Created by yifeng on 4/5/16.
 */
public class DuplicateHandCardAction implements Action {

  private final BaseCard[] baseCards;
  private final Hand hand;

  public DuplicateHandCardAction(final Hand hand, final BaseCard... baseCards) {
    this.hand = hand;
    this.baseCards = baseCards;
  }

  @Override
  public void act() {
    for (BaseCard baseCard : this.baseCards) {
      this.hand.add(baseCard);
    }
  }
}
