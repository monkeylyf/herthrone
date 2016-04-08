package com.herthrone.action;

import com.herthrone.base.BaseCreature;
import com.herthrone.base.Spell;

import java.util.List;

/**
 * Created by yifeng on 4/5/16.
 */
public class SpellAction implements Action {

  private final Spell spell;
  private final List<BaseCreature> creatures;

  public SpellAction(Spell spell, List<BaseCreature> creatures) {
    this.spell = spell;
    this.creatures = creatures;
  }

  @Override
  public void act() {
    for (BaseCreature baseCreature : this.creatures) {
      this.spell.cast(baseCreature);
    }
  }

}
