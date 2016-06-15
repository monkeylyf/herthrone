package com.herthrone.stats;

import com.google.common.base.Optional;
import com.herthrone.configuration.MechanicConfig;
import com.herthrone.constant.ConstMechanic;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yifengliu on 6/13/16.
 */
public class BooleanMechanics {

  static Logger logger = Logger.getLogger(BooleanMechanics.class.getName());

  private Map<ConstMechanic, BooleanAttribute> booleanAttributeMap;

  public BooleanMechanics() {
    this.booleanAttributeMap = new HashMap<>();
  }

  public BooleanMechanics(final Map<ConstMechanic, MechanicConfig> mechanics) {
    this.booleanAttributeMap = new HashMap<>();

    ConstMechanic.getBooleanMechanics().stream().forEach(
        mechanic -> {
          if (mechanics.containsKey(mechanic)) {
            booleanAttributeMap.put(mechanic, new BooleanAttribute());
          }
        }
    );
  }

  public Optional<BooleanAttribute> get(final ConstMechanic mechanic) {
    final BooleanAttribute booleanAttribute = booleanAttributeMap.get(mechanic);
    return Optional.fromNullable(booleanAttribute);
  }

  public void resetIfPresent(final ConstMechanic mechanic) {
    if (has(mechanic)) {
      logger.debug("Reset boolean mechanic " + mechanic);
      booleanAttributeMap.get(mechanic).reset();
    }
  }

  public boolean has(final ConstMechanic mechanic) {
    return booleanAttributeMap.containsKey(mechanic);
  }

  @Override
  public String toString() {
    return booleanAttributeMap.toString();
  }
}