package com.stottlerhenke.simbionic.engine.core;

import java.io.Serializable;
import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_ID;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.debug.DMFieldMap;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.comm.SB_CommLink;
import com.stottlerhenke.simbionic.engine.debug.EEventType;
import com.stottlerhenke.simbionic.engine.debug.SB_Debugger;

/**
 * This class is created to set and run a behavior 
 */
public class SB_Entity implements Serializable  
{
	protected SB_EntityData _state; //  pointer to the entity's state information
	protected String _name; // human-readable entity name
	protected SB_ID _id; // unique entity identifier
	protected SB_CommLink _commLink;

	public SB_Entity(SB_ID id, String name, SB_VariableMap globals,
			SB_CommLink commLink, SB_SingletonBook book)
	throws SB_Exception
	{
		_id = id;
		_name = name;
		_commLink = commLink;
		_state = new SB_EntityData(this, globals);
	}

	/**
	 * @return the name of this entity
	 */
	public String GetName()
	{
		return _name;
	}

	/**
	 * @return the unique ID for this entity
	 */
	public SB_ID GetId()
	{
		return _id;
	}

	/**
	 * @return the comm link for this entity
	 */
	public SB_CommLink GetCommLink()
	{
		return _commLink;
	}

	/**
	 * @return the global data object ("memory") for this entity.
	 */
	public SB_EntityData GetState()
	{
		return _state;
	}

	public void Update(SB_Debugger debugger, SB_Logger logger)
			throws SB_Exception
	{
		if (IsFinished())
			return;
		if (SIM_Constants.DEBUG_INFO_ON)
			logger.log("[" + toString() + "] ENTITY:\tstarting update",
					SB_Logger.ENTITY);
		do
		{
			if (SIM_Constants.AI_DEBUGGER)
			{
				// announce the beginning of each entity update *cycle*, not just the
				// beginning of each entity's whole update
				DMFieldMap fields = new DMFieldMap();
				fields.ADD_ID_FIELD("entity", _id._id);
				debugger.RecordEvent(EEventType.kEVT_ENTITY_STARTING, fields);
			}
			_state.SetDoAnotherTick(false);
			
			DoBehavior(logger);
			NextBehavior(logger);
			
		} while (_state.IsDoAnotherTick());
		if (SIM_Constants.DEBUG_INFO_ON)
			logger.log("[" + toString() + "] ENTITY:\tupdate completed",
					SB_Logger.ENTITY);
		if (SIM_Constants.AI_DEBUGGER)
		{
			DMFieldMap fields2 = new DMFieldMap();
			fields2.ADD_ID_FIELD("entity", _id._id);
			debugger.RecordEvent(EEventType.kEVT_ENTITY_ENDING, fields2);
		}
	}

	public void SetBehavior(String behaviorId, ArrayList params,
			SB_SingletonBook book) throws SB_Exception
	{
		_state.SetBaseBehavior(behaviorId, params, book);
	}

    public void PushBehavior(String behaviorId, ArrayList params,
            SB_SingletonBook book) throws SB_Exception
    {
        _state.InvokeBehavior(behaviorId, _state.GetExecStack().getCurrentFrame(), params, book);
    }

    public boolean IsFinished()
	{
		return _state.IsFinished();
	}

	public void DoBehavior(SB_Logger logger) throws SB_Exception
	{
		SB_ExecutionFrame frame = _state.GetExecStack().getCurrentFrame();
		if (SIM_Constants.DEBUG_INFO_ON)
		{
		    if (frame == null)
		    {
		        logger.log("[" + toString() + "] MSG: current behavior NONE",SB_Logger.TICK);
		    }
		    else
		    {
		        logger.log("[" + toString() + "] MSG: current behavior "
		                + frame.GetLogName() + ", node " + frame.GetCurrNode().GetLogName()
		                + " [" + _state.GetExecStack().getStatusString() + "]",
		                SB_Logger.TICK);
		    }
		}
		
		if (frame != null)
		{
		    frame.Perform();
		}
	}

	public void NextBehavior(SB_Logger logger) throws SB_Exception
	{
		if (SIM_Constants.DEBUG_INFO_ON)
			logger.log("[" + toString() + "] MSG: checking transitions...",
					SB_Logger.TICK);
		// check for active transitions, select one, and follow it
		_state.GetExecStack().NextTransition(logger);
		
	}

	public String toString()
	{
		return _id.toString();
	}
	
	/**
	 * Trickle down finishing
	 * 
	 * @param book
	 * @throws SB_Exception
	 */
	public void finishDeserialization(SB_SingletonBook book) throws SB_Exception
  {
	    _state.finishDeserialization(book);
  }
}