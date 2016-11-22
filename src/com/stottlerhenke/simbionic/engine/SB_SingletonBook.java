package com.stottlerhenke.simbionic.engine;

import java.util.ArrayList;

import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.classes.SB_ClassMap;
import com.stottlerhenke.simbionic.engine.core.SB_BehaviorRegistry;
import com.stottlerhenke.simbionic.engine.core.SB_DelayedAction;
import com.stottlerhenke.simbionic.engine.debug.SB_Debugger;
import com.stottlerhenke.simbionic.engine.file.SB_FileRegistry;
import com.stottlerhenke.simbionic.engine.manager.SB_EntityManager;


/**
 * This class is a container for all of the singletons used by
 * an instance of the engine.
 */
public class SB_SingletonBook
{
  private SB_EntityManager _entityMgr;
  private SB_Logger _logger;
  private SB_Debugger _debugger;
  private SB_BehaviorRegistry _registry;
  private SB_SimInterface _sim; //Single sim interface used before A/P modules
  private SB_FileRegistry _fileRegistry;
  private SB_ClassMap _classMap = new SB_ClassMap();
  private ArrayList _delayedActions;
  private SB_JavaScriptEngine _jsEngine;

  public SB_SingletonBook()
  {
  }
  
  
  public void setJavaScriptEngine(SB_JavaScriptEngine jsEngine) {
     _jsEngine = jsEngine;
  }
  public SB_JavaScriptEngine getJavaScriptEngine() {
     return _jsEngine;
  }

  public void setDebugger(SB_Debugger debugger) {_debugger = debugger; }
  public SB_Debugger getDebugger() {return _debugger; }

  public void setLogger(SB_Logger logger) {_logger = logger; }
  public SB_Logger getLogger() {return _logger; }

  public void setSimInterface(SB_SimInterface sim) {_sim = sim; }
  public SB_SimInterface getSimInterface() {return _sim; }


  public void setBehaviorRegistry(SB_BehaviorRegistry registry) {_registry = registry;}
  public SB_BehaviorRegistry getBehaviorRegistry() {return _registry; }

  public void setFileRegistry(SB_FileRegistry registry) {_fileRegistry = registry;}
  public SB_FileRegistry getFileRegistry() {return _fileRegistry; }

  public void setEntityManager(SB_EntityManager entityMgr) {_entityMgr = entityMgr;}
  public SB_EntityManager getEntityManager() {return _entityMgr;}
  
  public void setDelayedActions(ArrayList delayedActions) {_delayedActions = delayedActions;}
  public ArrayList getDelayedActions() {return _delayedActions; }
  
  public SB_ClassMap getUserClassMap() { return _classMap; }

  /**
   * Sets all singletons to null.
   */
  public void clearAll()
	{
  	_debugger = null;
  	//_parser = null;
  	_sim = null;
  	_registry = null;
  	_fileRegistry = null;
  	_entityMgr = null;
    _classMap = new SB_ClassMap();
    
    
    _jsEngine = null;
  }

  public void queueDelayedAction(SB_DelayedAction action)
  {
    _delayedActions.add(action);
  }

  public SB_DelayedAction dequeueDelayedAction()
  {
    SB_DelayedAction retVal = null;

    if(_delayedActions != null && _delayedActions.size() > 0 )
    {
      retVal = (SB_DelayedAction) _delayedActions.get(0);
      _delayedActions.remove(0);
    }

    return retVal;
  }
}