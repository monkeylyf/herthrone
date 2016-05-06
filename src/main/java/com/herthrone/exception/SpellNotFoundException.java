package com.herthrone.exception;

/**
 * Created by yifeng on 4/19/16.
 */
public class SpellNotFoundException extends CardNotFoundException {

  public SpellNotFoundException() {
  }

  public SpellNotFoundException(final String message) {
    super(message);
  }
}
