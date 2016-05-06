package com.herthrone.exception;

/**
 * Created by yifeng on 4/12/16.
 */
public class HeroNotFoundException extends CardNotFoundException {

  public HeroNotFoundException() {
  }

  public HeroNotFoundException(final String message) {
    super(message);
  }
}
