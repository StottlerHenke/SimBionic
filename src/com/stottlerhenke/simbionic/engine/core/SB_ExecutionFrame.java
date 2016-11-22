package com.stottlerhenke.simbionic.engine.core;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.debug.DMFieldMap;
import com.stottlerhenke.simbionic.engine.SB_SimInterface;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.debug.EEventType;
import com.stottlerhenke.simbionic.engine.debug.SB_Debugger;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;
/**
 * Represents an environment where a behavior can be executed. 
 */
public class SB_ExecutionFrame implements Serializable  
{

  public static final int NULL_FRAME = 0;

  transient protected SB_Behavior	_behavior;
  transient protected SB_SingletonBook _book;
  
  protected SB_ExecutionFrame _parent;	// the frame that invoked this frame
  protected SB_Entity	_entity;	// entity to which this frame belongs
  protected int		_stackDepth;
  protected int _subBehaviorId;
  protected int _compoundId;
  protected SB_VariableMap _variables;

  // execution state flags
  transient protected SB_Node	_currNode;
  transient protected SB_Node  _interruptedNode;
  
  protected int	_nodeInvocationCount;
  protected boolean _isInterrupt;
  protected boolean _invokedInterrupt;
  protected boolean _invokedFrameDone;
  protected boolean _alwaysNodeInvoked;

  //Deserialization variables
  private String _behaviorName;
  private int _currNodeID;
  private int _interruptedNodeID;
  
  public SB_ExecutionFrame(SB_ExecutionFrame parent,SB_Entity entity, boolean temporary, SB_SingletonBook book)
	{
    _parent = parent;
    _entity = entity;
    _book = book;
    _stackDepth = -1;
    _behavior = null;
    _currNode = null;
    _nodeInvocationCount = 0;
    _isInterrupt = false;
    _invokedInterrupt = false;
    _invokedFrameDone = false;
    _interruptedNode = null;
    _alwaysNodeInvoked = false;
    _variables= null;
    _subBehaviorId = 0;
    _compoundId = -1;

    if (temporary){
        // this frame was created solely for expression evaluation
        _variables = new SB_VariableMap();
    }
    if (parent != null && parent.InvokedInterrupt()){
        // this frame was either invoked via an interrupt transition
        // (making it an interrupt frame), or it was invoked by an existing
        // interrupt frame (interrupt status flows upward on the stack)
        SetInterrupt(true);
    }
  }

  public void Initialize(SB_Behavior behavior, ArrayList params) throws SB_Exception
	{
    if(SIM_Constants.DEBUG_INFO_ON)
      _book.getLogger().log(_entity.toString() + ": initializing frame " + this.toString()
                       + " with behavior " + behavior.toString());

    if (behavior == null)
      throw new SB_Exception("NULL behavior passed to SB_ExecutionFrame().");

    SetBehavior(behavior);

    // make a copy of the variable template for the behavior
    SB_VariableMap varTmpl = behavior.GetVarTemplate();
    _variables = varTmpl.Clone();

    // also add the behavior's parameters (defined by the parent class)
    // as regular variables
    SB_BehaviorClass behClass = behavior.GetClass();
    behClass.GetParams().Instantiate(_variables);

    // set current node to the initial node of the behavior
    _currNode = behavior.GetInitNode();

    if (params != null){
      behavior.GetClass().GetParams().UnpackArgs(this, params);
    }
  }

  /**
   * Store the given variable in this execution frame.
   * @param name The name of the variable.
   * @param value The value of the variable.
   */
  public void SetVariable(String name, SB_Variable value) {
    // check local variables first, then globals
    if (_variables.IsVariable(name)){
      try{
        _variables.SetVariable(name, value, _book.getLogger());

        if( SIM_Constants.AI_DEBUGGER )
        {
            DMFieldMap fields = new DMFieldMap();
            fields.ADD_ID_FIELD("entity",  _entity.GetId()._id );
            fields.ADD_INT_FIELD( "frame", GetStackDepth() );
            fields.ADD_STR_FIELD( "varName", name );
            fields.ADD_PARAM_FIELD( "value", SB_SimInterface.ConvertVariable(value) );
            _book.getDebugger().RecordEvent(EEventType.kEVT_VAR_CHANGED, fields);
        }
      }catch(SB_Exception e){
        _book.getLogger().log("Exception when setting variable '" + name + "' in SB_ExecutionFrame: " + e.getMessage());
      }
    }else{
      try{
    	  // store the variable in the global variable map.
        _entity.GetState().SetGlobal(name, value, _book.getLogger());

        if( SIM_Constants.AI_DEBUGGER )
        {
            DMFieldMap fields = new DMFieldMap();
            fields.ADD_ID_FIELD("entity",  _entity.GetId()._id );
            fields.ADD_INT_FIELD( "frame", GetStackDepth() );
            fields.ADD_STR_FIELD( "varName", name );
            fields.ADD_PARAM_FIELD( "value", SB_SimInterface.ConvertVariable(value) );
            _book.getDebugger().RecordEvent(EEventType.kEVT_GLOBAL_CHANGED, fields);
        }
      }catch(SB_Exception e)
      {
        _book.getLogger().log("Exception when setting global variable '" + name + "' in SB_ExecutionFrame: " + e.getMessage());
      }
    }
  }

  public SB_ExecutionFrame(SB_ExecutionFrame parent,SB_Entity entity,  SB_SingletonBook book){
    this(parent, entity, false, book);
  }

	/**
	 * Checks if the frame has entered a new sub-behavior.
	 */
  protected void checkEnterSubBehavior()
	{
		int nextSub = GetCurrNode().getSubBehaviorId();
		if (nextSub != _subBehaviorId)
		{
			// entering a new subbehavior
			_subBehaviorId = nextSub;
			_entity.GetState().GetExecStack().enterSubBehavior( GetCurrNode().getSubBehaviorMode() );

            if(SIM_Constants.DEBUG_INFO_ON)
                _book.getLogger().log("[" + _entity + ",STK " + GetStackDepth() + "] BEHAVIOR:\tentering sub-behavior "
                                        + _subBehaviorId,SB_Logger.BEHAVIOR);
		}
	}

	/**
	 * Checks if the frame has exited a sub-behavior.
	 */
	protected void checkExitSubBehavior()
	{
		int nextSub = GetCurrNode().getSubBehaviorId();
		if ((_subBehaviorId != 0) && (nextSub != _subBehaviorId))
		{
			// exiting a subbehavior
			_subBehaviorId = 0;
			_entity.GetState().GetExecStack().exitSubBehavior();

            if(SIM_Constants.DEBUG_INFO_ON)
                _book.getLogger().log("[" + _entity + ",STK " + GetStackDepth() + "] BEHAVIOR:\texiting sub-behavior ",SB_Logger.BEHAVIOR);
		}
	}
	
	/**
	 * Checks to see if the frame has entered a compound node
	 */
	protected void checkEnterCompoundNode()
	{
		int nextCompoundId = GetCurrNode().getId();
		if( nextCompoundId != _compoundId &&
				GetCurrNode().GetNodeType() == ENodeType.kCompoundNode )
		{
			_entity.GetState().GetExecStack().enterCompoundNode();
			_compoundId = nextCompoundId;
		}
	}
	
	/**
	 * Exit the compound node immediately.
	 */
	protected void checkExitCompoundNode()
	{
	  if( _compoundId == GetCurrNode().getId())
	  {
			_entity.GetState().GetExecStack().exitCompoundNode();
			_compoundId = -1;
	  }
	}

	/**
	 * Setup the node as if the invocation count is > node execution count,
	 * i.e. the node is completely done firing
	 */
	public void SetNodeExecuted()
	{
		_nodeInvocationCount = GetCurrNode().GetExecutionCount();
	}
	
  /**
   * Has the current node been invoked yet in this frame?
   */
  public boolean HasNodeBeenInvoked() { return _nodeInvocationCount > 0; }
  public void SetNodeBeenInvoked(boolean invoked)
  {
  	if(invoked)
  		_nodeInvocationCount = 1;
  	else
  		_nodeInvocationCount = 0;
  }
  public void SetNodeBeenInvoked(int count) { _nodeInvocationCount = count; }
  public int GetNodeInvocationCount() { return _nodeInvocationCount; }

  /**
   * 
   * @return true if the node has been executed completely false otherwise
   */
  public boolean HasBeenExecuted()
  {
  	return GetNodeInvocationCount() >= GetCurrNode().GetExecutionCount();
  }
  
  /**
   * Gets/sets the current node for this frame.
   * @interrupted true if this node is resuming after having been interrupted
   */
  //public void SetCurrNode(SB_Node node,boolean interrupted = false);
  public SB_Node GetCurrNode() { return _currNode; }

  /**
   * Gets/sets the entity's current behavior for this frame.
   */
  public SB_Behavior GetBehavior() {return _behavior;}
  public void SetBehavior(SB_Behavior behavior) {_behavior = behavior;}

  /**
   * @return the parent frame (ie the invoking frame) of this one
   */
  public SB_ExecutionFrame GetParent() { return _parent; }

  /**
   * @return position of the frame in the stack (counting up from 1 at the stack bottom)
   */
  public int GetStackDepth()  { return _stackDepth; }
  public void SetStackDepth(int depth) { _stackDepth = depth; }
  /**
   * @return true if this frame was called as an interrupt, false otherwise
   */
  public boolean IsInterrupt() { return _isInterrupt; }
  public void SetInterrupt(boolean flag) { _isInterrupt = flag; }

  /**
   * @return true if this frame just invoked an interrupt, false otherwise
   */
  public boolean InvokedInterrupt() { return _invokedInterrupt; }
  public void SetInvokedInterrupt(boolean flag) { _invokedInterrupt = flag; }

  /**
   * @return true if the behavior invoked by this frame has completed, false otherwise
   */
  public boolean IsInvokedDone() { return _invokedFrameDone; }
  public void SetInvokedDone(boolean flag) { _invokedFrameDone = flag; }

  public String toString(){
    String behName = (this.GetBehavior() != null) ? this.GetBehavior().GetClass().getName() : "<NULL>";
    return " FRAME " + GetStackDepth() + ": " + behName;
  }
  public void ClearSavedVariables(){
    _variables.ClearSavedVariables(_book.getLogger());
    _entity.GetState().ClearSavedGlobals(_book.getLogger());
  }

// ***************************************************
// *************        ACCESSORS        *************
// ***************************************************

  public void SetCurrNode(SB_Node node, boolean interrupted)throws SB_Exception{
    if (node == null)
      throw new SB_Exception("Null node in SB_ExecutionFrame::SetCurrNode.");

      // unless the node is resuming after being interrupted, always reapply its
      // bindings when it becomes current
      SetNodeBeenInvoked( interrupted );
      
      if( node == _behavior.GetAlwaysNode())
      	_alwaysNodeInvoked = true;
      
      _currNode = node;

    	// sub-behaviors are officially exited when the current node changes to a non-member node
    	checkExitSubBehavior();

    	if( SIM_Constants.AI_DEBUGGER )
      {
          DMFieldMap fields = new DMFieldMap();
          fields.ADD_ID_FIELD("entity",  _entity.GetId()._id );
          fields.ADD_INT_FIELD( "frame", GetStackDepth() );
          fields.ADD_INT_FIELD( "nodeId", node.getId() );
          _book.getDebugger().RecordEvent(EEventType.kEVT_NODE_CHANGED, fields);
      }
  }
  public void SetCurrNode(SB_Node node)throws SB_Exception{
    SetCurrNode(node, false);
  }

  public void InterruptCurrentNode(SB_Node node) throws SB_Exception{
    if (node == null)
      throw new SB_Exception("Null node in SB_ExecutionFrame::InterruptCurrentNode.");

    // make a note that this frame is interrupting
    SetInvokedInterrupt(true);
    SetInvokedDone(false);

    // save the interrupted node so execution can resume there later
    _interruptedNode = _currNode;
    SetCurrNode( node );
  }

  public void InterruptDone() throws SB_Exception{
    if (_interruptedNode == null)
      throw new SB_Exception("Null node in SB_ExecutionFrame::ResumeNode.");

      // note that the interrupt has completed
      SetInvokedInterrupt(false);

      // resume execution at the interrupted node
      SetCurrNode( _interruptedNode, true );
      _interruptedNode = null;
  }

	public String GetLogName() {
		return (_behavior == null) ? "<NO-BEHAVIOR>" : _behavior.GetClass().getName();
	}

// ***************************************************
// *************   VARIABLE MANAGEMENT   *************
// ***************************************************


  public SB_Variable GetVariable( String name)throws SB_Exception {
    // check local variables first, then globals
    if (_variables.IsVariable(name)){
      return _variables.GetVariable(name);
    }

    return _entity.GetState().GetGlobal(name);
  }


  public boolean IsVariable(String name)
  {
    return ( _variables.IsVariable(name) || _entity.GetState().IsVariable(name));
  }
  
  public ArrayList GetVariableNames(){
    return _variables.GetVariableNames();
  }


  public void SaveVariables(boolean initialSave /*=false*/) {
    _variables.SaveVariables( initialSave );
    _entity.GetState().SaveGlobals( initialSave );
  }
  public void SaveVariables(){ SaveVariables(false); }

  public void RestoreVariables(boolean lastRestore /*=false*/)throws SB_Exception{
    _variables.RestoreVariables( lastRestore, false, _entity.GetId(), GetStackDepth(), _book.getDebugger() );
    _entity.GetState().RestoreGlobals( lastRestore, _entity.GetId(), GetStackDepth(), _book.getDebugger() );
  }
  public void RestoreVariables()throws SB_Exception{
    RestoreVariables(false);
  }
// ***************************************************
// *************     EXECUTION FLOW      *************
// ***************************************************

  public void Perform() throws SB_Exception {
  	// sub-behaviors are officially entered when one of the member nodes is executed
  	checkEnterSubBehavior();
  	
  	checkEnterCompoundNode();
  	
    GetCurrNode().Execute(_entity,this, _book);
  }


  public ETransitionResult FollowTransition()
  throws SB_Exception
  {
    SB_EdgeSink currSink = GetCurrNode();
    SB_TransitionEdge edge = null;
    boolean isInterrupt = false;

    // start saving variables in case we get partway through a transition
    // but fail to reach a node and have to restore state
    SaveVariables(true);

    // keep following active transition edges until a node is reached
    while ((edge = currSink.FollowTransition(_entity,this)) != null)
    {
      currSink = edge.GetSink();

      // traversing an interrupt edge instantly makes the whole
      // transition an interrupt, regardless of what the terminal node is
      if (edge.IsInterrupt())
      {
        isInterrupt = true;
      }

      if (currSink.GetType() == ESinkType.kNode)
        break;	// found a node!

      if( SIM_Constants.AI_DEBUGGER )
      {
          DMFieldMap fields = new DMFieldMap();
          fields.ADD_ID_FIELD("entity",  _entity.GetId()._id );
          fields.ADD_INT_FIELD( "frame", GetStackDepth() );
          fields.ADD_INT_FIELD( "conditionId", currSink.getId() );
          _book.getDebugger().RecordEvent(EEventType.kEVT_CONDITION_FOLLOWED, fields);
      }
		}

	  ETransitionResult result;

	  if (edge == null)
	  {
	    // failed to complete a full transition
	    // restore initial state
	    try
	    {
	      RestoreVariables(true);
	      //result = ETransitionResult.kNoActive;
	    }
	    catch(Exception e)
	    {
	      throw new SB_Exception("Exception in restoring initial state in SB_ExecutionFrame.");
	    }
	    result = ETransitionResult.kNoActive;
	  }
	  else
	  {
	    if (!isInterrupt)
	    {
         // completed a full transition
         SetCurrNode( (SB_Node) (currSink));

	    	 result = ETransitionResult.kNormal;

		    if (SIM_Constants.DEBUG_INFO_ON)
		    	_book.getLogger().log("[" + _entity.toString() + ",STK " + GetStackDepth() + "] MSG:\ttransition to new node "
					 								+ ((SB_Node)currSink).GetLogName(),SB_Logger.TICK);

				if (_entity.GetState().GetExecStack().isRunUntilBlocked())
				{
					// in run-until-blocked mode, we continue every time we follow a transition
					_entity.GetState().SetDoAnotherTick(true);
				}
	    }
	    else
	    {
	      // interrupt transition
	      InterruptCurrentNode( (SB_Node) (currSink));

	      result = ETransitionResult.kInterrupt;

	      // interrupts take no clock ticks
	      _entity.GetState().SetDoAnotherTick(true);

			  if (SIM_Constants.DEBUG_INFO_ON)
				  _book.getLogger().log("[" + _entity.toString() + ",STK " + GetStackDepth() + "] MSG:\ttransition to interrupt node "
													  + ((SB_Node)currSink).GetLogName(),SB_Logger.TICK);
	    }
	  }

    // clean up any unrestored saved variables
    ClearSavedVariables();

    return result;
	}

  /**
   *
   * @return the SB_Debugger assigned to this execution frame
   */
  public SB_Debugger GetDebugger()
  {
    return _book.getDebugger();
  }

  /**
   *
   * @return the SB_book.getLogger() assigned to this execution frame
   */
  public SB_Logger GetLogger()
  {
    return _book.getLogger();
  }

  public SB_SingletonBook GetBook()
  {
    return _book;
  }
  
  /**
   * 
   * @return true if the always node has been invoked in this frame, false otherwise
   */
  public boolean GetAlwaysNodeInvoked()
  {
  	return _alwaysNodeInvoked;
  }
  
  public SB_Entity GetEntity()
  {
  	return _entity;
  }
  
  public SB_ExecutionFrameState GetState()
  {
  	SB_ExecutionFrameState state = new SB_ExecutionFrameState();
  	
  	state._currNode = _currNode;
  	state._nodeInvocationCount = _nodeInvocationCount;
  	state._isInterrupt = _isInterrupt;
  	state._invokedInterrupt = _invokedInterrupt;
  	state._invokedFrameDone = _invokedFrameDone;
  	state._interruptedNode = _interruptedNode;
  	state. _alwaysNodeInvoked = _alwaysNodeInvoked;
  	
  	//Variables required for deserialization
  	state._behaviorName = (_behavior == null ? null : _behavior.getName());
  	state._entity = _entity;
  	
  	return state;
  }
  
  public void SetState(SB_ExecutionFrameState newState)
  {
  	_currNode = newState._currNode;
  	_nodeInvocationCount = newState._nodeInvocationCount;
  	_isInterrupt = newState._isInterrupt;
  	_invokedInterrupt = newState._invokedInterrupt;
  	_invokedFrameDone = newState._invokedFrameDone;
  	_interruptedNode = newState._interruptedNode;
     _alwaysNodeInvoked = newState._alwaysNodeInvoked;
  }
  
  /**
   * Set the following before writing:
   * 	_beavior name
   *  _currNode id
   *  _interruptedNode id
   * 
   * @param s
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream s) throws IOException 
  {
      if(_behavior != null)
          _behaviorName = _behavior.getName();
      else
          _behaviorName = null;
      
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
      _book = book;
      
      if( _behaviorName != null )
      {
          _behavior = book.getBehaviorRegistry().getBehavior(_behaviorName, _entity);
          
          if( _behavior == null )
              throw new SB_Exception("Behavior that existed during serialization can no longer be found: " + _behaviorName);

          if(_currNodeID != -1)
              _currNode = _behavior.GetNode(_currNodeID);
          
          if(_interruptedNodeID != -1) 
              _interruptedNode = _behavior.GetNode(_interruptedNodeID);
      }   
  }


}