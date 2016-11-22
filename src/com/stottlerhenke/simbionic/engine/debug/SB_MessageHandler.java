package com.stottlerhenke.simbionic.engine.debug;

import java.util.Date;

import com.stottlerhenke.simbionic.engine.core.SB_ExecutionFrame;
import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.debug.DMFieldMap;
import com.stottlerhenke.simbionic.common.debug.SB_DebugMessage;

/**
 *  This class processes incoming messages from the external debugger.  For data
 * queries, it retrieves the requested information, packages it, and sends it back
 * to the external debugger.  All other messages it dispatches to the appropriate
 * debugging module for processing.
 */

public class SB_MessageHandler
{
  private static final int CLOCKS_PER_SEC = 1000;

  public SB_MessageHandler( SB_DebugServer server,
                            SB_BreakpointManager breakpointMgr,
                            SB_EngineQueryInterface queryHandler,
                            SB_EventHandler eventHandler,
                            SB_Logger logger)
  {
	_msgServer = server;
        _breakpointMgr = breakpointMgr;
        _queryHandler = queryHandler;
	_eventHandler = eventHandler;
        _logger = logger;

	_msgServer.SetMessageHandler( this );	// bit of a kludge
	_eventHandler.SetMessageHandler( this );
  }

  /**
   * Blocks while waiting for a message of a specified type (or range of types) to arrive
   * from a debug client.
   * @param firstMsgType the start of the desired range of message types (inclusive)
   * @param lastMsgType the end of the desired range of message types (inclusive);
   *						-1 indicates a single desired message type == firstMsgType
   * @param processMsgs if true, all messages that are *not* of the desired type will
   *						still be processed by the message handler; if false, they are simply discarded
   * @param timeout if positive, indicates the time in seconds to wait for the message before
   *					giving up
   * @return the received message, or null if it timed out or a gui_shutdown message was received
   */
  public SB_DebugMessage WaitForMessage(int firstMsgType,
                                        int lastMsgType /*=-1*/,
                                        boolean processMsgs /*=false*/,
                                        int timeout /*= SB_DebugServer::NO_TIMEOUT*/,
                                        SB_Debugger debugger) throws SB_Exception
  {
    SB_DebugMessage msg = null;

    if (lastMsgType == -1)
      lastMsgType = firstMsgType;

    // wait for message or timeout, whichever comes first
    double waitSoFar;
    Date startTick = new Date();

    do
    {
      if (msg != null)
      {
        // shutdown messages are *always* processed, and *always* end the wait loop;
        // otherwise only process unlooked-for messages if explicitly told to
        if (processMsgs || (msg.GetMsgType() == SB_DebugMessage.kDBG_GUI_SHUTDOWN))
        {
          boolean shutdown = DispatchMsg( msg );

          if (shutdown)
          {
            _logger.log(" [[ shutdown: abandoning wait-loop in debug message handler ]]",SB_Logger.DEBUGGER);
            return null;
          }
        }
        else
        {
          _logger.log("! Discarded client message " + msg.GetTypeName() + " while waiting for response type (" + firstMsgType + "-" + lastMsgType + ")", SB_Logger.WARNING);
        }
      }

      try
      {
        Thread.sleep(100);
      }
      catch(Exception ex)
      {
        _logger.log(ex.toString());
      }

      msg = _msgServer.ReceiveMsg();

      // verify that the connection is still open so we don't get stuck in an infinite loop
      if ((msg == null) && !_msgServer.IsConnected())
        return null;

      waitSoFar = (new Date().getTime() - startTick.getTime()) / (double)CLOCKS_PER_SEC;

    } while (((msg == null) || (msg.GetMsgType() < firstMsgType) || (msg.GetMsgType() > lastMsgType)) &&
                     ((timeout == SB_DebugServer.NO_TIMEOUT) || (waitSoFar < timeout)));

    if (msg != null)
      _logger.log(" [[ received message '" + msg.GetTypeName() + "' ]]",SB_Logger.DEBUGGER);

    return msg;

  }

  public SB_DebugMessage WaitForMessage(int firstMsgType,
                                      int lastMsgType /*=-1*/,
                                      boolean processMsgs /*=false*/,
                                      SB_Debugger debugger) throws SB_Exception
  {
    return WaitForMessage( firstMsgType, lastMsgType, processMsgs, SB_DebugServer.NO_TIMEOUT, debugger );
  }

  /**
   * Process the queue of incoming messages from the debug client.  Each message
   * is forwarded to the debugging module that should handle it.  If the message
   * is a query message, this method will cause this class to respond to the query.
   * @return true if a gui_shutdown message was received, false otherwise
   */
  public boolean HandleMessages() throws SB_Exception
  {

    SB_DebugMessage msg = null;

    // verify that the connection is still open
    if (!_msgServer.IsConnected())
            return true;

    // process all incoming messages currently in the queue
    while ((msg = _msgServer.ReceiveMsg()) != null)
    {
      boolean shutdown = DispatchMsg(msg);

      if (shutdown)
        return true;
    }

    return false;
  }

  /**
   * Takes an engine event and decides whether the editor should be notified
   * about that event, based on current editor status and runtime mode.
   * @param event the event to evaluate and possibly forward
   */
  public void NotifyClient(SB_DebugEvent event) throws SB_Exception
  {
    // only send events that are relevant to the client's current display state
    if (!ShouldBeFiltered(event))
    {
      SB_DebugMessage msg = TranslateEventToMsg(event);

      if (msg != null)
      {
        _msgServer.SendMsg(msg);
      }
    }
  }

  /**
   * Forwards the given message to the appropriate module to be handled.
   * @param msg the message to dispatch
   * @return true if a gui_shutdown message was received, false otherwise
   */
  boolean DispatchMsg(SB_DebugMessage msg) throws SB_Exception
  {

    _logger.log(" [[ received message '" + msg.GetTypeName() + "' ]]",SB_Logger.DEBUGGER);

    switch (msg.GetMsgType())
    {
      case SB_DebugMessage.kDBG_GUI_ADD_BREAK_BEH:	// fall through
      case SB_DebugMessage.kDBG_GUI_ADD_BREAK_FUNC:
      case SB_DebugMessage.kDBG_GUI_ADD_BREAK_VAR:
      case SB_DebugMessage.kDBG_GUI_ADD_BREAK_ELEM:
      case SB_DebugMessage.kDBG_GUI_DISABLE_BREAKPOINT:
      case SB_DebugMessage.kDBG_GUI_REMOVE_BREAKPOINT:_breakpointMgr.HandleMessage(msg); break;

      case SB_DebugMessage.kDBG_GUI_SELECT_ENTITY:	break; // deprecated
      case SB_DebugMessage.kDBG_GUI_SELECT_FRAME:		break; // deprecated

      case SB_DebugMessage.kDBG_GUI_ADD_TO_WATCH:		// fall through
      case SB_DebugMessage.kDBG_GUI_REMOVE_FROM_WATCH:_breakpointMgr.HandleMessage(msg); break;

      case SB_DebugMessage.kDBG_GUI_GET_FRAME:		// fall through
      case SB_DebugMessage.kDBG_GUI_GET_LOCAL_VARS:
      case SB_DebugMessage.kDBG_GUI_GET_GLOBAL_VARS:
      case SB_DebugMessage.kDBG_GUI_GET_ENTITY:		HandleClientQuery(msg); break;

      case SB_DebugMessage.kDBG_GUI_SET_LOCAL:		// fall through
      case SB_DebugMessage.kDBG_GUI_SET_GLOBAL:		HandleClientEdit(msg); break;

      case SB_DebugMessage.kDBG_GUI_PAUSE:			_eventHandler.EnterStepMode(msg); break;

      case SB_DebugMessage.kDBG_GUI_STOP:				// fall through
      case SB_DebugMessage.kDBG_GUI_SHUTDOWN:			return true;

      case SB_DebugMessage.kDBG_GUI_STEP:				// fall through
      case SB_DebugMessage.kDBG_GUI_STEP_INTO:
      case SB_DebugMessage.kDBG_GUI_STEP_ONE_TICK:
      case SB_DebugMessage.kDBG_GUI_START:
      case SB_DebugMessage.kDBG_GUI_RUN_TO_FINAL:		return false;	// ignore these

      default: throw new SB_Exception("Can't dispatch unknown message type " + msg.GetMsgType());
    }

    return false;
  }

  /**
   * Processes a data query from the debug client, possibly sending a response
   * message.
   * @param msg the query message to handle
   */
  void HandleClientQuery(SB_DebugMessage msg) throws SB_Exception
  {
    long entityId = msg.GetIdField("entity");
    SB_DebugMessage response = null;
    DMFieldMap fields = new DMFieldMap();

    switch (msg.GetMsgType())
    {
    case SB_DebugMessage.kDBG_GUI_GET_FRAME:
            {
                    int frameId = msg.GetIntField("frame");

                    FrameBehavior frameBehave = _queryHandler.GetFrameBehavior(entityId,frameId);

                    int currNode = _queryHandler.GetFrameCurrentNode(entityId,frameId);

                    FrameInfo frameInf = _queryHandler.GetFrameInfo( entityId, frameId );

                    fields.ADD_ID_FIELD(  "entity", entityId );
                    fields.ADD_INT_FIELD(  "frame", frameId );
                    fields.ADD_INT_FIELD(  "parent", frameInf._parentId );
                    fields.ADD_STR_FIELD(  "behavior", frameBehave._behavior );
                    fields.ADD_SA_FIELD(  "polyIndices", frameBehave._polys );
                    fields.ADD_INT_FIELD(  "currentNode", currNode );
                    fields.ADD_INT_FIELD(  "interrupt", frameInf._isInterrupt );

                    response = new SB_DebugMessage(SB_DebugMessage.kDBG_ENG_FRAME_INFO, fields);
            }
            break;

    case SB_DebugMessage.kDBG_GUI_GET_LOCAL_VARS:
            {
                    int frameId = msg.GetIntField("frame");
                    FrameVarValues varVal = _queryHandler.GetFrameVarValues(entityId,frameId);

                    fields.ADD_ID_FIELD(  "entity", entityId );
                    fields.ADD_INT_FIELD(  "frame", frameId );
                    fields.ADD_SA_FIELD(  "varNames", varVal._varNames );
                    fields.ADD_PA_FIELD(  "varValues", varVal._varValues );

                    response = new SB_DebugMessage(SB_DebugMessage.kDBG_ENG_LOCAL_VARS_INFO, fields);
            }
            break;

    case SB_DebugMessage.kDBG_GUI_GET_GLOBAL_VARS:
            {
                    FrameVarValues varVal = _queryHandler.GetGlobalVarValues(entityId);

                    fields.ADD_ID_FIELD(  "entity", entityId );
                    fields.ADD_SA_FIELD(  "varNames", varVal._varNames );
                    fields.ADD_PA_FIELD(  "varValues", varVal._varValues );

                    response = new SB_DebugMessage(SB_DebugMessage.kDBG_ENG_GLOBAL_VARS_INFO, fields);
            }
            break;

    case SB_DebugMessage.kDBG_GUI_GET_ENTITY:
            {
                    int frameId = _queryHandler.GetEntityCurrentFrame(entityId);
                    FrameBehavior frameBehave = _queryHandler.GetFrameBehavior(entityId,frameId);

                    fields.ADD_ID_FIELD(  "entity", entityId );
                    fields.ADD_INT_FIELD(  "stackSize", _queryHandler.GetEntityStackSize(entityId) );
                    fields.ADD_STR_FIELD(  "behavior", frameBehave._behavior );
                    fields.ADD_SA_FIELD(  "polyIndices", frameBehave._polys );
                    fields.ADD_LONG_FIELD(  "alive", _queryHandler.GetEntityTimeAlive(entityId) );
                    fields.ADD_LONG_FIELD(  "updates", _queryHandler.GetEntityTotalUpdates(entityId) );

                    response = new SB_DebugMessage(SB_DebugMessage.kDBG_ENG_ENTITY_INFO, fields);
            }
            break;

    default : throw new SB_Exception("Can't handle unknown query message " + msg.GetMsgType());
    }

    _msgServer.SendMsg( response );

  }

  /**
   * Processes an edit request from the debug client.
   * @param msg the edit message to handle
   * @todo - This functions seems lacking. Maybe it wasn't property ported?
   */
  void HandleClientEdit(SB_DebugMessage msg) throws SB_Exception
  {
    switch (msg.GetMsgType())
    {
    case SB_DebugMessage.kDBG_GUI_SET_LOCAL:
      _queryHandler.SetFrameVarValue( msg.GetIdField("entity"),
       msg.GetIntField("frame"),
       msg.GetStringField("varName"),
       msg.GetParamField("varValue") );
       break;

    case SB_DebugMessage.kDBG_GUI_SET_GLOBAL:
      _queryHandler.SetGlobalVarValue( msg.GetIdField("entity"),
      msg.GetStringField("varName"),
      msg.GetParamField("varValue") );
      break;

    default:  throw new SB_Exception("Can't handle unknown edit message " + msg.GetMsgType());
    }
  }

  /**
   * Determines if a given event should be sent to the client based
   * on current filter settings.
   * @param event the event to filter
   * @return true if the event should not be sent, false if it should.
   */
  boolean ShouldBeFiltered(SB_DebugEvent event) throws SB_Exception
  {
    int eventType = event.GetType();

    // check for events that are never filtered
    switch(eventType)
    {
      case EEventType.kEVT_ENTITY_CREATED:
      case EEventType.kEVT_ENTITY_DESTROYED:
      case EEventType.kEVT_STEP_FINISHED:
      case EEventType.kEVT_BREAKPOINT_HIT:
        return false;
    }

    StepModeInfo step = _eventHandler.GetStepModeInfo();
    long entity = step._entity;
    int frame = step._frame;


    // most events are only sent in step mode -- run mode would glut the client
    switch (step._step)
    {
    case EStepMode.kSTEP_INTO: return false; // filter nothing

    case EStepMode.kSTEP_OVER:
            // filter only frame-created messages until a behavior node is hit
            // (but don't filter the creation of a base-level frame!)
            if (eventType == EEventType.kEVT_FRAME_CREATED)
            {
                    if (event.GetIntField("parent") != SB_ExecutionFrame.NULL_FRAME)
                            return true;
            }
            return false;

    case EStepMode.kSTEP_OVER_ACTIVE:
            // in the process of skipping over a behavior invocation -- only start
            // sending messages again when the target frame becomes current or is discarded
            if ((eventType == EEventType.kEVT_FRAME_CURRENT) || (eventType == EEventType.kEVT_FRAME_DISCARDED))
            {
                    if ((event.GetIdField("entity") == entity) && (event.GetIntField("frame") == frame))
                    {
                            return false;
                    }
            }
            else if ((eventType == EEventType.kEVT_ENTITY_DESTROYED) && (event.GetIdField("entity") == entity))
            {
                    // the entity in question has been destroyed, so quit stepping
                    return false;
            }
            return true;

    case EStepMode.kSTEP_ONE_TICK:
            // only send the start-of-tick message, or notify the editor if the target
            // entity no longer exists
            if ((eventType == EEventType.kEVT_ENTITY_STARTING) || (eventType == EEventType.kEVT_ENTITY_DESTROYED))
            {
                    // only send the message if it is the target entity's tick
                    if (event.GetIdField("entity") == entity)
                    {
                            return false;
                    }
            }
            return true;

    case EStepMode.kSTEP_RUN_TO_FINAL:
            // only send the event marking the completion of the target frame
            // for the target entity
            if ((eventType == EEventType.kEVT_FRAME_COMPLETED) || (eventType == EEventType.kEVT_FRAME_DISCARDED))
            {
                    if ((event.GetIdField("entity") == entity) &&
                            (event.GetIntField("frame") == frame))
                    {
                            return false;
                    }
            }
            return true;

    case EStepMode.kNO_STEP:	return true;	// in run mode no messages are sent

    default: throw new SB_Exception("Unknown step mode in ShouldBeFiltered().");
    }
  }

  /**
   * Produces the corresponding debugging message for the given event.
   * @param event the event to generate a message for
   * @return the newly-allocated message
   */
  SB_DebugMessage TranslateEventToMsg(SB_DebugEvent event) throws SB_Exception
  {
    int msgType;

    // map from an event type to the appropriate message type
    // NOTE: should this be handled through some kind of mapping array instead?
    switch (event.GetType())
    {
    case EEventType.kEVT_ENTITY_CREATED:		msgType = SB_DebugMessage.kDBG_ENG_ENTITY_CREATED; break;
    case EEventType.kEVT_ENTITY_DESTROYED:		msgType = SB_DebugMessage.kDBG_ENG_ENTITY_DESTROYED; break;
    case EEventType.kEVT_BEHAVIOR_CHANGED:		msgType = SB_DebugMessage.kDBG_ENG_BEHAVIOR_CHANGED; break;
    case EEventType.kEVT_ENTITY_STARTING:		msgType = SB_DebugMessage.kDBG_ENG_ENTITY_STARTING; break;
    case EEventType.kEVT_ENTITY_ENDING:		msgType = SB_DebugMessage.kDBG_ENG_ENTITY_ENDING; break;
    case EEventType.kEVT_FRAME_CREATED:		msgType = SB_DebugMessage.kDBG_ENG_FRAME_CREATED; break;
    case EEventType.kEVT_FRAME_COMPLETED:		msgType = SB_DebugMessage.kDBG_ENG_FRAME_COMPLETED; break;
    case EEventType.kEVT_FRAME_DISCARDED:		msgType = SB_DebugMessage.kDBG_ENG_FRAME_DISCARDED; break;
    case EEventType.kEVT_FRAME_CURRENT:		msgType = SB_DebugMessage.kDBG_ENG_FRAME_CURRENT; break;
    case EEventType.kEVT_VAR_CHANGED:			msgType = SB_DebugMessage.kDBG_ENG_VAR_CHANGED; break;
    case EEventType.kEVT_NODE_CHANGED:			msgType = SB_DebugMessage.kDBG_ENG_NODE_CHANGED; break;
    case EEventType.kEVT_GLOBAL_CHANGED:		msgType = SB_DebugMessage.kDBG_ENG_GLOBAL_CHANGED; break;
    case EEventType.kEVT_CONDITION_CHECKED:	msgType = SB_DebugMessage.kDBG_ENG_CONDITION_CHECKED; break;
    case EEventType.kEVT_CONDITION_FOLLOWED:	msgType = SB_DebugMessage.kDBG_ENG_CONDITION_FOLLOWED; break;
    case EEventType.kEVT_ACTION_INVOKED:		return null;  // do not send to client
    case EEventType.kEVT_FUNCTION_INVOKED:		return null;  // do not send to client
    case EEventType.kEVT_BREAKPOINT_HIT:		msgType = SB_DebugMessage.kDBG_ENG_BREAKPOINT_HIT; break;
    case EEventType.kEVT_STEP_FINISHED:		msgType = SB_DebugMessage.kDBG_ENG_STEP_FINISHED; break;
    default: throw new SB_Exception("Unknown event type " + event.GetType());
    }

    return new SB_DebugMessage(msgType, event.CloneFields(_logger));

  }

  SB_DebugServer	    _msgServer;
  SB_BreakpointManager	    _breakpointMgr;
  SB_EngineQueryInterface   _queryHandler;
  SB_EventHandler	    _eventHandler;
  SB_Logger                 _logger;
}
