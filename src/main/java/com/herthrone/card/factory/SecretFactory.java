package com.herthrone.card.factory;

import com.herthrone.base.Secret;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstSecret;
import com.herthrone.constant.ConstType;
import com.herthrone.game.Battlefield;
import com.herthrone.stats.IntAttribute;

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
