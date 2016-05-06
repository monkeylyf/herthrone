package com.herthrone.exception;

/**
 * Created by yifeng on 4/12/16.
 */
public class MinionNotFoundException extends CardNotFoundException {

  public MinionNotFoundException() {
  }

  public MinionNotFoundException(final String message) {
    super(message);
  }
}
