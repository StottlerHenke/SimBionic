package com.stottlerhenke.simbionic.engine.core;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Connector;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;

/**
 * An instance of this class represents a directed edge between two elements
 * (nodes or conditions) of a behavior.  It has associated variable bindings
 * that will be applied when the edge is traversed.
 */
public class SB_TransitionEdge extends SB_BehaviorElement 
{
  protected SB_EdgeSink	_sink;
  protected SB_Bindings	_bindings;
  protected boolean	_bInterrupt;
  protected int priority;

  public SB_TransitionEdge(SB_BehaviorElement owner) {
    super(owner);
    _sink = null;
    _bindings = null;
    _bInterrupt = false;
  }
  
  public void initialize(SB_SingletonBook book) 
	{
    // initialize variable bindings (parse them)
    _bindings.initialize(book);
  }

  /**
   * Replaces all instances of SB_Method that match the name of newMethod.
   * @param newMethod the method with which to replace existing methods
   */
  public void replaceFunction(SB_Function newFunction)
  {
      _bindings.replaceFunction(newFunction);
  }
  
  public void ApplyBindings(SB_Entity p, SB_ExecutionFrame contextFrame) throws SB_Exception {
    _bindings.ApplyForVariables(p,contextFrame);
  }

  public void load(Connector connector,SB_SingletonBook book)
		throws SB_FileException
	{
	  // read in ID
	  _id = connector.getId();
	
	  if(SIM_Constants.DEBUG_INFO_ON)
	    book.getLogger().log(".Loading transition edge " + _id,SB_Logger.INIT);
	
	  // read in interrupt transition flag
	  _bInterrupt = connector.isInterrupt();
	
	  // read in end element id and type
	  int endId = connector.getEndId();
	  int type = connector.getEndType();
	
	  // link up the actual end element (node or condition)
	  SB_Behavior owner = (SB_Behavior) getOwner();
	  if (type == ESinkType.kNode.getState()) {
	    _sink = (SB_EdgeSink) owner.GetNode(endId);
	  }
	  else if (type == ESinkType.kCondition.getState()) {
	     _sink = (SB_EdgeSink) owner.GetCondition(endId);
	  }
	  else
	    throw new SB_FileException("Unknown endpoint type " + type +
	                           			" for transition " + _id);
	
	  // read in variable bindings 
	  _bindings = new SB_Bindings();
	  _bindings.load(connector.getBindings(),book);
  }

  /**
   * @return the sink of this edge
   */
  public SB_EdgeSink GetSink() { return _sink;}

  /**
   * @return true if this is an interrupt edge, false otherwise
   */
  public boolean IsInterrupt() { return _bInterrupt; }

  public String toString(){
    return new String("TRAN" + getId());
  }
  
  public int getPriority () {
	  return priority;
  }
  
  public void setPriority(int priority) {
	  this.priority = priority;
  }


}