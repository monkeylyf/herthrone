package com.herthrone.effect;

import com.herthrone.base.Card;
import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.game.Container;
import com.herthrone.game.Game;

public class CopyCardEffect implements Effect {

  private final Container<Card> container;
  private final Card cardToCopy;

  public CopyCardEffect(final Card cardToCopy, final Container<Card> container) {
    this.cardToCopy = cardToCopy;
    this.container = container;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.COPY_CARD;
  }

  @Override
  public void act() {
    final String cardName = cardToCopy.cardName();
    // TODO: deep copy is not so fun. Will create createCardByName method.
    container.add(Game.createCardInstance(cardName, cardToCopy.type()));
  }
}
