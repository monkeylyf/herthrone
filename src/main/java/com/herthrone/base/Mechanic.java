package com.herthrone.base;

import com.herthrone.configuration.TargetConfig;
import com.herthrone.object.ActiveMechanics;
import com.herthrone.object.StaticMechanics;

public interface Mechanic {

  interface ActiveMechanic extends Bind {
    ActiveMechanics getActiveMechanics();
    TargetConfig getSelectTargetConfig();
  }

  interface StaticMechanic extends Bind {
    StaticMechanics booleanMechanics();
  }
}
