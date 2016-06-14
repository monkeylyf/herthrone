package com.herthrone.stats;

import com.google.common.base.Optional;
import com.herthrone.constant.ConstMechanic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yifengliu on 6/13/16.
 */
public class BooleanMechanics {

  private Map<ConstMechanic, BooleanAttribute> booleanAttributeMap;

  public BooleanMechanics() {
    this.booleanAttributeMap = new HashMap<>();
  }

  public Optional<BooleanAttribute> getBooleanAttribute(final ConstMechanic mechanic) {
    final BooleanAttribute booleanAttribute = booleanAttributeMap.get(mechanic);
    return Optional.fromNullable(booleanAttribute);
  }

  public void resetBooleanAttributeIfPresent(final ConstMechanic mechanic) {
    Optional<BooleanAttribute> booleanAttributeOptional = getBooleanAttribute(mechanic);
    if (booleanAttributeOptional.isPresent()) {
      booleanAttributeOptional.get().reset();
    }
  }

  public boolean hasBooleanAttribute(final ConstMechanic mechanic) {
    return booleanAttributeMap.containsKey(mechanic);
  }

  public void setBooleanAttribute(final ConstMechanic mechanic) {
    if (!booleanAttributeMap.containsKey(mechanic)) {
      booleanAttributeMap.put(mechanic, new BooleanAttribute());
    }
  }

  @Override
  public String toString() {
    return booleanAttributeMap.toString();
  }
}
