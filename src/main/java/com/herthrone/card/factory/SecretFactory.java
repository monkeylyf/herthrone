package com.herthrone.card.factory;

import com.herthrone.base.Secret;
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

  public Secret createSecretByName(final String name) {
    return new Secret() {
      @Override
      public String getCardName() {
        return null;
      }

      @Override
      public String getType() {
        return null;
      }

      @Override
      public String getClassName() {
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
