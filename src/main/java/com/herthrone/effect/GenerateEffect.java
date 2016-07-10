package com.herthrone.effect;

import com.herthrone.base.Card;
import com.herthrone.base.Effect;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.ConstType;
import com.herthrone.factory.TargetFactory;
import com.herthrone.game.GameManager;
import com.herthrone.game.Side;
import com.herthrone.helper.RandomMinionGenerator;

import java.util.List;

/**
 * Created by yifengliu on 6/30/16.
 */
public class GenerateEffect implements Effect {

  private final List<String> cardNames;
  private final ConstType cardType;
  private final TargetConfig target;
  private final Side side;

  public GenerateEffect(final List<String> cardNames, final String cardType, final TargetConfig target, final Side side) {
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

    sideToTakeEffect.stream().forEach(side -> {
      final String randomCardName = RandomMinionGenerator.randomOne(cardNames);
      final Card card = GameManager.createCardInstance(randomCardName, cardType);
      side.bind(card);

      switch (target.type) {
        case HAND:
          side.hand.add(card);
          break;
        case DECK:
          side.deck.add(card);
          break;
        default:
          throw new RuntimeException("Unsupported " + target.type + " type for generate effect");
      }
    });
  }

}
