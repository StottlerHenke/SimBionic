package com.stottlerhenke.simbionic.engine.debug.breakpoint;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.debug.*;
import com.stottlerhenke.simbionic.engine.debug.EEventType;
import com.stottlerhenke.simbionic.engine.debug.SB_BreakpointManager;
import com.stottlerhenke.simbionic.engine.debug.SB_DebugEvent;
import com.stottlerhenke.simbionic.engine.debug.SB_EngineQueryInterface;


public class BreakpointFunction extends Breakpoint
{
  protected static final int kAction = 0;
  protected static final int kFunction = 1;

  /**
   * @param funcName the unique name of the action or predicate to watch
   * @param type equals 0 for action,1 for predicate
   */
  public BreakpointFunction( SB_BreakpointManager mgr,
                      int breakpointId,
                      String funcName,
                      int type,
                      long entityId,
                      long iterations,
                      String constraint)
  {
    super(mgr, breakpointId, entityId, iterations, constraint);
    _funcName = funcName;
    _type = type;

    mgr.getLogger().log( " [[ breakpoint set on action/predicate '" + funcName + "' ]]" );
  }

  public SB_DebugEvent CheckTrigger(SB_DebugEvent event, SB_EngineQueryInterface queryHandler) throws SB_Exception
  {
    int evType = event.GetType();

    if (((_type == kAction) && (evType == EEventType.kEVT_ACTION_INVOKED)) ||
        ((_type == kFunction) && (evType == EEventType.kEVT_FUNCTION_INVOKED)))
    {
      if (event.GetStringField("funcName") == _funcName)
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

  protected int _type;
  protected String _funcName;
}

