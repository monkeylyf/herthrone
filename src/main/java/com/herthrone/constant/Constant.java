package com.herthrone.constant;


import java.util.Map;

/**
 * Created by yifengliu on 5/13/16.
 */
public class Constant {

  // TODO: There are used in switch/case so cannot be enum type.
  // However, due to the silly modeling on the effect config yaml, cannot
  // use Enum to replace the String(yaml field might be mapped to different Enum
  // type, causing type conversion impossible during load config. Better to
  // think about it later(better config file structure?)
  public static final String ARMOR = "armor";
  public static final String ATTACK = "attack";
  public static final String CRYSTAL = "crystal";
  public static final String HEALTH = "health";
  public static final String HEALTH_UPPER_BOUND = "health_upper_bound";

  public static String upperCaseValue(final Map map, final String key) {
    final String value = (String) map.get(key);
    return value.toUpperCase();
  }
}
