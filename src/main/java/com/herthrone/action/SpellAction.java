package com.herthrone.action;

import com.herthrone.base.Minion;
import com.herthrone.base.Spell;

import java.util.List;

/**
 * Created by yifeng on 4/5/16.
 */
public class SpellAction implements Action {

  private final Spell spell;
  private final List<Minion> creatures;

  public SpellAction(Spell spell, List<Minion> creatures) {
    this.spell = spell;
    this.creatures = creatures;
  }

  @Override
  public void act() {
    for (Minion minion : this.creatures) {
      this.spell.cast(minion);
    }
  }

}
