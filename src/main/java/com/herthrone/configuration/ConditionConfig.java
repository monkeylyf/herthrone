package com.herthrone.configuration;

import com.google.common.collect.Range;
import com.herthrone.constant.ConstCondition;

import java.util.Map;

import static com.herthrone.configuration.ConfigLoader.getUpperCaseStringValue;

/**
 * Created by yifengliu on 6/29/16.
 */
public class ConditionConfig {

  private static final String TYPE = "type";
  private static final String RANGE_START = "range_start";
  private static final String RANGE_END = "range_end";
  public final ConstCondition conditionType;
  private final Range<Integer> range;

  ConditionConfig(final Map map) {
    this.conditionType = ConstCondition.valueOf(getUpperCaseStringValue(map, TYPE));
    this.range = Range.closed((int) map.get(RANGE_START), (int) map.get(RANGE_END));
  }

  public boolean inRange(final int value) {
    return range.contains(value);
  }
}
