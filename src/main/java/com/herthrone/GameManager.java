package com.herthrone;

import com.herthrone.base.Battlefield;
import com.herthrone.base.Hero;
import com.herthrone.card.factory.HeroFactory;
import com.herthrone.container.Board;
import com.herthrone.container.Deck;
import com.herthrone.container.Hand;
import com.herthrone.exception.CardNotFoundException;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class GameManager {

  private final Hero hero1;
  private final Hero hero2;
  private final Hand hand1;
  private final Hand hand2;
  private final Deck deck1;
  private final Deck deck2;
  private final Board board1;
  private final Board board2;
  private final Battlefield battlefield1;
  private final Battlefield battlefield2;

  public GameManager(final String hero1, final String hero2, final List<String> cardList1, final List<String> cardList2) throws CardNotFoundException, FileNotFoundException {
    this.hero1 = HeroFactory.createHeroByName(hero1);
    this.hero2 = HeroFactory.createHeroByName(hero2);
    this.hand1 = new Hand();
    this.hand2 = new Hand();
    this.deck1 = new Deck();
    this.deck2 = new Deck();
    this.board1 = new Board();
    this.board2 = new Board();

    this.battlefield1 = new Battlefield(this.hero1, this.hero2, this.hand1, this.hand2, this.deck1, this.deck2, this.board1, this.board2);
    this.battlefield2 = new Battlefield(this.hero2, this.hero1, this.hand2, this.hand1, this.deck2, this.deck1, this.board2, this.board1);
  }
}
