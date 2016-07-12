package com.herthrone.base;

import com.herthrone.game.Binder;
import com.herthrone.object.BooleanMechanics;
import com.herthrone.object.TriggeringMechanics;

/**
 * Created by yifengliu on 7/10/16.
 */
public interface Mechanic {
  interface TriggeringMechanic extends Bind {
    TriggeringMechanics getTriggeringMechanics();
  }

  interface BooleanMechanic extends Bind {
    BooleanMechanics booleanMechanics();
  }
}
