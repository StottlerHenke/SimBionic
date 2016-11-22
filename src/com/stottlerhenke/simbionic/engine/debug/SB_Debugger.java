package com.stottlerhenke.simbionic.engine.debug;

import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.debug.DMFieldMap;
import com.stottlerhenke.simbionic.common.debug.SB_DebugMessage;
import com.stottlerhenke.simbionic.api.SB_Config;
import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;

/**
 * 
 * This class serves as the interface between the rest of the engine and the interactive
 * debugging facility. 
 */

public class SB_Debugger
{
  /**
  * Prepares the debugger for use.  Must be called before using any other debugger
  * methods.
  * @param config configuration parameters for the debugger
  * @param entityMgr the Entity Manager, used to respond to data queries
  * @throws WG_Exception if debugEnabled==true and this is not a developer build
  */
  public void initialize(SB_Config config,SB_SingletonBook book) throws SB_Exception
  {
    _debugEnabled = config.debugEnabled;

    if (!_debugEnabled)
      return;

    _logger = book.getLogger();
    _queryHandler = new SB_EngineQueryInterface( book.getEntityManager(), _logger, book );
    _debugServer = new SB_DebugServer(book);
    _breakpointMgr = new SB_BreakpointManager( _queryHandler, this, _logger );
    _eventHandler = new SB_EventHandler( _queryHandler, this, _logger );
    _messageHandler = new SB_MessageHandler( _debugServer, _breakpointMgr, _queryHandler, _eventHandler, _logger );

    // set up the debug server
    if (!_debugServer.Initialize())
      throw new SB_Exception("Unable to initialize network layer for Interactive Debugging!");

    //Wait for a connection
    if (!_debugServer.WaitForConnection( config.debugConnectTimeout ))
    {
      //_debugServer.Shutdown();  //Don't call the shutdown function since we don't actually have a connnection yet
      _debugEnabled = false;

      throw new SB_Exception("Waited for Interactive Debugging connect but it never connected");
    }

    // wait for an external debug client to connec
    if (!_debugServer.EstablishSession( config.debugConnectTimeout ))
    {
      _logger.log("Unable to establish connection to debugging client.",SB_Logger.ERROR);
      Shutdown();
      return;
    }

    // wait for the client's ok to start the simulation
    SB_DebugMessage msg = _messageHandler.WaitForMessage( SB_DebugMessage.kDBG_GUI_START, SB_DebugMessage.kDBG_GUI_RUN_TO_FINAL, true, this);
    if (msg == null)
    {
      Shutdown(false);
      return;
    }

    _eventHandler.HandleStepMessage(msg);
  }

  /**
  * Terminates the debugger, ending any current interactive debugging session.
  * The current simulation run is not interrupted, and further calls to the
  * debugger are ignored.
  * @param engineInitiated true if the engine ordered the shutdown, false if the client did (true was default)
  */
  public void Shutdown(boolean engineInitiated) throws SB_Exception
  {
    if (!_debugEnabled)
      return;

    if (engineInitiated)
      _debugServer.EndSessionServer();
    else
      _debugServer.EndSessionClient();

    _debugServer.Shutdown();
    _debugEnabled = false;
  }

  public void Shutdown() throws SB_Exception
  {
    Shutdown(true);
  }

// Event-based Debugging Methods

  /**
  * Called on each clock tick to update the debugger.  Processes any incoming
  * messages from the external debugger.
  */
  public void Update() throws SB_Exception
  {
    if (!_debugEnabled)
      return;

    boolean shutdown = _messageHandler.HandleMessages();

    if (shutdown)
      Shutdown(false);
  }

  /**
  * Notifies the debugger that the given event has occurred.  If the debugger is
  * connected to the Behavior Editor, the event will be forwarded to the Editor.
  */
  public void RecordEvent(int eventType, DMFieldMap eventFields)
  {
    if (!_debugEnabled)
    {
      return;
    }

    try
    {
      SB_DebugEvent event = new SB_DebugEvent(eventType, eventFields);

      _messageHandler.NotifyClient(event);
      boolean shutdown = _eventHandler.ProcessEvent(event);

      if (!shutdown)
      {
        SB_DebugEvent breakptEvent = _breakpointMgr.CheckBreakpoints(event);

        if (breakptEvent != null)
        {
          _messageHandler.NotifyClient(breakptEvent);
          shutdown = _eventHandler.ProcessEvent(breakptEvent);
        }
      }

      if (shutdown)
        Shutdown(false);
    }
    catch(SB_Exception ex)
    {
      _logger.log(ex.getMessage(), SB_Logger.ERROR);
    }
  }

  public SB_Debugger()
  {
    _debugEnabled = false;
    _queryHandler = null;
    _debugServer = null;
    _messageHandler = null;
    _eventHandler = null;
    _breakpointMgr = null;
  }

//Private Variables

  private boolean                 _debugEnabled;

  private SB_EngineQueryInterface _queryHandler;
  private SB_DebugServer	  			_debugServer;
  private SB_MessageHandler	 			_messageHandler;
  private SB_EventHandler	  			_eventHandler;
  private SB_BreakpointManager    _breakpointMgr;
  private SB_Logger               _logger;
}