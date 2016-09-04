package com.herthrone.base;

import com.google.common.base.Optional;
import com.herthrone.configuration.TargetConfig;

/**
 * Created by yifeng on 4/4/16.
 */
public interface Spell extends Card, Refreshable, Mechanic.TriggeringMechanic {

  Optional<TargetConfig> getTargetConfig();

}
