package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.ConstMinion;
import com.herthrone.factory.MinionFactory;
import com.herthrone.game.Side;
import com.herthrone.helper.RandomMinionGenerator;

import java.util.List;

public class TransformEffect implements Effect {

  private final Minion target;
  private final List<String> choices;

  public TransformEffect(final Minion target, final List<String> choices) {
    this.target = target;
    this.choices = choices;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.TRANSFORM;
  }

  @Override
  public void act() {
    final Side side = target.binder().getSide();
    final int position = side.board.indexOf(target);
    side.board.remove(position);

    final String minion = RandomMinionGenerator.randomOne(choices);
    final ConstMinion minionType = ConstMinion.valueOf(minion.toUpperCase());
    final Minion transformedMinion = MinionFactory.create(minionType);
    side.setSequenceId(transformedMinion);
    side.board.add(position, transformedMinion);
  }
}
