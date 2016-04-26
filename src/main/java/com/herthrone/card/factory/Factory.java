package com.herthrone.card.factory;

import com.herthrone.base.BaseCard;
import com.herthrone.game.Battlefield;
import com.herthrone.base.Hero;
import com.herthrone.base.Minion;
import com.herthrone.configuration.Constants;
import com.herthrone.exception.CardNotFoundException;
import com.herthrone.exception.MinionNotFoundException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class Factory {

  private final Battlefield battlefield;
  public final MinionFactory minionFactory;
  public final SpellFactory spellFactory;
  public final WeaponFactory weaponFactory;
  public final SecretFactory secretFactory;
  public final EffectFactory effectFactory;
  public final AttackFactory attackFactory;

  public Factory(final Battlefield battlefield) {
    this.battlefield = battlefield;
    this.minionFactory = new MinionFactory(battlefield);
    this.weaponFactory = new WeaponFactory(battlefield);
    this.effectFactory = new EffectFactory(this.minionFactory, this.weaponFactory, battlefield);
    this.spellFactory = new SpellFactory(this.effectFactory);
    this.secretFactory = new SecretFactory(battlefield);
    this.attackFactory = new AttackFactory(battlefield);
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
