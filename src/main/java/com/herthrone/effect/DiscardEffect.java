package com.herthrone.effect;

import com.google.common.base.Preconditions;
import com.herthrone.base.Card;
import com.herthrone.base.Effect;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.game.Container;
import com.herthrone.helper.RandomMinionGenerator;

public class DiscardEffect implements Effect {

  private final Container<Card> container;
  private final boolean randomDiscard;

  public DiscardEffect(final Container<Card> container, final boolean randomDiscard) {
    this.container = container;
    this.randomDiscard = randomDiscard;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.DISCARD;
  }

  @Override
  public void act() {
    Preconditions.checkArgument(container.size() > 0, "Cannot discard from empty container");
    if (randomDiscard) {
      // Randomly discard.
      final Card cardToDiscard = RandomMinionGenerator.randomOne(container.asList());
      container.remove(cardToDiscard);
    } else {
      // Discard the top/right-most one.
      container.remove(container.size() - 1);
    }
  }
}
