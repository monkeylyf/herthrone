package com.herthrone.object;

import com.herthrone.configuration.MechanicConfig;
import com.herthrone.constant.ConstMechanic;
import com.herthrone.constant.ConstTrigger;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticMechanics {

  private static final Logger logger = Logger.getLogger(StaticMechanics.class.getName());

  private Map<ConstMechanic, BooleanAttribute> booleanAttributeMap;

  public StaticMechanics() {
    this(Collections.emptyMap());
  }

  public StaticMechanics(final Map<ConstTrigger, List<MechanicConfig>> mechanics) {
    this.booleanAttributeMap = new HashMap<>();

    if (mechanics.containsKey(ConstTrigger.NO_TRIGGER)) {
      final List<MechanicConfig> noTriggerMechanics = mechanics.get(ConstTrigger.NO_TRIGGER);
      noTriggerMechanics.forEach(mechanic -> booleanAttributeMap.put(
          mechanic.mechanic, new BooleanAttribute()));
    }
  }

  public void resetIfPresent(final ConstMechanic mechanic) {
    if (booleanAttributeMap.containsKey(mechanic)) {
      logger.debug("Reset boolean mechanic " + mechanic);
      booleanAttributeMap.get(mechanic).reset();
    }
  }

  public boolean isOff(final ConstMechanic mechanic) {
    return !isOn(mechanic);
  }

  public boolean isOn(final ConstMechanic mechanic) {
    return booleanAttributeMap.containsKey(mechanic) && booleanAttributeMap.get(mechanic).isOn();
  }

  public void initialize(final ConstMechanic mechanic) {
    booleanAttributeMap.put(mechanic, new BooleanAttribute());
  }

  public void initialize(final ConstMechanic mechanic, final double roundUntilExpire) {
    booleanAttributeMap.put(mechanic, new BooleanAttribute(roundUntilExpire));
  }

  @Override
  public String toString() {
    return booleanAttributeMap.toString();
  }
}