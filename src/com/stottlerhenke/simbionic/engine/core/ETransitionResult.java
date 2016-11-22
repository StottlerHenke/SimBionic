package com.stottlerhenke.simbionic.engine.core;

import com.stottlerhenke.simbionic.common.Enum;

public class ETransitionResult extends Enum {
  private ETransitionResult(int value) {
    super(value);
  }
  public static final ETransitionResult kNoActive = new ETransitionResult(0);
  public static final ETransitionResult kNormal = new ETransitionResult(1);
  public static final ETransitionResult kInterrupt = new ETransitionResult(2);
}