package com.stottlerhenke.simbionic.engine.debug;

public class FrameInfo
{
  public FrameInfo(boolean isInterrupt, int parentId)
  {
    _isInterrupt = isInterrupt;
    _parentId = parentId;
  }

  public boolean _isInterrupt;
  public int _parentId;
}