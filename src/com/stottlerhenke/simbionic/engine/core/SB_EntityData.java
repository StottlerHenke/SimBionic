package com.stottlerhenke.simbionic.engine.core;
import java.io.Serializable;
import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_ID;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.debug.DMFieldMap;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.debug.EEventType;
import com.stottlerhenke.simbionic.engine.debug.SB_Debugger;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;

/**
 *  This class holds a data about SB_Entity. 
 */
public class SB_EntityData implements Serializable  
{
  protected SB_ExecutionStack	_execStack;
  protected SB_Entity		_owner;
  protected boolean		_bFinished;
  protected boolean   _bDoAnotherTick;
  protected SB_VariableMap	_globalVars;


  public SB_EntityData(SB_Entity owner, SB_VariableMap globals) throws SB_Exception
  {
    _execStack = new SB_ExecutionStack(owner);
    _owner = owner;
    _bFinished = false;
    _globalVars = null;
    _bDoAnotherTick = false;
    _globalVars = globals.Clone();
  }
  public void SetBaseBehavior(String behaviorName,ArrayList params, SB_SingletonBook book)throws SB_Exception{
    // initialize everything in case a behavior is currently in progress
    _bFinished = false;
    _bDoAnotherTick = false;
    _execStack.Reset();

    InvokeBehavior(behaviorName, null,params, book);
  }

  public SB_ExecutionFrame InvokeBehavior(String behaviorName,SB_ExecutionFrame invokingFrame, ArrayList params /*=NULL*/, SB_SingletonBook book)
  throws SB_Exception
  {
    // dynamically determine which behavior to run given the entity's current state
    SB_Behavior behavior = book.getBehaviorRegistry().getBehavior(behaviorName, _owner);
    if (behavior == null)
      throw new SB_Exception("Unknown behavior " + behaviorName + ".");

    // create a new stack frame for the invoked behavior
    SB_ExecutionFrame newFrame = new SB_ExecutionFrame(invokingFrame,_owner, book);
    newFrame.Initialize(behavior,params);

    // push the newly-invoked behavior on the stack
    GetExecStack().Push(newFrame);

		if(SIM_Constants.DEBUG_INFO_ON)
		  newFrame.GetLogger().log("[" + _owner.toString() + ",STK " + newFrame.GetStackDepth()
		  												+ "] BEHAVIOR:\tinvoking " + behaviorName,SB_Logger.BEHAVIOR);

    if( SIM_Constants.AI_DEBUGGER )
    {
        DMFieldMap fields = new DMFieldMap();
        fields.ADD_ID_FIELD("entity",  _owner.GetId()._id );
				fields.ADD_INT_FIELD( "frame", newFrame.GetStackDepth() );
				int parentId = (invokingFrame == null) ? SB_ExecutionFrame.NULL_FRAME : invokingFrame.GetStackDepth();
				fields.ADD_INT_FIELD( "parent", parentId );
				fields.ADD_STR_FIELD( "behavior", behavior.getName() );
				fields.ADD_SA_FIELD( "polyIndices", behavior.GetHierarchyIndices() );
				fields.ADD_INT_FIELD( "currentNode", newFrame.GetCurrNode().getId() );
				fields.ADD_INT_FIELD( "interrupt", newFrame.IsInterrupt() );
        book.getDebugger().RecordEvent(EEventType.kEVT_FRAME_CREATED, fields);

      	// have to manually send this message because of initialization ordering
				// constraints with frame->Initialize() and stack->Push().  Though
				// technically the debug client already has this info from the above message,
				// Step needs this event to be able to stop on the initial node of a behavior

        DMFieldMap fields2 = new DMFieldMap();
        fields2.ADD_ID_FIELD("entity",  _owner.GetId()._id );
				fields2.ADD_INT_FIELD( "frame", newFrame.GetStackDepth() );
				fields2.ADD_INT_FIELD( "nodeId", newFrame.GetCurrNode().getId() );
        book.getDebugger().RecordEvent(EEventType.kEVT_NODE_CHANGED, fields2);
    }

    return newFrame;
  }
  public SB_ExecutionFrame InvokeBehavior(String behaviorName,SB_ExecutionFrame invokingFrame,  SB_SingletonBook book)
  throws SB_Exception
  {
    return InvokeBehavior(behaviorName, invokingFrame, null, book);
  }
  /**
   * @return the execution stack
   */
  public SB_ExecutionStack GetExecStack() { return _execStack; }


// Execution State Flags

  /**
   * Sets the flag indicating whether this entity has completed
   * its base-level behavior (and hence has nothing further to do).
   * @param flag new value for the flag
   */
  public void SetFinished(boolean flag) {
	  _bFinished = flag;
	  }

  /**
   * Indicates whether this entity has completed its base-level behavior.
   * @return true if this entity has reached a terminal state, false otherwise
   */
  public boolean IsFinished(){
	  return _bFinished; 
	  }

  /**
   * Sets the 'do another tick' flag, which forces the entity to
   * go through another action-transition sequence after the current one completes.
   * @param flag new value for the flag
   */
  public void SetDoAnotherTick(boolean flag) { _bDoAnotherTick = flag; }

  /**
   * Indicates whether this entity should go through another
   * action-transition sequence after the current one finishes.
   * @return true if another tick should be run, false otherwise.
   */
  public boolean IsDoAnotherTick()
  {
		return _bDoAnotherTick || GetExecStack().isOneTick();
  }

  public SB_Variable GetGlobal( String varName)throws SB_Exception{
    return _globalVars.GetVariable(varName);
  }

  public boolean IsVariable(String name)
  {
    return _globalVars.GetVariableOrNull(name) != null;
  }
  
  public ArrayList GetGlobalNames(){
    return _globalVars.GetVariableNames();
  }

  public void SetGlobal( String name,  SB_Variable value, SB_Logger logger)throws SB_Exception{
    _globalVars.SetVariable(name,value, logger);
  }
  public void SaveGlobals(boolean deepSave /*=false*/){
    _globalVars.SaveVariables(deepSave);
  }
  public void SaveGlobals() throws SB_Exception{
    SaveGlobals();
  }
  public void RestoreGlobals(boolean deepRestore,SB_ID entityID,int frameDepth, SB_Debugger debugger) throws SB_Exception{
    _globalVars.RestoreVariables(deepRestore,true,entityID,frameDepth, debugger);
  }

  public void ClearSavedGlobals(SB_Logger logger){
    _globalVars.ClearSavedVariables(logger);
  }
  public ArrayList GetPolymorphicState(SB_BehaviorRegistry behaviorRegistry) throws SB_Exception{
    ArrayList polyState = new ArrayList();

    int numHiers = behaviorRegistry.getNumHierarchies();
    for (int hier = 0; hier < numHiers; hier++) {
      String hierName = behaviorRegistry.getHierarchyName(hier);

      SB_Variable global = GetGlobal(hierName);
      String globalStr = global.getValue().toString();
      polyState.add(globalStr);
    }
    return polyState;
  }
  
	/**
	 * Trickle down finishing to the SB_ExecutionStack
	 * 
	 * @param book
	 * @throws SB_Exception
	 */
	public void finishDeserialization(SB_SingletonBook book) throws SB_Exception
  {
	    _execStack.finishDeserialization(book);
  }
	
}