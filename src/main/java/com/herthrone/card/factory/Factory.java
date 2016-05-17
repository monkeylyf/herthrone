package com.herthrone.card.factory;

import com.herthrone.base.BaseCard;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.game.Battlefield;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class Factory {

  public final MinionFactory minionFactory;
  public final SpellFactory spellFactory;
  public final WeaponFactory weaponFactory;
  public final SecretFactory secretFactory;
  public final EffectFactory effectFactory;
  public final AttackFactory attackFactory;
  private final Battlefield battlefield;

  public Factory(final Battlefield battlefield) {
    this.battlefield = battlefield;
    this.minionFactory = new MinionFactory(battlefield);
    this.weaponFactory = new WeaponFactory(battlefield);
    this.effectFactory = new EffectFactory(this.minionFactory, this.weaponFactory, battlefield);
    this.spellFactory = new SpellFactory(this.effectFactory);
    this.secretFactory = new SecretFactory(battlefield);
    this.attackFactory = new AttackFactory(battlefield);
  }

  public BaseCard createCardInstance(final String cardName) {
    return null;
  }

  public static List<Action> singleActionToList(Action action) {
    return Arrays.asList(action);
  }

  public static boolean targetAny(BaseCard card) {
    return targetMinion(card) || targetMinion(card);
  }

  public static boolean targetMinion(BaseCard card) {
    return card instanceof Minion && !(card instanceof Hero);
  }

  public static boolean targetHero(BaseCard card) {
    return card instanceof Hero;
  }
}
