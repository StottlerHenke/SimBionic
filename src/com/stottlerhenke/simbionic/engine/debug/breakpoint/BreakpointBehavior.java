package com.stottlerhenke.simbionic.engine.debug.breakpoint;

import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.debug.*;
import com.stottlerhenke.simbionic.engine.debug.EEventType;
import com.stottlerhenke.simbionic.engine.debug.FrameBehavior;
import com.stottlerhenke.simbionic.engine.debug.SB_BreakpointManager;
import com.stottlerhenke.simbionic.engine.debug.SB_DebugEvent;
import com.stottlerhenke.simbionic.engine.debug.SB_EngineQueryInterface;


public class BreakpointBehavior extends Breakpoint
{
  /**
   * @param behavior the unique name of the behavior to watch
   * @param polyIndices the set of polymorphic indices specifying which behavior
   */
  public BreakpointBehavior(SB_BreakpointManager mgr, int breakpointId, String behavior,
          ArrayList polyIndices /*String*/, long entityId, long iterations, String constraint)
  {
    super(mgr, breakpointId, entityId, iterations, constraint);
    _behavior = behavior;
    _polyIndices = polyIndices;

    mgr.getLogger().log( " [[ breakpoint set on behavior '" + behavior + "' ]]");
  }

  public SB_DebugEvent CheckTrigger(SB_DebugEvent event, SB_EngineQueryInterface queryHandler) throws SB_Exception
  {
    if (event.GetType() == EEventType.kEVT_FRAME_CREATED)
    {
      if (DoesMatchBehavior(event,queryHandler))
      {
        if (AreConstraintsSatisfied(event, queryHandler))
        {

          DMFieldMap fields = new DMFieldMap();
          fields.ADD_INT_FIELD( "breakpointId", _breakpointId );
          fields.ADD_ID_FIELD( "entity", event.GetIdField("entity") );
          fields.ADD_INT_FIELD(  "frame", event.GetIntField("frame") );
          fields.ADD_INT_FIELD( "iteration",  _iterCount );

          return new SB_DebugEvent( EEventType.kEVT_BREAKPOINT_HIT, fields );
        }
      }
    }

    return null;
  }

  boolean DoesMatchBehavior(SB_DebugEvent event, SB_EngineQueryInterface queryHandler)
  {
    String currBehavior = new String();
    ArrayList currPolys = new ArrayList();

    // determine the current behavior, even if the event message
    // doesn't contain the information
    if (event.IsField("behavior"))
    {
      currBehavior = event.GetStringField("behavior");
      currPolys = event.GetStringArrayField("polyIndices");
    }
    else
    {
      long entityId = event.GetIdField("entity");
      int currFrame = queryHandler.GetEntityCurrentFrame(entityId);
      FrameBehavior behave = queryHandler.GetFrameBehavior(entityId, currFrame);
      currBehavior = behave._behavior;
      currPolys = behave._polys;
    }

    if (currBehavior.compareTo(_behavior) != 0)
      return false;

    // make sure this is the right polymorphism for the behavior
    int nCurrCount = currPolys.size();
    int nOrigCount = _polyIndices.size();

    if (nOrigCount == 0)
    {
    	// polys weren't specified (for breakpoint on behavior param)
    	return true;
    }

    if( nCurrCount == nOrigCount )
    {
      for( int x = 0; x < nCurrCount; x++ )
      {
        if( !_polyIndices.get(x).equals(currPolys.get(x)) )
          return false;
      }
    }
    else
      return false;

    return true;
  }

  protected String _behavior;
  protected ArrayList _polyIndices;
}
