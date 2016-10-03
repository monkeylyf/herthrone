package com.herthrone.constant;

public enum ConstSelect {
  // Card cannot be played unless a target is selected
  MANDATORY,
  // Card can be played either with or without a specific target. But the mechanic only triggers
  // when a target is provided.
  OPTIONAL,
  // No target specified when being played. The real target will be post-selected.
  PASSIVE,
  // Temp
  NOT_PROVIDED
}
