package com.herthrone.factory;

import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Secret;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstSecret;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.Constant;
import com.herthrone.game.Battlefield;
import com.herthrone.stats.IntAttribute;

import java.util.Map;

/**
 * Created by yifeng on 4/16/16.
 */
public class SecretFactory {


  private final Battlefield battlefield;

  public SecretFactory(final Battlefield battlefield) {
    this.battlefield = battlefield;
  }

  public Secret createSecretByName(final ConstSecret secret) {
    return new Secret() {
      @Override
      public Map<String, String> view() {
        return ImmutableMap.<String, String>builder()
                .put(Constant.CARD_NAME, getCardName().toString())
                .put(Constant.CRYSTAL, getCrystalManaCost().toString())
                .put(Constant.TYPE, getType().toString())
                //.put(Constant.DESCRIPTION, "TODO")
                .build();
      }

      @Override
      public String getCardName() {
        return null;
      }

      @Override
      public ConstType getType() {
        return ConstType.SECRET;
      }

      @Override
      public ConstClass getClassName() {
        return null;
      }

      @Override
      public IntAttribute getCrystalManaCost() {
        return null;
      }

      @Override
      public boolean isCollectible() {
        return true;
      }
    };
  }
}
