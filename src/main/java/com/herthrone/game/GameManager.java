package com.herthrone.game;

import com.herthrone.base.BaseCard;
import com.herthrone.base.Minion;
import com.herthrone.card.factory.Action;
import com.herthrone.card.factory.Factory;
import com.herthrone.constant.ConstHero;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by yifeng on 4/14/16.
 */
public class GameManager {

  public final Factory factory1;
  public final Factory factory2;
  private final Battlefield battlefield1;
  private final Battlefield battlefield2;
  private final Queue<Action> actionQueue;

  public GameManager(final ConstHero hero1, final ConstHero hero2, final Container<BaseCard> deck1, final Container<BaseCard> deck2) throws FileNotFoundException {
    // TODO: need to find a place to init deck given cards in a collection.
    this.battlefield1 = new Battlefield(hero1, hero2, deck1, deck2);
    this.battlefield2 = this.battlefield1.getMirrorBattlefield();
    this.factory1 = new Factory(this.battlefield1);
    this.factory2 = new Factory(this.battlefield2);

    this.actionQueue = new LinkedList<>();
  }

  public void playCard(final Battlefield battlefield, final int index) {
    BaseCard card = battlefield.mySide.hand.get(index);
    //this
    if (card instanceof Minion) {

    }
  }

  public Battlefield getBattlefield1() {
    return this.battlefield1;
  }

  public Battlefield getBattlefield2() {
    return this.battlefield2;
  }

}
