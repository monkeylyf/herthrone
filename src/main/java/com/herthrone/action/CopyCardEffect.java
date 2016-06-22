package com.herthrone.action;

import com.herthrone.base.Card;
import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.game.Container;

/**
 * Created by yifeng on 4/16/16.
 */
public class CopyCardEffect implements Effect {

  private final Container<Card> container;
  private final Card cardToCopy;

  public CopyCardEffect(final Card cardToCopy, final Container<Card> container) {
    this.cardToCopy = cardToCopy;
    this.container = container;
  }

  @Override
  public ConstEffectType getEffectType() {
    return ConstEffectType.COPY_CARD;
  }

  @Override
  public void act() {
    final String cardName = cardToCopy.getCardName();
    // TODO: deep copy is not so fun. Will create createCardByName method.
    final Card copiedCard = null;
    container.add(copiedCard);
  }
}
