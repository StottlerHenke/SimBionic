package com.stottlerhenke.simbionic.engine.core;

import com.stottlerhenke.simbionic.common.Enum;

public class ESinkType extends Enum {

  private ESinkType(int value) {
    super(value);
  }

  public static final ESinkType kNode = new ESinkType(0);
  public static final ESinkType kCondition = new ESinkType(1);

}