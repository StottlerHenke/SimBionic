package com.stottlerhenke.simbionic.engine.debug;

public class StepModeInfo
{
  public StepModeInfo(int step, long entity, int frame)
  {
    _step = step;
    _entity = entity;
    _frame = frame;
  }

  public int _step;
  public long _entity;
  public int _frame;
}
