package com.herthrone.effect;

import com.herthrone.base.Card;
import com.herthrone.base.Effect;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.ConstType;
import com.herthrone.factory.TargetFactory;
import com.herthrone.game.Game;
import com.herthrone.game.Side;
import com.herthrone.helper.RandomMinionGenerator;

import java.util.List;

public class GenerateEffect implements Effect {

  private final List<String> cardNames;
  private final ConstType cardType;
  private final TargetConfig target;
  private final Side side;

  public GenerateEffect(final List<String> cardNames, final String cardType,
                        final TargetConfig target, final Side side) {
    this.cardNames = cardNames;
    this.cardType = ConstType.valueOf(cardType.toUpperCase());
    this.target = target;
    this.side = side;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.GENERATE;
  }

  @Override
  public void act() {
    final List<Side> sideToTakeEffect = TargetFactory.getSide(target, side);

    sideToTakeEffect.forEach(side -> {
      final String randomCardName = RandomMinionGenerator.randomOne(cardNames);
      final Card card = Game.createCardInstance(randomCardName, cardType);
      side.bind(card);
      TargetFactory.getContainer(target, side).add(card);
    });
  }

}
