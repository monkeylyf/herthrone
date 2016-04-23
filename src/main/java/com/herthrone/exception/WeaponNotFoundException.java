package com.herthrone.exception;

/**
 * Created by yifeng on 4/22/16.
 */
public class WeaponNotFoundException extends  CardNotFoundException {

  public WeaponNotFoundException() {}
  public WeaponNotFoundException(final String message) {
    super(message);
  }
}
