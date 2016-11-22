package com.stottlerhenke.simbionic.engine.debug.breakpoint;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;
import com.stottlerhenke.simbionic.engine.core.SB_ExecutionFrame;
import com.stottlerhenke.simbionic.engine.debug.SB_BreakpointManager;
import com.stottlerhenke.simbionic.engine.debug.SB_DebugEvent;
import com.stottlerhenke.simbionic.engine.debug.SB_EngineQueryInterface;


public abstract class Breakpoint
{
  static final long ALL_ENTITIES = -1;
  static final long WATCHED_ENTITIES = -2;

  /**
   * @param id the unique ID of the breakpoint
   * @param entityId the ID of the entity being watched for this breakpoint
   * @param iterations the number of triggerings that must occur before actually triggering
   * @param constraint the expression string constraining breakpoint triggering
   */
  public Breakpoint(SB_BreakpointManager mgr, int breakpointId, long entityId, long iterations, String constraint)
  {
    _mgr = mgr;
    _breakpointId = breakpointId;
    _disabled = false;
    _entityId = entityId;
    _iterations = iterations;
    _iterCount = 0;
    _constraint = constraint;
  }

  /**
   * Determines if the given event triggers this breakpoint.
   * @param event the event to check
   * @param queryHandler the query interface to the engine proper
   * @return the kEVT_BREAKPOINT_HIT event if triggered, NULL otherwise
   * @throws SB_Exception TODO
   */
  public abstract SB_DebugEvent CheckTrigger(SB_DebugEvent event, SB_EngineQueryInterface queryHandler) throws SB_Exception;

// Accessors

  /**
   * @return the unique ID of the breakpoint
   */
  public int GetId() { return _breakpointId; }

  /**
   * Disables or re-enables a breakpoint.  A disabled breakpoint is not checked
   * for triggering.
   * @param flag true if the breakpoint is being disabled, false otherwise
   */
  public void SetDisabled(boolean flag) { _disabled = flag; }

  /**
   * @return true if the breakpoint is disabled, false otherwise
   */
  public boolean IsDisabled() { return _disabled; }

  /**
   * @param event the event that may be triggered the breakpoint
   * @param queryHandler the query interface to the engine proper
   * @return true if the breakpoint's constraints are satisfied, false otherwise
   */
  public boolean AreConstraintsSatisfied(SB_DebugEvent event, SB_EngineQueryInterface queryHandler) throws SB_Exception
  {
    ++_iterCount;

    boolean satisfied = true;

    // check entity constraint on the breakpoint
    if (_entityId >= 0)
    {
      // only one specific entity can trigger this breakpoint
      if (event.GetIdField("entity") != _entityId)
        return false;
    }
    else if (_entityId == WATCHED_ENTITIES)
    {
      // only entities on the watchlist can trigger this breakpoint
      if (!_mgr.IsInWatchlist( event.GetIdField("entity") ))
        return false;
    }

    // check iteration constraint on breakpoint
    if (_iterCount < _iterations)
      return false;

    // check expression constraint on breakpoint
    if (_constraint != null && !_constraint.trim().equals(""))
    {
      int frameId = event.IsField("frame") ? event.GetIntField("frame") : SB_ExecutionFrame.NULL_FRAME;
      SB_Variable result = queryHandler.EvaluateExpression(_constraint, event.GetIdField("entity"), frameId);
      if (!result.bool() )
      {
        satisfied = false;
      }
    }

    return satisfied;

  }

  public String toString()
  {
    return "BKPT " + _breakpointId;
  }

  int _breakpointId;
  boolean _disabled;
  long _entityId;
  long _iterations;
  String	_constraint;
  long _iterCount;
  SB_BreakpointManager _mgr;
}