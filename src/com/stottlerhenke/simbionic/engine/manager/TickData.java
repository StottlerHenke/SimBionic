package com.stottlerhenke.simbionic.engine.manager;

import java.io.Serializable;

public class TickData implements Serializable 
{
  public int _numUpdates;
  public SB_EntityRecord _firstScheduledEntity;
}