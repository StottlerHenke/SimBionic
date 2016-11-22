package com.stottlerhenke.simbionic.engine.debug.breakpoint;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.debug.*;
import com.stottlerhenke.simbionic.engine.debug.EEventType;
import com.stottlerhenke.simbionic.engine.debug.SB_BreakpointManager;
import com.stottlerhenke.simbionic.engine.debug.SB_DebugEvent;
import com.stottlerhenke.simbionic.engine.debug.SB_EngineQueryInterface;


public class BreakpointGlobalVar extends Breakpoint
{
  /**
   * @param variable the unique name of the variable to watch
   *
   */
  public BreakpointGlobalVar(SB_BreakpointManager mgr,
                      int breakpointId,
                      String variable,
                      long entityId,
                      long iterations,
                      String constraint)
  {
    super(mgr, breakpointId, entityId, iterations, constraint);
    _varName = variable;

    mgr.getLogger().log(" [[ breakpoint set on variable '" + variable + "' ]]" );
  }

  public SB_DebugEvent CheckTrigger(SB_DebugEvent event, SB_EngineQueryInterface queryHandler) throws SB_Exception
  {
    if (event.GetType() == EEventType.kEVT_GLOBAL_CHANGED)
    {
      if (event.GetStringField("varName").compareTo(_varName) == 0)
      {
        if (AreConstraintsSatisfied(event, queryHandler))
        {
          DMFieldMap fields = new DMFieldMap();
          fields.ADD_INT_FIELD( "breakpointId", _breakpointId);
          fields.ADD_ID_FIELD( "entity", event.GetIdField("entity") );
          fields.ADD_INT_FIELD( "frame", event.GetIntField("frame") ); //JRL
          fields.ADD_INT_FIELD( "iteration", (int) _iterCount );            //JRL

          return new SB_DebugEvent( EEventType.kEVT_BREAKPOINT_HIT, fields );
        }
      }
    }
    return null;
  }

  protected String _varName;
}