package com.herthrone.exception;

/**
 * Created by yifeng on 4/12/16.
 */
public class ConfigNotFoundException extends Exception {

  ConfigNotFoundException() {}
  public ConfigNotFoundException(final String message) {
    super(message);
  }
}
