package com.stottlerhenke.simbionic.engine.debug;

import java.util.ArrayList;

import com.stottlerhenke.simbionic.engine.debug.breakpoint.*;
import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.debug.SB_DebugMessage;

/**
 * This class keeps track of all current breakpoints (as specified by the
 * external debugger).  It is responsible for determining when a debug event
 * triggers a breakpoint.
 */

public class SB_BreakpointManager
{
  public SB_BreakpointManager(SB_EngineQueryInterface queryHandler, SB_Debugger debugger, SB_Logger logger)
  {
    _breakpoints = new ArrayList();  //Breakpoint
    _watchlist = new ArrayList();   //Long
    _queryHandler = queryHandler;
    _debugger = debugger;
    _logger = logger;
  }

  /**
   * Handles requests from the debugging client to create, destroy, or
   * modify breakpoints.
   * @param msg the client request
   */
  public void HandleMessage(SB_DebugMessage msg) throws SB_Exception
  {
    switch (msg.GetMsgType())
    {
      case SB_DebugMessage.kDBG_GUI_ADD_BREAK_BEH:  // fall through
      case SB_DebugMessage.kDBG_GUI_ADD_BREAK_FUNC:
      case SB_DebugMessage.kDBG_GUI_ADD_BREAK_VAR:
      case SB_DebugMessage.kDBG_GUI_ADD_BREAK_ELEM:	  AddBreakpoint(msg); break;
      case SB_DebugMessage.kDBG_GUI_DISABLE_BREAKPOINT: DisableBreakpoint( msg.GetIntField("breakpointId") ); break;
      case SB_DebugMessage.kDBG_GUI_ENABLE_BREAKPOINT:  EnableBreakpoint( msg.GetIntField("breakpointId") ); break;
      case SB_DebugMessage.kDBG_GUI_REMOVE_BREAKPOINT:  RemoveBreakpoint( msg.GetIntField("breakpointId") ); break;
      case SB_DebugMessage.kDBG_GUI_ADD_TO_WATCH:		  AddEntityToWatchlist( msg.GetIdField("entity") ); break;
      case SB_DebugMessage.kDBG_GUI_REMOVE_FROM_WATCH:  RemoveEntityFromWatchlist( msg.GetIdField("entity") ); break;

      default: throw new SB_Exception("Breakpoint manager does not handle messages of type " + msg.GetTypeName());
    }
  }

  /**
   * Determines if the given event triggers any of the existing breakpoints.
   * @param event the possible triggering event
   * @return a kEVT_BREAKPOINT_HIT event if a breakpoint is triggered, NULL otherwise
   */
  public SB_DebugEvent CheckBreakpoints(SB_DebugEvent event) throws SB_Exception
  {
    // just walk through all breakpoints, checking each one
    int nCount = _breakpoints.size();
    for( int x = 0; x < nCount; x++ )
    {
      Breakpoint bp = (Breakpoint) _breakpoints.get(x);

      if( !bp.IsDisabled() )
      {
        SB_DebugEvent breakpEvent = bp.CheckTrigger(event, _queryHandler);
        if( breakpEvent != null)
        {
          _logger.log( " [[ breakpoint triggered : " + bp.toString() + " ]]" );
          return breakpEvent;
        }
      }
    }

    return null;
  }

  /**
   * Is the specified entity in the editor's watchlist?
   * @param entity the entity to check for
   * @return true if the entity is in the watchlist, false otherwise
   */
  public boolean IsInWatchlist(long entity)
  {
    int nCount = _watchlist.size();
    for( int x = 0; x < nCount; x++ )
    {
      if( ((Long) _watchlist.get(x)).longValue() == entity )
        return true;
    }

    return false;
  }

  /**
   * Adds the specified breakpoint.  If a breakpoint with the specified ID
   * already exists, it is deleted and replaced.
   * @param msg the message from the client describing the breakpoint to add
   */
  protected void AddBreakpoint(SB_DebugMessage msg) throws SB_Exception
  {
    Breakpoint newBreak = null;

    switch (msg.GetMsgType())
    {
      case SB_DebugMessage.kDBG_GUI_ADD_BREAK_BEH:
		newBreak = new BreakpointBehavior(this, msg.GetIntField("breakpointId"),
											 msg.GetStringField("behavior"),
											 msg.GetStringArrayField("polyIndices"),
											 msg.GetIdField("entity"),
											 msg.GetIntField("iterations"),
											 msg.GetStringField("constraint") );
		break;

      case SB_DebugMessage.kDBG_GUI_ADD_BREAK_FUNC:
              newBreak = new BreakpointFunction(this, msg.GetIntField("breakpointId"),
                                                                                       msg.GetStringField("name"),
                                                                                       msg.GetIntField("type"),
                                                                                       msg.GetIdField("entity"),
                                                                                       msg.GetIntField("iterations"),
                                                                                       msg.GetStringField("constraint") );
              break;

      case SB_DebugMessage.kDBG_GUI_ADD_BREAK_VAR:
        if (msg.GetStringField("behavior").equals(""))
        {
                // breakpoint on global variable
                newBreak = new BreakpointGlobalVar(this, msg.GetIntField("breakpointId"),
                                                                                          msg.GetStringField("variable"),
                                                                                          msg.GetIdField("entity"),
                                                                                          msg.GetIntField("iterations"),
                                                                                          msg.GetStringField("constraint") );
        }
        else
        {
                // breakpoint on local variable or behavior parameter
                newBreak = new BreakpointLocalVar(this, msg.GetIntField("breakpointId"),
                                                                                         msg.GetStringField("behavior"),
                                                                                         msg.GetStringArrayField("polyIndices"),
                                                                                         msg.GetStringField("variable"),
                                                                                         msg.GetIdField("entity"),
                                                                                         msg.GetIntField("iterations"),
                                                                                         msg.GetStringField("constraint") );
        }
        break;

      case SB_DebugMessage.kDBG_GUI_ADD_BREAK_ELEM:
              newBreak = new BreakpointElement(this, msg.GetIntField("breakpointId"),
                                                                                       msg.GetStringField("behavior"),
                                                                                       msg.GetStringArrayField("polyIndices"),
                                                                                       msg.GetIntField("elemId"),
                                                                                       msg.GetIntField("type"),
                                                                                       msg.GetIdField("entity"),
                                                                                       msg.GetIntField("iterations"),
                                                                                       msg.GetStringField("constraint") );
              break;

      default: throw new SB_Exception("Breakpoint Manager can't handle message type " + msg.GetTypeName() );
    }

    int bpIndex = FindBreakpoint( msg.GetIntField("breakpointId") );
    if (bpIndex != -1)
    {
        // breakpoint already exists!
        _breakpoints.remove(bpIndex);
        _breakpoints.add( newBreak ); //JRL - adding at end instead of inserting. Doesn't look like it will matter
    }
    else
    {
      // completely new breakpoint
      _breakpoints.add( newBreak );
    }
  }

  /**
   * Disables the specified breakpoint.
   * @param breakpointId the unique ID of the breakpoint to disable
   */
  protected void DisableBreakpoint(int breakpointId)
  {
    int index = FindBreakpoint(breakpointId);
    if( index != -1 )
    {
      ((Breakpoint)_breakpoints.get(index)).SetDisabled(true);
    }
  }

  /**
   * Re-enables the specified breakpoint.
   * @param breakpointId the unique ID of the breakpoint to enable
   */
  void EnableBreakpoint(int breakpointId)
  {
    int index = FindBreakpoint(breakpointId);
    if( index != -1 )
    {
      ((Breakpoint)_breakpoints.get(index)).SetDisabled(false);
    }
  }

  /**
   * Removes the specified breakpoint.
   * @param breakpointId the unique ID of the breakpoint to remove
   */
  void RemoveBreakpoint(int breakpointId)
  {
    int index = FindBreakpoint(breakpointId);
    if( index != -1 )
    {
      _breakpoints.remove(index);
    }
  }

  /**
   * Adds the specified entity to the watchlist.
   * @param entity the unique ID of the entity to add
   */
  void AddEntityToWatchlist(long entity)
  {
    _watchlist.add(new Long(entity));
  }

  /**
   * Removes the specified entity from the watchlist.
   * @param entity the unique ID of the entity to remove
   */
  void RemoveEntityFromWatchlist(long entity)
  {
    int nCount = _watchlist.size();
    for( int x = 0; x < nCount; x++ )
    {
      if( ((Long) _watchlist.get(x)).longValue() == entity )
      {
        _watchlist.remove(x);
        break;
      }
    }
  }


  /**
   * Finds the breakpoint with the specified ID.
   * @param breakpointId the ID to find
   * @return the breakpoint index, if it exists, or -1
   */
  int FindBreakpoint(int breakpointId)
  {
    int nCount = _breakpoints.size();
    for( int x = 0; x < nCount; x++ )
    {
      if( ((Breakpoint) _breakpoints.get(x)).GetId() == breakpointId )
      {
        return x;
      }
    }

    return -1;
  }


  public SB_Logger getLogger()
  {
    return _logger;
  }

  ArrayList _breakpoints;  //Breakpoint
  ArrayList _watchlist;   //Long
  SB_EngineQueryInterface _queryHandler;
  SB_Debugger _debugger;
  SB_Logger  _logger;
}