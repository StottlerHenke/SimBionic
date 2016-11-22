package com.stottlerhenke.simbionic.engine.comm;
import java.io.Serializable;

import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.core.*;
import com.stottlerhenke.simbionic.engine.parser.*;
import com.stottlerhenke.simbionic.api.*;
import com.stottlerhenke.simbionic.common.*;

/**
 * Instances of this class define a shared space for unregulated information
 * exchange between entities (i.e., a "blackboard").  The blackboard is divided
 * into named regions, each of which can contain a single piece of data (such
 * as a WG_Variable).  Any entity can look at the data in any given region of
 * a given blackboard, and it can also modify that data.
 */

public class SB_Blackboard implements Serializable 
{
  private String _id;
  private SB_VariableMap _regions;
  transient private SB_Logger _logger;

  public SB_Blackboard(String id, SB_Logger logger) {
    _id = id;
    _regions = new SB_VariableMap();
    _logger = logger;
  }
  public SB_Variable Read(String regionName){
    SB_Variable value = _regions.GetVariableOrNull(regionName);
    if (value == null)
      value = new SB_VarInvalid();
    return value;
  }

  public void Post(String regionId, SB_Variable value)
  throws SB_Exception
  {
    if (!_regions.IsVariable(regionId))
      _regions.AddVariable(regionId,value);
    else{
      try{
        _regions.SetVariable(regionId, value, _logger);
      }catch(SB_Exception e){
        System.out.println("Exception in Post() when SetVariable() is called in SB_Blackboard.");
      }
    }
  }

  public String GetId(){ return _id; }
  
  /**
	 * Trickle down finishing to all SB_Blackboards
	 * 
	 * @param book
	 * @throws SB_Exception
	 */
	public void finishDeserialization(SB_SingletonBook book) throws SB_Exception
  {
	   _logger = book.getLogger();
  }

}