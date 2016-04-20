package com.herthrone.exception;

/**
 * Created by yifeng on 4/14/16.
 */
public class CardNotFoundException extends Exception {

  public CardNotFoundException() {}
  public CardNotFoundException(final String message) {
    super(message);
  }
}
