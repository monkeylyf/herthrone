package com.herthrone.card.factory;

import com.herthrone.action.Action;
import com.herthrone.action.ActionFactory;
import com.herthrone.base.Battlefield;
import com.herthrone.card.action.AttributeEffect;
import com.herthrone.card.action.Summon;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yifeng on 4/14/16.
 */
public class HeroPowerFactory {

  private final Battlefield battlefield;

  public HeroPowerFactory(Battlefield battlefield) {
    this.battlefield = battlefield;
  }

  public ActionFactory getArmorUp() {
    return getArmorUp(this.battlefield);
  }

  private ActionFactory getArmorUp(final Battlefield field) {
    return new ActionFactory() {
      private final int armorGain = 2;
      private final Battlefield battlefield = field;
      @Override
      public List<Action> yieldActions() {
        Action action = new AttributeEffect(field.getMySide().getHero().getArmorAttr(), this.armorGain);
        return singleActionToList(action);
      }
    };
  }

  public ActionFactory getFireBlast() {
    return new ActionFactory() {
      @Override
      public List<Action> yieldActions() {
        Action action = new AttributeEffect(null, -1);
        return singleActionToList(action);
      }
    };
  }

  public ActionFactory getReinforce() {
    return new ActionFactory() {
      @Override
      public List<Action> yieldActions() {
        Action action = new Summon(null, Arrays.asList(""));
        return singleActionToList(action);
      }
    };
  }

  private static List<Action> singleActionToList(Action action) {
    return Arrays.asList(action);
  }
}
