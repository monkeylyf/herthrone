package com.herthrone.action;

import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;


/**
 * Created by yifengliu on 5/25/16.
 */
public class PlaySpellEffect implements Effect {

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
