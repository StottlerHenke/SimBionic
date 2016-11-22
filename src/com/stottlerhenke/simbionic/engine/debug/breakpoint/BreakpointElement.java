package com.stottlerhenke.simbionic.engine.debug.breakpoint;

import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.debug.*;
import com.stottlerhenke.simbionic.engine.debug.EEventType;
import com.stottlerhenke.simbionic.engine.debug.SB_BreakpointManager;
import com.stottlerhenke.simbionic.engine.debug.SB_DebugEvent;
import com.stottlerhenke.simbionic.engine.debug.SB_EngineQueryInterface;


public class BreakpointElement extends BreakpointBehavior
{
  protected static final int kNode = 0;
  protected static final int kCondition = 1;

  /**
   * @param behavior the unique name of the behavior to watch
   * @param polyIndices the set of polymorphic indices specifying which behavior
   * @param elemId the behavior-unique ID of the node or condition to watch
   * @param type equals 0 for node, 1 for condition
   */
  public BreakpointElement(SB_BreakpointManager mgr,
                    int breakpointId,
                    String behavior,
                    ArrayList polyIndices,
                    int elemId,
                    int type,
                    long entityId,
                    long iterations,
                    String constraint)
  {
    super(mgr, breakpointId, behavior, polyIndices, entityId, iterations, constraint);
    _elemId = elemId;
    _type = type;

    if( type == kNode )
      mgr.getLogger().log(" [[ breakpoint set on node " + elemId + " of '" + behavior + "' ]]");
    else
      mgr.getLogger().log(" [[ breakpoint set on condition " + elemId + " of '" + behavior + "' ]]");
  }

  public SB_DebugEvent CheckTrigger(SB_DebugEvent event, SB_EngineQueryInterface queryHandler) throws SB_Exception
  {
    if (((_type == kNode) && (event.GetType() == EEventType.kEVT_NODE_CHANGED)) ||
        ((_type == kCondition) && (event.GetType() == EEventType.kEVT_CONDITION_FOLLOWED)))
    {
      int elemId = (_type == kNode) ?	event.GetIntField("nodeId") : event.GetIntField("conditionId");

      if (DoesMatchBehavior(event,queryHandler) && (_elemId == elemId))
      {
        if (AreConstraintsSatisfied(event, queryHandler))
        {
          DMFieldMap fields = new DMFieldMap();
          fields.ADD_INT_FIELD( "breakpointId", _breakpointId);
          fields.ADD_ID_FIELD( "entity", event.GetIdField("entity") );
          fields.ADD_INT_FIELD( "frame", event.GetIntField("frame") );
          fields.ADD_INT_FIELD( "iteration", _iterCount );

          return new SB_DebugEvent( EEventType.kEVT_BREAKPOINT_HIT, fields );
        }
      }
    }
    return null;
  }

  protected int	_elemId;
  protected int _type;
}