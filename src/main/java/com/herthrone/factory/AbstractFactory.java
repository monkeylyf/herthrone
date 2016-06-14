package com.herthrone.factory;

/**
 * Created by yifengliu on 6/11/16.
 */
public interface AbstractFactory<T> {

  T createByName();
}
