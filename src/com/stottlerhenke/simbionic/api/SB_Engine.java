package com.stottlerhenke.simbionic.api;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;

import com.stottlerhenke.simbionic.common.SB_ID;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.Version;
import com.stottlerhenke.simbionic.engine.ActionPredicateAPI;
import com.stottlerhenke.simbionic.engine.SB_JavaScriptEngine;
import com.stottlerhenke.simbionic.engine.SB_SimInterface;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.comm.SB_CommMsg;
import com.stottlerhenke.simbionic.engine.core.SB_BehaviorRegistry;
import com.stottlerhenke.simbionic.engine.core.SB_DelayedAction;
import com.stottlerhenke.simbionic.engine.core.SB_Entity;
import com.stottlerhenke.simbionic.engine.debug.SB_Debugger;
import com.stottlerhenke.simbionic.engine.file.SB_FileRegistry;
import com.stottlerhenke.simbionic.engine.file.SB_ProjectSpec;
import com.stottlerhenke.simbionic.engine.manager.SB_DefaultScheduler;
import com.stottlerhenke.simbionic.engine.manager.SB_EntityManager;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;

/**
 * This class implements the API for the Simbionic runtime library.
 */

public class SB_Engine
{
  public SB_Exception _lastError;
  private SB_Config _config;       		//a copy of the current configuration file, used for LoadProject
  private SB_SingletonBook _book;
  
  /**
   * JavaScript engine
   */
  private SB_JavaScriptEngine _jsEngine;
  

  

  public SB_Engine()
  {
    _book = new SB_SingletonBook();
    _book.setLogger(new SB_Logger());
  }

  /**
   * Initializes the runtime system and prepares it for operation.
   *
   * @param config configuration parameters for the runtime engine
   * @return kOK on successful initialization, otherwise an error code
   */
  public SB_Error initialize(SB_Config config)
  {
    _config = new SB_Config(config, _book.getLogger());	//this copy is created for later use by swapProject

    // create a JavaScript engine
    _jsEngine = new SB_JavaScriptEngine(config);
    

    if (!validInterfaceVersion(config))
    {
      return SB_Error.kVersion;
    }

    // Set the javaScript engine
    _book.setJavaScriptEngine(_jsEngine);
    _book.setSimInterface( new SB_SimInterface() );
    ActionPredicateAPI.initInstance(_book);
    _book.setBehaviorRegistry( new SB_BehaviorRegistry(_book.getLogger()) );
    _book.setDebugger( new SB_Debugger() );
    _book.setFileRegistry( new SB_FileRegistry() );
    _book.setEntityManager( new SB_EntityManager(_book) );

    try
    {
      loadProjectSpec(config.fileURL);
      _book.getBehaviorRegistry().initializeNewProject(_book);

      SB_DefaultScheduler sched = new SB_DefaultScheduler(_book.getLogger());
      sched.Initialize(config.maxUpdatePeriod);

      _book.getEntityManager().Initialize(sched);

      if(SIM_Constants.AI_DEBUGGER)
      {
        _book.getDebugger().initialize(config,_book);
      }
    }
    catch (SB_Exception e)
    {
            // initialization failed
      SB_Error errCode = SB_Error.getType(((SB_Exception)e).GetErrorCode().getState());

      if (SIM_Constants.DEBUG_INFO_ON)
        _book.getLogger().log("!! " + ((SB_Exception)e).getMessage(),SB_Logger.ERROR);

      saveLastError(e);

      return errCode;
    }
    catch (Exception e )
    {
      _book.getLogger().log("!! " + e.toString(), SB_Logger.ERROR);
      e.printStackTrace();
      return SB_Error.kFailure;
    }

    if(SIM_Constants.DEBUG_INFO_ON)
      _book.getLogger().log("** Initialization complete.",SB_Logger.MILESTONE);

    return SB_Error.kOK;
  }
  

  /**
   * Terminates the runtime system, deallocating all memory.
   * NOTE: The logging system is not affected by terminate. User
   * @return kOK on successful termination, otherwise an error code
   */
  public SB_Error terminate()
  {
      if( _book == null ) //Never Initialized
          return SB_Error.kOK;
     
      try
      {
          doDelayedActions();
      }
      catch(SB_Exception ex)
      {
          saveLastError( ex );
          return SB_Error.kFailure;
      }
      
      if(SIM_Constants.DEBUG_INFO_ON)
          _book.getLogger().log("** Terminating runtime engine...",SB_Logger.MILESTONE);
      
      SB_EntityManager em = _book.getEntityManager();
      if(em!=null)
          em.Shutdown();
      
      SB_BehaviorRegistry br = _book.getBehaviorRegistry();
      if(br!=null)
          br.shutdown();
      
      if(SIM_Constants.AI_DEBUGGER)
      {
          try
          {
              SB_Debugger debugger = _book.getDebugger();
              if (debugger != null)
                  debugger.Shutdown();
          }
          catch(SB_Exception ex)
          {
              saveLastError( ex );
              return SB_Error.kFailure;
          }
      }
      
      if(SIM_Constants.DEBUG_INFO_ON)
          _book.getLogger().log("** Termination complete.",SB_Logger.MILESTONE);
      
      _book.clearAll();
      return SB_Error.kOK;
  }

 /**
    * Unloads the current project and destroys all entities, then loads the
    * specified new project.  Both projects must use the same SB_Interface
    * (ie the same set of actions and predicates).  The existing log print
    * streams and configuration (other than behavior URL and name) are
    * maintained.
    *
    * @param behaviorFileURL url of the project sim file
    * @return kOK on successful loading, otherwise an error code
    */
  public SB_Error swapProject(URL behaviorFileURL)
  {
    _config.fileURL = behaviorFileURL;

    SB_Error err = terminate();

    if( err == SB_Error.kOK )
    {
      err = initialize(_config);
    }

    return err;
  }

  /**
   * Retrieves the version information for the engine.
   * @return the version string
   */
  public String getVersion()
	{
    return Version.SYSTEM_INFO;
	}

  /**
   * Returns the error message for the last interface method call that
   * returned a value other than kOK.
   * @return last error message
   */
  public String getLastError()
	{
		return _lastError.getMessage();
	}

  public SB_Exception getLastException()
	{
		return _lastError;
	}
  
  /**
   * Advances the runtime clock by one tick, updating entities as necessary.
   * @return kOK on success, otherwise an error code
   */
  public SB_Error update()
  {
    try
    {
      doDelayedActions();

      if(SIM_Constants.AI_DEBUGGER)
      {
        _book.getDebugger().Update();
      }

      _book.getEntityManager().Update();

       doDelayedActions();
    }
    catch (SB_Exception exc)
    {
      saveLastError( exc);
      return SB_Error.kFailure;
    }
    return SB_Error.kOK;
  }

  /**
   * Updates the specified entity, bypassing the entity manager completely.
   * @param entityId the entity to update
   * @return kOK on success, otherwise an error code
   */
  public SB_Error updateEntity(long entityId)
  {
    try
    {
      doDelayedActions();

      if(SIM_Constants.AI_DEBUGGER)
        _book.getDebugger().Update();

      _book.getEntityManager().Update( SB_SimInterface.TranslateId(entityId) );

       doDelayedActions();
    }
    catch (SB_Exception exc)
    {
      saveLastError( exc );
      return SB_Error.kFailure;
    }

    return SB_Error.kOK;
  }

  /**
   * Creates a new entity in the runtime system, placing it under Simbionic's control with
   * the specified initial behavior.  Note that the polymorphism for this behavior is chosen
   * based on the default values of the entity's global variables.  To select another
   * polymorphism, call SetEntityGlobal() and then SetBehavior() after creating the entity.
   * @param entityName the human-readable name of the entity
   * @param behaviorId identifies the entity's initial behavior
   * @param params the parameters to be passed to the initial behavior
   * @param updateFreq the initial update frequency for the entity [-1..100]
   * @param updatePriority the initial update priority for the entity [0..max_int]
   * @return the unique Simbionic identifier for this entity, or INVALID_ID on failure
   */
  public long createEntity(String entityName,String behaviorId,ArrayList params,int updateFreq,int updatePriority)
	{
		SB_ID newId;

		try {
			ArrayList varParams = SB_SimInterface.ConvertParams(params, _book);

			newId = _book.getEntityManager().createEntity(entityName,behaviorId,varParams,updateFreq,updatePriority);
		}
		catch (SB_Exception exc)
		{
			saveLastError( exc );
			return SB_Param.INVALID_ID;
		}

		return SB_SimInterface.TranslateId(newId);
	}

  /**
   * Creates a new entity in the runtime system, placing it under Simbionic's control.
   * By default, it will update on every tick with priority 0.
   *
   * NOTE: the created entity has not yet been assigned a base-level behavior.
   * You MUST call SetBehavior() before calling Update() on this entity the first time!
   *
   * @param entityName the human-readable name of the entity
   * @return the unique Simbionic identifier for this entity, or INVALID_ID on failure
   */
  public long makeEntity(String entityName)
	{
		SB_ID newId;

		try {
			newId = _book.getEntityManager().createEntity(entityName);
		}
		catch (SB_Exception exc)
		{
	    saveLastError( exc );
	    return SB_Param.INVALID_ID;
		}

		return SB_SimInterface.TranslateId(newId);
	}

  /**
   * Sets the value of the named global variable for the specified entity.
   * @param entityId the ID of the entity whose variable should be set
   * @param varName the unique name of the global variable to set
   * @param value the new value for the global variable
   * @return kOK on success, otherwise an error code
   */
  public SB_Error setEntityGlobal( long entityId, String varName,SB_Param value)
	{
		try 
		{
		  SB_Variable oldValue = _book.getEntityManager().GetGlobal(SB_SimInterface.TranslateId(entityId),varName);
		  
		  if( oldValue == null)
		      throw new SB_Exception("Error setting global variable: '"+ varName + "' not found.");
		  
	    SB_Variable varValue = SB_SimInterface.ConvertParam(value, _book);

	    _book.getEntityManager().SetGlobal(SB_SimInterface.TranslateId(entityId),varName, varValue );
		}
		catch (SB_Exception exc)
		{
	    saveLastError( exc);
	    return SB_Error.kFailure;
		}
		return SB_Error.kOK;
	}

  /**
   * Returns the value of the named global variable for the specified entity.
   * @param entityId the ID of the entity whose variable should be returned
   * @param varName the unique name of the global variable to return
   * @return the value of the specified global (type = kAI_Invalid if entity/variable does not exist)
   */
  public SB_Param getEntityGlobal(long entityId, String varName)
	{
		SB_Param paramValue = new SB_Param();

		try {
	    SB_Variable value = null;
	    value = _book.getEntityManager().GetGlobal(SB_SimInterface.TranslateId(entityId),varName);

	    paramValue = SB_SimInterface.ConvertVariable(value);
		}
		catch (SB_Exception exc)
		{
	    saveLastError( exc );
	    return paramValue;	// default param value is kInvalid
		}

		return paramValue;
	}

  /**
   * Determines if the specified entity has completed all behaviors.
   * @param entityId the ID of the entity to be checked
   * @return true if the entity's execution stack is empty, false otherwise
   */
  public boolean isEntityFinished(long entityId)
	{
  	return _book.getEntityManager().IsEntityFinished(SB_SimInterface.TranslateId(entityId));
	}

  /**
   * Sets the specified entity's current base-level behavior, initializing it
   * with the specified parameters.  The polymorphism for the behavior is chosen
   * based on the current values of the entity's global variables.
   * @param entityId the ID of the entity to be changed
   * @param behaviorId the entity's new behavior
   * @param params the parameters to be passed to the behavior
   * @return kOK on success, otherwise an error code
   */
  public SB_Error setBehavior(long entityId, String behaviorId, ArrayList<SB_Param> params)
	{
		try {
	    ArrayList paramValues = SB_SimInterface.ConvertParams(params, _book);
	    _book.getEntityManager().SetBehavior( SB_SimInterface.TranslateId(entityId),behaviorId, paramValues );
		}
		catch (SB_Exception exc)
		{
	    saveLastError( exc );
	    return SB_Error.kFailure;
		}
		return SB_Error.kOK;
	}

  /**
   * Sets the update frequency for the specified entity.  Lower frequency numbers
   * will be updated more frequently than higher frequency numbers, with zero
   * being updated on each tick.  Entities with negative frequencies are
   * placed on hold and will not be updated until their frequencies change.
   * @param newFreq new update frequency for the entity [-1..100]
   * @param entityId entity whose frequency is being changed
   * @return kOK on success, otherwise an error code
   */
  public SB_Error setUpdateFreq(int newFreq, long entityId)
	{
		try {
	    _book.getEntityManager().SetUpdateFreq(newFreq, SB_SimInterface.TranslateId(entityId));
		}
		catch (SB_Exception exc)
		{
	    saveLastError( exc );
	    return SB_Error.kFailure;
		}
		return SB_Error.kOK;
	}

  /**
   * Sets the update priority for the specified entity.  This priority is used
   * to determine the order in which entities are updated within a single update.
   * Entities with lower priority values will be updated before those with higher values.
   * @param newPriority new update priority for the entity [0..max_int]
   * @param entityId entity whose frequency is being changed
   * @return kOK on success, otherwise an error code
   */
  public SB_Error setUpdatePriority(int newPriority, long entityId)
	{
		try {
			_book.getEntityManager().SetUpdatePriority(newPriority, SB_SimInterface.TranslateId(entityId));
		}
		catch (SB_Exception exc)
		{
	    saveLastError( exc );
	    return SB_Error.kFailure;
		}
		return SB_Error.kOK;
	}

  /**
   * Destroys the specified entity within the runtime system, removing it from Simbionic's control.
   * @param entityID the Simbionic ID of the entity to destroy
   * @return kOK on success, otherwise an error code
   */
  public SB_Error destroyEntity(long entityId)
	{
  	try 
		{
	    _book.getEntityManager().DestroyEntity( SB_SimInterface.TranslateId(entityId) );
    }
    catch (SB_Exception exc)
    {
	    saveLastError( exc );
	    return SB_Error.kFailure;
    }
    return SB_Error.kOK;
	}

  /**
   * Logs the given message to Simbionic's log file.
   * @param msg the message to be logged
   */
  public void log(String msg)
	{
	  _book.getLogger().log(msg);
	}

  /**
   * Declares a print stream that logging messages will be sent to.
   * More than one print stream can be specified.
   *
   * @param out The stream that messages should be written to.
   */
  public void registerLogPrintStream(PrintStream logPrintStream)
  {
    _book.getLogger().register(logPrintStream, SB_Logger.ALL );
  }

  /**
   * Declares a print stream that logging messages will be sent to.
   * More than one print stream can be specified.
   *
   * @param out The stream that messages should be written to.
   * @param contentFlags set of bit flags that controls what type of content should be logged
   */
  public void registerLogPrintStream(PrintStream logPrintStream,int contentFlags)
  {
    _book.getLogger().register(logPrintStream, contentFlags );
  }
  
  public void unregisterLogPrintStream(PrintStream logPrintStream){
	  _book.getLogger().unregister(logPrintStream);
  }

 
 
  

  /**
   * Unloads all behaviors and destroys all entities.  The master project
   * file data (actions, predicates, globals, descriptors) remains loaded.
   * @return kOK on success, otherwise an error code
   */
	public SB_Error unloadAll()
	{
		_book.getBehaviorRegistry().removeAllBehaviors();
		_book.getFileRegistry().removeAllAssociations();
		return SB_Error.kOK;
	}



 /**
   * Lets an entity send a message and number code to a named group of entities.
   *
   * @param entityId the entity to send the message
   * @param recipient group to receive the message
   * @param type number code of the message
   * @param value the actual message
   * @return kOK on success, otherwise an error code
   */
  public SB_Error sendMsg(long entityId, String recipient, int type, SB_Param value) throws SB_Exception
  {
    SB_Entity entity = _book.getEntityManager().GetEntity( SB_SimInterface.TranslateId(entityId) );
    SB_Variable sbvalue = SB_SimInterface.ConvertParamAuto(value, _book);
    SB_CommMsg msg = new SB_CommMsg( entity.GetId(), recipient, type, sbvalue );

    entity.GetCommLink().Send(msg);

    return SB_Error.kOK;
  }

  /**
   * Lets an entity create a virtual blackboard specified by a particular name, making it easy for any
   * entity to share information with any or all other entities in your program.
   *
   * @param entityId entity to create hte blackboard
   * @param bboardId the id of the blackboard
   * @return kOK on success, otherwise an error code
   */
  public SB_Error createBBoard(long entityId, String bboardId)
  {
    SB_Entity entity = _book.getEntityManager().GetEntity( SB_SimInterface.TranslateId(entityId) );
    entity.GetCommLink().CreateBBoard( bboardId, _book.getLogger() );

    return SB_Error.kOK;
  }

  /**
   * Lets an entity place information in a named section of a named virtual blackboard.
   * The information can then be read by any entity that "knows" the names of the blackboard and section.
   * The information remains available until it's either replaced (via another PostBBoard action) or the board
   * is destroyed (via the DestroyBBoard action).
   *
   * @param entityId entity to post information
   * @param bboardId id of the blackboard
   * @param region section of the blackboard to post to
   * @param value the actual message
   * @return kOK on success, otherwise an error code
   */
  public SB_Error postBBoard(long entityId, String bboardId, String region, SB_Param value) throws SB_Exception
  {
    SB_Entity entity = _book.getEntityManager().GetEntity( SB_SimInterface.TranslateId(entityId) );
    SB_Variable sbvalue = SB_SimInterface.ConvertParamAuto(value, _book);
    entity.GetCommLink().PostBBoard( bboardId, region, sbvalue);

    return SB_Error.kOK;
  }

  /**
   * Lets an entity access any virtual blackboard (created previously via the CreateBBoard action)
   * and read any information on the board (placed previously via the PostBBoard action) by specifying the
   * board's name and the section of the board storing the information. ReadBBoard therefore allows any entity to
   * obtain information from any other entity in your program.
   *
   * @param entityId entity to read he bboard
   * @param bboardId blackboard to read
   * @param region section of the blackboard to read
   * @return the value of the specified message (type = kAI_Invalid if entity/blackboard/region does not exist)
   */
  public SB_Param readBBoard(long entityId, String bboardId, String region)
  throws SB_Exception
  {
    SB_Entity entity = _book.getEntityManager().GetEntity( SB_SimInterface.TranslateId(entityId) );
    SB_Variable value = null;
    value = entity.GetCommLink().ReadBBoard( bboardId, region );

    return SB_SimInterface.ConvertVariable(value);
  }

  /**
   * Eliminates a named virtual blackboard, freeing up the memory taken up by the board and all
   * the information stored on it. Alternatively, an entity can "erase" the information on a blackboard
   * by destroying the board with this action and then immediately creating a new blank board with the
   * same name via CreateBBoard.
   *
   * @param entityId entity to destroy a blackboard
   * @param bboardId id of blackboard to destroy
   * @return kOK on success, otherwise an error code
   */
  public SB_Error destroyBBoard(long entityId, String bboardId)
  {
    SB_Entity entity = _book.getEntityManager().GetEntity( SB_SimInterface.TranslateId(entityId) );
    entity.GetCommLink().DestroyBBoard( bboardId );

    return SB_Error.kOK;
  }

  /**
   * Checks if the named blackboard exists.
   *
   * @param entityId entity checking the blackboard
   * @param bboardId the id of the blackboard
   * @return true if the blackboard exists, false otherwise
   */
  public boolean isBBoard(long entityId, String bboardId)
  {
    SB_Entity entity = _book.getEntityManager().GetEntity( SB_SimInterface.TranslateId(entityId) );
    return entity.GetCommLink().IsBBoard( bboardId );
  }  
  
	private boolean validInterfaceVersion(SB_Config config)
	{
	  if(SB_Config.getVersion().compareTo(Version.SIMBIONIC_INTERFACE_VERSION) != 0)
		{
			if (SIM_Constants.DEBUG_INFO_ON)
			  _book.getLogger().log("!! SB_Config version not compatible with engine version (" +
					  Version.SIMBIONIC_INTERFACE_VERSION + ")",SB_Logger.ERROR);
			return false;
		}
	  return true;
	}

	private void saveLastError(SB_Exception ex){
	  _lastError = ex;
	  if (_lastError == null)
	  	_lastError = new SB_Exception("Unknown error occurred.");
	  
	  if (SIM_Constants.DEBUG_INFO_ON)
	    _book.getLogger().logException(ex);
	}

  private void loadProjectSpec(URL projectURL) throws SB_Exception
  {
  	SB_ProjectSpec spec = new SB_ProjectSpec(projectURL);
  	spec.read(_book);
  }

  /**
   * Perform all delayed actions in the queue
   */
  private void doDelayedActions() throws SB_Exception
  {
      SB_DelayedAction action = _book.dequeueDelayedAction();
      
      try
      {
          while( action != null )
          {
              action.DoDelayedAction( _book.getEntityManager() );
              action = _book.dequeueDelayedAction();
          }
      }
      catch( SB_Exception ex)
      {
          if( action != null)
          {
              SB_Exception newEx = new SB_Exception("Delayed action/predicate failed: " + action.toString(), ex);
              _book.getLogger().logException(newEx);
              throw(newEx);
          }
          
          _book.getLogger().logException(ex);
          throw ex;
      }
  }

  
  
  
  /**
   * Save the curent SimBionic state to the given stream
   * @param str
   */
  public void serializeEngineState(ObjectOutputStream out) throws SB_Exception
  {
      try
      {
	      SB_EntityManager em = _book.getEntityManager();
	      if( em != null )
	      {
	          out.writeObject(em);
	          out.writeObject(_book.getDelayedActions());
	      }
      }
      catch(IOException ex)
      {
          SB_Exception newEx = new SB_Exception("IOException caught while serializing, see cause of this exception", ex);
          if(SIM_Constants.AI_DEBUGGER)
              _book.getLogger().logException(newEx);
          throw newEx;
      }
  }
  
  /**
   * Load the current SimBionic state from the given stream
   * @param in
   */
  public void deserializeEngineState(ObjectInputStream in) throws SB_Exception
  {
      try
      {
	      SB_EntityManager em = (SB_EntityManager) in.readObject();
	      _book.setEntityManager(em);
	      em.finishDeserialization(_book);
	      
	      ArrayList da = (ArrayList) in.readObject();
	      _book.setDelayedActions(da);
      }
      catch(IOException ex)
      {
          SB_Exception newEx = new SB_Exception("IOException caught while deserializing, see cause of this exception", ex);
          if(SIM_Constants.AI_DEBUGGER)
              _book.getLogger().logException(newEx);
          throw newEx;
      }
      catch(ClassNotFoundException ex)
      {
          SB_Exception newEx = new SB_Exception("IOException caught while deserializing, see cause of this exception", ex);
          if(SIM_Constants.AI_DEBUGGER)
              _book.getLogger().logException(newEx);
          throw newEx;
      }
  }
}