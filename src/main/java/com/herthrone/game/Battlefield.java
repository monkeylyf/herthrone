package com.herthrone.game;

import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Hero;
import com.herthrone.base.View;
import com.herthrone.constant.ConstTarget;
import com.herthrone.constant.Constant;

import java.util.Map;

/**
 * Created by yifeng on 4/2/16.
 */
public class Battlefield implements View {

  public final Side mySide;
  public final Side opponentSide;
  private final EffectQueue effectQueue;

  public Battlefield(final Hero hero1, final Hero hero2) {
    this.effectQueue = new EffectQueue();
    this.mySide = Side.createSide(hero1, hero2, effectQueue);
    this.opponentSide = mySide.getOpponentSide();
  }

  private Battlefield(final Side mySide, final Side opponentSide, final EffectQueue effectQueue) {
    this.mySide = mySide;
    this.opponentSide = opponentSide;
    this.effectQueue = effectQueue;
  }

  public Battlefield getMirrorBattlefield() {
    return new Battlefield(opponentSide, mySide, effectQueue);
  }

  @Override
  public Map<String, String> view() {
    final ImmutableMap.Builder<String, String> viewBuilder = ImmutableMap.builder();

    final String ownPrefix = ConstTarget.OWN.toString() + ":";
    final Map<String, String> ownSideView = getOwnSideView();
    for (Map.Entry<String, String> entry : ownSideView.entrySet()) {
      viewBuilder.put(ownPrefix + entry.getKey(), entry.getValue());
    }

    final String opponentPrefix = ConstTarget.OPPONENT.toString() + ":";
    final Map<String, String> opponentSideView = getOpponentSideView();
    for (Map.Entry<String, String> entry : opponentSideView.entrySet()) {
      viewBuilder.put(opponentPrefix + entry.getKey(), entry.getValue());
    }

    return viewBuilder.build();
  }

  private Map<String, String> getOwnSideView() {
    final ImmutableMap.Builder<String, String> ownSideBuilder = buildNoHiddenSideView(mySide);

    // Add hands as part of view.
    for (int i = 0; i < mySide.hand.size(); ++i) {
      ownSideBuilder.put(Constant.HAND + i, mySide.hand.get(i).view().toString());
    }
    // Add secrets as part of view.
    for (int i = 0; i < mySide.secrets.size(); ++i) {
      ownSideBuilder.put(Constant.SECRET + i, mySide.secrets.get(i).view().toString());
    }

    return ownSideBuilder.build();
  }

  private Map<String, String> getOpponentSideView() {
    return buildNoHiddenSideView(opponentSide)
        .put(Constant.HAND_SIZE, Integer.toString(opponentSide.hand.size()))
        .put(Constant.SECRET_SIZE, Integer.toString(opponentSide.secrets.size()))
        .build();
  }

  private ImmutableMap.Builder<String, String> buildNoHiddenSideView(final Side side) {
    final ImmutableMap.Builder<String, String> sideViewBuilder = ImmutableMap.builder();
    // Add here as part of view.
    sideViewBuilder.put(Constant.HERO, side.hero.view().toString());
    // Add deck count as part of view.
    sideViewBuilder.put(Constant.DECK_SIZE, Integer.toString(side.deck.size()));
    // Add board as part of view.
    for (int i = 0; i < side.board.size(); ++i) {
      sideViewBuilder.put(Constant.BOARD + i, side.board.get(i).view().toString());
    }
    // Add crystals as part of view.
    sideViewBuilder.put(Constant.CRYSTAL, side.hero.manaCrystal().toString());
    // Add hero power as part of view.
    sideViewBuilder.put(Constant.HERO_POWER, side.hero.getHeroPower().view().toString());

    return sideViewBuilder;
  }

}