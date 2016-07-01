package com.herthrone.object;

import com.herthrone.base.Minion;
import com.herthrone.base.Reset;
import com.herthrone.base.Round;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yifengliu on 5/5/16.
 */
public class Buff implements Reset, Round {

  public final Map<Minion, Integer> minionToTemporaryBuffMapping;
  public final Map<Minion, Integer> minionToPermanentBuffMapping;
  public final Value temporaryBuff;
  public final Value permanentBuff;

  public Buff() {
    this.minionToTemporaryBuffMapping = new HashMap<>();
    this.minionToPermanentBuffMapping = new HashMap<>();
    this.temporaryBuff = new Value();
    this.permanentBuff = new Value();
  }

  public int value() {
    return temporaryBuff.value() + permanentBuff.value();
  }

  @Override
  public void reset() {
    temporaryBuff.reset();
    permanentBuff.reset();
  }

  @Override
  public void endTurn() {
    temporaryBuff.reset();
  }

  @Override
  public void startTurn() {

  }
}
