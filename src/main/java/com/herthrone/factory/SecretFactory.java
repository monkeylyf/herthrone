package com.herthrone.factory;

import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Secret;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstSecret;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;
import com.herthrone.game.Binder;
import com.herthrone.object.TriggeringMechanics;
import com.herthrone.object.ValueAttribute;

import java.util.Map;

/**
 * Created by yifeng on 4/16/16.
 */
public class SecretFactory {

  public static Secret create(final ConstSecret secret) {
    return new Secret() {

      private final Binder binder = new Binder();

      @Override
      public Map<String, String> view() {
        return ImmutableMap.<String, String>builder()
            .put(Constant.CARD_NAME, cardName().toString())
            .put(Constant.CRYSTAL, manaCost().toString())
            .put(Constant.TYPE, type().toString())
            .build();
      }

      @Override
      public String cardName() {
        return null;
      }

      @Override
      public String displayName() {
        return null;
      }

      @Override
      public ConstType type() {
        return ConstType.SECRET;
      }

      @Override
      public ConstClass className() {
        return null;
      }

      @Override
      public ValueAttribute manaCost() {
        return null;
      }

      @Override
      public boolean isCollectible() {
        return true;
      }

      @Override
      public Binder binder() {
        return binder;
      }

      @Override
      public TriggeringMechanics getTriggeringMechanics() {
        return null;
      }

    };
  }
}
