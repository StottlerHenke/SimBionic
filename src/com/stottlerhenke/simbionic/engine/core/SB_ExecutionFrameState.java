package com.stottlerhenke.simbionic.engine.core;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;

/**
 * An easy way to package all of the state variables for an execution frame to allow
 * setting/getting of the complete state
 */
public class SB_ExecutionFrameState implements Serializable
{
	//execution state flags
  transient public SB_Node	_currNode = null;
  public int	_nodeInvocationCount;
  public boolean _isInterrupt;
  public boolean _invokedInterrupt;
  public boolean _invokedFrameDone;
  transient public SB_Node  _interruptedNode = null;
  public boolean _alwaysNodeInvoked;
  
  //serialization flags
  public SB_Entity _entity;
  public String _behaviorName;
  private int _currNodeID;
  private int _interruptedNodeID;
  
	/**
	 * 
	 */
	public SB_ExecutionFrameState()
	{
		super();
	}
	
	/**
   * Set the following before writing:
   *  _currNode id
   *  _interruptedNode id
   * 
   * @param s
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream s) throws IOException 
  {
      if(_currNode != null)
          _currNodeID = _currNode.getId();
      else
          _currNodeID = -1;
          
      
      if(_interruptedNode != null)
          _interruptedNodeID = _interruptedNode.getId();
      else
          _interruptedNodeID = -1;
      
      s.defaultWriteObject();
  }

  /**
   * After the object is deserialized, this function will reset the behavior
   * variables.
   * 
   * @param book
   */
  public void finishDeserialization(SB_SingletonBook book) throws SB_Exception
  {
      if( _behaviorName != null)
      {
          SB_Behavior behavior = book.getBehaviorRegistry().getBehavior(_behaviorName, _entity);
          
          if( behavior == null )
              throw new SB_Exception("Behavior that existed during serialization can no longer be found: " + _behaviorName);
          
          if(_currNodeID != -1)
              _currNode = behavior.GetNode(_currNodeID);
          
          if(_interruptedNodeID != -1) 
              _interruptedNode = behavior.GetNode(_interruptedNodeID);
      }
      
  }
}
