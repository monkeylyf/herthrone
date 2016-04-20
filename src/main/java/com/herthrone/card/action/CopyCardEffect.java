package com.herthrone.card.action;

import com.herthrone.card.factory.Action;
import com.herthrone.base.BaseCard;
import com.herthrone.container.Container;

/**
 * Created by yifeng on 4/16/16.
 */
public class CopyCardEffect implements Action {

  private final Container<BaseCard> container;
  private final BaseCard cardToCopy;

  public CopyCardEffect(final BaseCard cardToCopy, final Container<BaseCard> container) {
    this.cardToCopy = cardToCopy;
    this.container = container;
  }

  @Override
  public void act() {
    final String cardName = cardToCopy.getCardName();
    // TODO: deep copy is not so fun. Will create createCardByName method.
    final BaseCard copiedCard = null;
    this.container.add(copiedCard);
  }
}
