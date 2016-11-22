package com.stottlerhenke.simbionic.engine.debug;

import java.util.ArrayList;

public class FrameBehavior
{
  public FrameBehavior(String behavior, ArrayList polys)
  {
    _behavior = behavior;
    _polys = polys;
  }

  public String _behavior;
  public ArrayList _polys;
}