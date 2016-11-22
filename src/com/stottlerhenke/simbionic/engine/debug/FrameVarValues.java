package com.stottlerhenke.simbionic.engine.debug;

import java.util.ArrayList;

public class FrameVarValues
{
  public FrameVarValues(ArrayList varNames, ArrayList varValues)
  {
    _varNames = varNames;
    _varValues = varValues;
  }

  public ArrayList _varNames;
  public ArrayList _varValues;

}