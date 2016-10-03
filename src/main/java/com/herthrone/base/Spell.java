package com.herthrone.base;

import com.google.common.base.Optional;
import com.herthrone.configuration.TargetConfig;

public interface Spell extends Card, Refreshable, Mechanic.TriggeringMechanic {

  Optional<TargetConfig> getTargetConfig();

  Optional<TargetConfig> getTargetV2();

}
