package com.stottlerhenke.simbionic.common;

import java.io.Serializable;

public class Enum implements Serializable  {
  protected int state;
  public Enum(int state) { this.state = state;}
  public final int getState(){ return state; }
  public boolean equals(Enum obj){ return state == obj.getState(); }
}