package com.stottlerhenke.simbionic.engine.core;



/**
 * Base class for all methods that entities can invoke through
 * behavior nodes, including actions and predicates.
 */
public class SB_Method  
{
  protected String      _name;
  protected SB_ParamList _params;

  public SB_Method() {
  }
  public SB_Method(String name){
    _name = name;
    _params = null;
  }
  /**
   * @return the human-readable name of the method
   */
  public String GetName() { return _name; }


  /**
   * @return the parameters of the method
   */
  public SB_ParamList GetParams() { return _params; }



  public String toString(){
    return new String("Method(" + _name + ")");
  }


}