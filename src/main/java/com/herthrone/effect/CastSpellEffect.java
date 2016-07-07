package com.herthrone.effect;

import com.herthrone.base.Effect;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.constant.ConstEffectType;
import com.herthrone.factory.EffectFactory;


/**
 * Created by yifengliu on 5/25/16.
 */
public class CastSpellEffect implements Effect {

  private final Spell spell;
  private final Minion target;

  public CastSpellEffect(final Spell spell) {
    this(spell, null);
  }

  public CastSpellEffect(final Spell spell, final Minion target) {
    this.spell = spell;
    this.target = target;
  }

  @Override
  public ConstEffectType effectType() {
    return ConstEffectType.CAST_SPELL;
  }

  @Override
  public void act() {
    EffectFactory.pipeEffectsByConfig(spell, target);
  }
}
