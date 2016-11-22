package com.stottlerhenke.simbionic.engine.core;

import java.io.Serializable;

/**
 * An instance of this class describes a single parameter to a behavior,
 * action, or predicate.
 */
public class SB_Parameter implements Serializable  
{
  protected String _name;
  
  protected String _type;
  
  


  public SB_Parameter() {
  }

  public SB_Parameter(String name, String type)
  {
    _name = name;
    _type = type;
  }

  /**
   * @return the name for this parameter
   */
  public String GetName() { return _name; }
  
  public String GetType() { return _type; }


  public String toString(){
    return new String("Param " + _name + " [" + _type + "]" );  

  }
}