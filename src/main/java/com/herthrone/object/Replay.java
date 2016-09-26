package com.herthrone.object;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.herthrone.base.Round;
import com.herthrone.constant.ConstAction;

import java.util.ArrayList;
import java.util.List;

public class Replay implements Round {

  private final List<List<ReplayRecord>> records;
  private int turnCount;

  public Replay() {
    this.records = new ArrayList<>();
    this.turnCount = 0;
  }

  public void add(final String who, final int whoIndex, final ConstAction action, final String what) {
    getCurrentRoundRecords().add(new ReplayRecord(who, whoIndex, action, Optional.of(what)));
  }

  public void add(final String who, final int whoIndex, final ConstAction action) {
    getCurrentRoundRecords().add(new ReplayRecord(who, whoIndex, action, Optional.absent()));
  }

  public List<ReplayRecord> getCurrentRoundRecords() {
    Preconditions.checkArgument(turnCount > 0, "Game has not started yet");
    return records.get(turnCount - 1);
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
    final MoreObjects.ToStringHelper stringHelper = MoreObjects.toStringHelper(this)
        .add("turn", getTurn());
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
      final MoreObjects.ToStringHelper stringHelper = MoreObjects.toStringHelper(this)
          .add("who", who)
          .add("action", action.toString());
      if (what.isPresent()) {
        stringHelper.add("what", what.get());
      }
      return stringHelper.toString();
    }
  }

}

