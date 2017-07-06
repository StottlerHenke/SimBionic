package com.stottlerhenke.simbionic.engine.core;

import java.util.ArrayList;
import java.util.List;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Node;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;

/**
 * Base class for all nodes in behaviors.
 */
public abstract class SB_Node extends SB_EdgeSink
{
  protected int _subBehaviorId;
  protected int _subBehaviorMode;
  protected boolean _catchNodeFlag = false;
  protected boolean _alwaysNodeFlag = false;

  private static final int SUBMODE_MULTITICK= 0x000;
  private static final int SUBMODE_RUNUNTIL = 0x100;
  private static final int SUBMODE_ONETICK 	= 0x200;
  private static final int SUBMODE_MODEMASK	= SUBMODE_RUNUNTIL | SUBMODE_ONETICK;
  private static final int SUB_BEHAVIOR = 0;


  public SB_Node(SB_BehaviorElement owner,int nodeID)
	{
    super(owner);
    setId(nodeID);
    _subBehaviorId = 0;
    _subBehaviorMode = SB_BehaviorClass.kMODE_MULTITICK;
    _catchNodeFlag = false;
    _alwaysNodeFlag = false;
  }

  public abstract ENodeType GetNodeType();
  public abstract void Execute(SB_Entity entity,SB_ExecutionFrame contextFrame, SB_SingletonBook book) throws SB_Exception ;
  public ESinkType GetType() { return ESinkType.kNode; }

  public static String TypeToString(ENodeType type){
    if(type == ENodeType.kActionNode )
      return "action";
    else if(type == ENodeType.kBehaviorNode )
      return "behavior";
    else if(type == ENodeType.kFinalNode )
      return "final";
    else if(type == ENodeType.kCompoundNode)
    	return "compound";
    else
      return "unknown";
  }

  public String GetLogName() {
	  return "<NODE>";
  }


  /**
   * Reads a single node from the node data model.
   */
  public void load(Node node,SB_SingletonBook book, List<Integer> transitionIds)
		throws SB_FileException
	{
    super.load(node,book, transitionIds);
    loadBehaviorFlag(book);
  }

  /**
   * Load the sub-behavior-flag information
   * 
   * @param reader
   * @param book
   */
  protected void loadBehaviorFlag(SB_SingletonBook book)
  	throws SB_FileException
  {
  		int subbehavior = SUB_BEHAVIOR;
  		_subBehaviorId = subbehavior & ~SUBMODE_MODEMASK;
  		_subBehaviorMode = subbehavior & SUBMODE_MODEMASK;

  		// convert to the standard execution mode flag values
  		switch (_subBehaviorMode)
  		{
  		case SUBMODE_MULTITICK:	_subBehaviorMode = SB_BehaviorClass.kMODE_MULTITICK; break;
  		case SUBMODE_RUNUNTIL:	_subBehaviorMode = SB_BehaviorClass.kMODE_RUNUNTIL; break;
  		case SUBMODE_ONETICK:		_subBehaviorMode = SB_BehaviorClass.kMODE_ONETICK; break;
  		default: throw new SB_FileException("Unknown sub-behavior execution mode.");
  		}
  }
  
 
	/**
	 * @return the id of this node's sub-behavior (0 if none)
	 */
  public int getSubBehaviorId()
	{
  	return _subBehaviorId;
  }

  /**
	 * @return the execution mode of this node's sub-behavior (kMultiTick if none)
	 */
  public int getSubBehaviorMode()
	{
  	return _subBehaviorMode;
  }

  public void initialize(ArrayList transitions,SB_SingletonBook book)
		throws SB_Exception
	{
    // replace the transition indices with actual transition pointers
    AssignTransitionEdges(transitions);

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

  public SB_TransitionEdge FollowTransition(SB_Entity p,SB_ExecutionFrame contextFrame, SB_SingletonBook book)
  throws SB_Exception
  {
	  // can't follow a transition out of a node until that node has been
	  // applied at least once (unless the transition is in a different stack
	  // frame, of course)
	  if (!contextFrame.HasNodeBeenInvoked())
	  {
	  	return null;
	  }

	  return super.FollowTransition(p,contextFrame, book);
  }


  public boolean IsValidDestination(SB_Entity entity,SB_ExecutionFrame contextFrame,SB_EdgeSink source)
  {
	  // if this node is the endpoint of a null transition coming out
	  // of a behavior node, there is an implied IsDone()
	  if (source.GetType() == ESinkType.kNode){
	    SB_Node sourceNode = (SB_Node) source;
	    if (sourceNode.GetNodeType() == ENodeType.kBehaviorNode)
	    {
	    	boolean isDone = contextFrame.IsInvokedDone();

				if(SIM_Constants.DEBUG_INFO_ON)
				  contextFrame.GetLogger().log("[" + entity.toString() + ",STK " + contextFrame.GetStackDepth()
														+ "] CONDITION: IsDone() => " + isDone,SB_Logger.CONDITION);

	      return isDone;
	    }
	  }
	  return true;
  }

  /**
	 * @return The number of times Execute needs to be called for this node (default is once)
	 */
	public int GetExecutionCount()
	{
			return 1;
	}
	
	/**
	 * Synchronize the parent of a completed behavir. Maybe be a behavior or compound node.
	 * 
	 * @param p
	 * @param parentFrame
	 * @param doneFrame
	 * @throws SB_Exception
	 */
	public void SyncBindings(SB_Entity p,SB_ExecutionFrame parentFrame,
      SB_ExecutionFrame doneFrame) throws SB_Exception
	{
		_bindings.ApplyForReturn(p,parentFrame,doneFrame);
	}
	
	public boolean IsCatchNode()
	{
		return _catchNodeFlag;
	}
	
	public boolean IsAlwaysNode()
	{
		return _alwaysNodeFlag;
	}	
}