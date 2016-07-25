package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.constant.ConstMinion;
import com.herthrone.factory.MinionFactory;
import com.herthrone.game.Container;
import com.herthrone.helper.RandomMinionGenerator;

import java.util.List;

/**
 * Created by yifengliu on 7/24/16.
 */
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
    final Container<Minion> board = target.binder().getSide().board;
    final int position = board.indexOf(target);
    final String minion = RandomMinionGenerator.randomOne(choices);
    final ConstMinion minionType = ConstMinion.valueOf(minion.toUpperCase());
    final Minion transformedMinion = MinionFactory.create(minionType);
    board.remove(position);
    board.add(position, transformedMinion);
  }
}
