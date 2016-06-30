package com.herthrone.action;

import com.herthrone.base.Card;
import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.ConstType;
import com.herthrone.game.GameManager;
import com.herthrone.game.Side;
import com.herthrone.helper.RandomMinionGenerator;

import java.util.ArrayList;
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
  public ConstEffectType getEffectType() {
    return ConstEffectType.GENERATE;
  }

  @Override
  public void act() {
    List<Side> sideToTakeEffect = new ArrayList<>();
    switch (target.scope) {
      case ALL:
        sideToTakeEffect.add(side);
        sideToTakeEffect.add(side.getOpponentSide());
        break;
      case OWN:
        sideToTakeEffect.add(side);
        break;
      case OPPONENT:
        sideToTakeEffect.add(side.getOpponentSide());
        break;
      default:
        throw new RuntimeException("Unknown target scope: " + target.scope);
    }

    sideToTakeEffect.stream().forEach(side -> {
      final String randomCardName = RandomMinionGenerator.randomOne(cardNames);
      final Card card = GameManager.createCardInstance(randomCardName, cardType);
      if (card instanceof Minion) {
        card.getBinder().bind(side);
      }

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
