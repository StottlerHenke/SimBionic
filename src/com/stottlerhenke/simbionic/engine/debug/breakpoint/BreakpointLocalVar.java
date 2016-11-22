package com.stottlerhenke.simbionic.engine.debug.breakpoint;

import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.debug.*;
import com.stottlerhenke.simbionic.engine.debug.EEventType;
import com.stottlerhenke.simbionic.engine.debug.SB_BreakpointManager;
import com.stottlerhenke.simbionic.engine.debug.SB_DebugEvent;
import com.stottlerhenke.simbionic.engine.debug.SB_EngineQueryInterface;


public class BreakpointLocalVar extends BreakpointBehavior
{
  /**
   * @param polyIndices the set of polymorphic indices specifying which behavior
   * @param elemId the behavior-unique ID of the node or condition to watch
   * @param variable the unique name of the variable to watch
   */
   public BreakpointLocalVar(SB_BreakpointManager mgr,
                      int breakpointId,
                      String behavior,
                      ArrayList polyIndices, //Strings
                      String variable,
                      long entityId,
                      long iterations,
                      String constraint)
  {
    super(mgr, breakpointId, behavior, polyIndices, entityId, iterations, constraint);
    _varName = variable;

    mgr.getLogger().log(" [[ breakpoint set on local variable/parameter '" + variable + "' ]]");
}

  public SB_DebugEvent CheckTrigger(SB_DebugEvent event, SB_EngineQueryInterface queryHandler)throws SB_Exception
  {
    if (event.GetType() == EEventType.kEVT_VAR_CHANGED)
    {
      if (event.GetStringField("varName").compareTo(_varName) == 0)
      {
        if (DoesMatchBehavior(event,queryHandler))
        {
          if (AreConstraintsSatisfied(event, queryHandler))
          {
            DMFieldMap fields = new DMFieldMap();
            fields.ADD_INT_FIELD( "breakpointId", _breakpointId);
            fields.ADD_ID_FIELD( "entity", event.GetIdField("entity") );
            fields.ADD_INT_FIELD( "frame", event.GetIntField("frame") );
            fields.ADD_INT_FIELD( "iteration",  _iterCount );

            return new SB_DebugEvent( EEventType.kEVT_BREAKPOINT_HIT, fields );
          }
        }
      }
    }
    return null;
  }

  protected String _varName;
}

/*




WG_DebugEvent* WG_BreakpointLocalVar::CheckTrigger(WG_DebugEvent* event,WG_EngineQueryInterface* queryHandler)
{


	return NULL;
}
*/