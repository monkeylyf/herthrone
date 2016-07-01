package com.herthrone.object;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.herthrone.base.Round;
import com.herthrone.constant.ConstAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifengliu on 6/25/16.
 */
public class Replay implements Round {

  private final List<List<ReplayRecord>> records;
  private int turnCount;

  public Replay() {
    this.records = new ArrayList<>();
    this.turnCount = 0;
  }

  public void add(final String who, final int whoIndex, final ConstAction action, final String what) {
    records.get(turnCount - 1).add(new ReplayRecord(who, whoIndex, action, Optional.of(what)));
  }

  public void add(final String who, final int whoIndex, final ConstAction action) {
    records.get(turnCount - 1).add(new ReplayRecord(who, whoIndex, action, Optional.absent()));
  }

  public int size() {
    return records.get(turnCount - 1).size();
  }

  public int size(final int turn) {
    return records.get(turn).size();
  }

  @Override
  public void endTurn() {
  }

  @Override
  public void startTurn() {
    turnCount += 1;
    records.add(new ArrayList<>());
  }

  @Override
  public String toString() {
    final Objects.ToStringHelper stringHelper = Objects.toStringHelper(this).add("turn", getTurn());
    for (int i = 0; i < records.size(); ++i) {
      stringHelper.add("turn#" + (i + 1), records.get(i));
    }
    return stringHelper.toString();
  }

  public int getTurn() {
    return turnCount;
  }

  private static class ReplayRecord {

    final String who;
    final int whoIndex;
    final ConstAction action;
    final Optional<String> what;

    ReplayRecord(final String who, final int whoIndex, final ConstAction action, final Optional<String> what) {
      this.who = who;
      this.whoIndex = whoIndex;

      this.action = action;
      this.what = what;
    }

    @Override
    public String toString() {
      final Objects.ToStringHelper stringHelper = Objects.toStringHelper(this)
          .add("who", who)
          .add("action", action.toString());
      if (what.isPresent()) {
        stringHelper.add("what", what.get());
      }
      return stringHelper.toString();
    }
  }

}

