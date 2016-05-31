package com.herthrone.card.action;

import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.card.factory.Action;


/**
 * Created by yifengliu on 5/25/16.
 */
public class PlaySpellEffect implements Action {

  private final Spell spell;
  private final Minion target;

  public PlaySpellEffect(final Spell spell, final Minion target) {
    this.spell = spell;
    this.target = target;
  }

  public PlaySpellEffect(final Spell spell) {
    this(spell, null);
  }

  @Override
  public void act() {
    // TODO: need factory here...
  }
}
