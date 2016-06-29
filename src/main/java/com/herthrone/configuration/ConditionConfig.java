package com.herthrone.configuration;

import com.google.common.collect.Range;
import com.herthrone.constant.ConstCondition;
import com.herthrone.constant.Constant;

import java.util.Map;

/**
 * Created by yifengliu on 6/29/16.
 */
public class ConditionConfig {

  private ConstCondition conditionType;
  private Range<Integer> range;

  private static final String TYPE = "type";
  private static final String RANGE_START = "rangeStart";
  private static final String RANGE_END = "rangeEnd";

  public ConditionConfig(final Map map) {
    this.conditionType = ConstCondition.valueOf(Constant.upperCaseValue(map, TYPE));
    this.range = Range.closed((int) map.get(RANGE_START), (int) map.get(RANGE_END));
  }

  public boolean inRange(final int value) {
    return range.contains(value);
  }

  public ConstCondition getConditionType() {
    return conditionType;
  }
}
