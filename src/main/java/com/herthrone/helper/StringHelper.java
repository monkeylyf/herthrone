package com.herthrone.helper;

import com.google.common.base.CaseFormat;

/**
 * Created by yifengliu on 6/30/16.
 */
public class StringHelper {

  public static String lowerUnderscoreToUpperWhitespace(final Enum name) {
    return CaseFormat.UPPER_CAMEL
        .to(CaseFormat.UPPER_UNDERSCORE, name.toString())
        .replaceAll(" ", "_");
  }
}
