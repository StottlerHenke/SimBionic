
package com.stottlerhenke.simbionic.engine.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.EIdType;
import com.stottlerhenke.simbionic.common.SB_ID;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.debug.DMFieldMap;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.comm.SB_CommCenter;
import com.stottlerhenke.simbionic.engine.comm.SB_CommLink;
import com.stottlerhenke.simbionic.engine.core.SB_Behavior;
import com.stottlerhenke.simbionic.engine.core.SB_Entity;
import com.stottlerhenke.simbionic.engine.core.SB_VariableMap;
import com.stottlerhenke.simbionic.engine.debug.EEventType;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;

/**
 * The Entity Manager keeps track of all entities controlled by SimBionic and handles the
 * creation and destruction of entities as requested by the simulation.  Its primary
 * duty, however, is dynamically scheduling entity updates to ensure a balanced processor
 * load that does not exceed the maximum CPU allocation for SimBionic (as defined by the
 * simulation developer).
 */
public class SB_EntityManager implements Serializable 
{
  //public static final int MAX_UPDATE_PERIOD;
  public int DEFAULT_UPDATE_FREQ = 0;
  public int DEFAULT_UPDATE_PRIORITY = 0;
  private HashMap  _entities = new HashMap(); //Key = Long, Value = SB_EntityRecord
  private SB_SchedulingAlg	_scheduler;
  private SB_VariableMap	_globals;
  private long			_startTime;		// wallclock time at which the simulation run started
  private SB_ID			_currentEntity;
  private SB_CommCenter        _commCenter;
  private SB_IdDispenser      _dispenser;
  transient private SB_SingletonBook   _book;

  public SB_EntityManager(SB_SingletonBook book)
	{
    _scheduler = null;
    _globals = null;
    _startTime = 0;
    _currentEntity = null;
    _commCenter = null;
    _dispenser = new SB_IdDispenser();
    _book = book;
  }

  public void Initialize(SB_SchedulingAlg scheduler)
		throws SB_Exception
  {
    _startTime = Math.round(System.currentTimeMillis()/1000);
    _scheduler = scheduler;
    _commCenter = new SB_CommCenter();
  }

//Administrative Methods

	/**
	 * Sets the global variable template.
	 * @param globalTemplate the global variable template to use
	 */
  public void setGlobalTemplate(SB_VariableMap globalTemplate)
	{
		_globals = globalTemplate;
	}


	public void createEntity( SB_ID newId, String entityName)
		throws SB_Exception
	{
	  SB_CommLink link = new SB_CommLink(newId,_commCenter);
	  SB_Entity newEntity = new SB_Entity(newId,entityName,_globals, link, _book);

	  if (SIM_Constants.DEBUG_INFO_ON)
			_book.getLogger().log("[" + newId.toString() + "]\tENTITY:\tcreated",SB_Logger.ENTITY);

	  SB_EntityRecord newRecord = new SB_EntityRecord(newEntity,DEFAULT_UPDATE_FREQ,DEFAULT_UPDATE_PRIORITY);
	  _entities.put(new Long(newId._id), newRecord);

	  _scheduler.AddEntity(newRecord);

	  if( SIM_Constants.AI_DEBUGGER )
	  {
			DMFieldMap fields = new DMFieldMap();
			fields.ADD_ID_FIELD("entity", newId._id);
			fields.ADD_STR_FIELD("name", entityName );
			_book.getDebugger().RecordEvent(EEventType.kEVT_ENTITY_CREATED, fields);
	  }
	}

	public SB_ID createEntity(String entityName) throws SB_Exception
	{
	  SB_ID newId = _dispenser.ClaimId(EIdType.kEntityId);
	  createEntity(newId, entityName);
	  return newId;
	}

	public void createEntity(SB_ID newId,String entityName,String behaviorId,ArrayList params,int updateFreq,int updatePriority)
		throws SB_Exception
	{
	  SB_CommLink link = new SB_CommLink(newId,_commCenter);
	  SB_Entity newEntity = new SB_Entity(newId,entityName,_globals, link, _book);

		if (SIM_Constants.DEBUG_INFO_ON)
			_book.getLogger().log("[" + newId.toString() + "]\tENTITY:\tcreated",SB_Logger.ENTITY);

	  if( SIM_Constants.AI_DEBUGGER )
	  {
	    DMFieldMap fields = new DMFieldMap();
	    fields.ADD_ID_FIELD("entity", newId._id);
	    fields.ADD_STR_FIELD("name", entityName );

	    _book.getDebugger().RecordEvent(EEventType.kEVT_ENTITY_CREATED, fields);
	  }

	  SB_EntityRecord newRecord = new SB_EntityRecord(newEntity,updateFreq,updatePriority);
	  _entities.put(new Long(newId._id), newRecord);

	  _scheduler.AddEntity(newRecord);

	  newEntity.SetBehavior(behaviorId,params, _book);
	}

    public SB_ID createEntity( String entityName,  String behaviorId, ArrayList params,int updateFreq,int updatePriority)
    throws SB_Exception
    {
      SB_ID newId = _dispenser.ClaimId(EIdType.kEntityId);
      createEntity(newId, entityName, behaviorId, params, updateFreq, updatePriority);
      return newId;
    }

    public SB_Entity GetEntity( SB_ID id){
      SB_EntityRecord record = GetRecord(id);
      return (record == null) ? null : record.GetEntity();
    }

    public void PushBehavior( SB_ID entityId, String behaviorId, ArrayList params)
    throws SB_Exception
    {
      SB_EntityRecord record = GetRecord(entityId);
      if (record != null){
        record.GetEntity().PushBehavior(behaviorId,params, _book);
      }

      if (SIM_Constants.DEBUG_INFO_ON)
            _book.getLogger().log("[" + entityId.toString() + "]\tENTITY:\tpush behavior " + behaviorId,SB_Logger.ENTITY);
    }

    public void SetBehavior( SB_ID entityId, String behaviorId, ArrayList params)
    throws SB_Exception
    {
      SB_EntityRecord record = GetRecord(entityId);
      if (record != null){
        record.GetEntity().SetBehavior(behaviorId,params, _book);
      }

		  if (SIM_Constants.DEBUG_INFO_ON)
				_book.getLogger().log("[" + entityId.toString() + "]\tENTITY:\tset behavior " + behaviorId,SB_Logger.ENTITY);

      if( SIM_Constants.AI_DEBUGGER )
      {
          DMFieldMap fields = new DMFieldMap();
          fields.ADD_ID_FIELD("entity", entityId._id);
          fields.ADD_STR_FIELD("behavior", behaviorId );
          SB_Behavior behavior = record.GetEntity().GetState().GetExecStack().getCurrentFrame().GetBehavior();
          fields.ADD_SA_FIELD("polyIndices", behavior.GetHierarchyIndices());
          _book.getDebugger().RecordEvent(EEventType.kEVT_BEHAVIOR_CHANGED, fields);
      }
    }

    public boolean IsEntityFinished( SB_ID entityId){
      SB_EntityRecord record = GetRecord(entityId);
      if (record != null){
        return record.GetEntity().GetState().IsFinished();
      }
      return false;
    }


    public SB_Variable GetGlobal( SB_ID entityId, String varName)
    throws SB_Exception
    {
      SB_EntityRecord record = GetRecord(entityId);
      if (record != null){
          return record.GetEntity().GetState().GetGlobal(varName);
      }
      return null;
    }

    public void SetGlobal( SB_ID entityId, String varName, SB_Variable value)
    throws SB_Exception
    {
      SB_EntityRecord record = GetRecord(entityId);
      if (record != null){
          record.GetEntity().GetState().SetGlobal(varName, value, _book.getLogger());
      }
    }

    public void SetUpdateFreq(int newFreq, SB_ID entityId)throws SB_Exception{
      SB_EntityRecord record = GetRecord(entityId);
      if (record != null){
				if (SIM_Constants.DEBUG_INFO_ON)
					 _book.getLogger().log("[" + entityId.toString() + "]\tENTITY:\tset freq " + newFreq,SB_Logger.ENTITY);

	      record.SetUpdateFreq(newFreq);
	      _scheduler.RescheduleEntity(record);
      }
    }

    public void SetUpdatePriority(int newPriority, SB_ID entityId)throws SB_Exception{
      SB_EntityRecord record = GetRecord(entityId);

      if (record != null){
				if (SIM_Constants.DEBUG_INFO_ON)
					 _book.getLogger().log("[" + entityId.toString() + "]\tENTITY:\tset priority " + newPriority,SB_Logger.ENTITY);

        record.SetUpdatePriority(newPriority);
        _scheduler.RescheduleEntity(record);
      }
    }

    public void DestroyEntity( SB_ID entityId) throws SB_Exception{
      SB_EntityRecord it = (SB_EntityRecord)_entities.get(new Long(entityId._id));

      if (it == null)
        return;		// no such entity

      _scheduler.RemoveEntity(it);
      _entities.remove(new Long(entityId._id));

		  if (SIM_Constants.DEBUG_INFO_ON)
				_book.getLogger().log("[" + entityId.toString() + "]\tENTITY:\tdestroyed",SB_Logger.ENTITY);

      if( SIM_Constants.AI_DEBUGGER )
      {
          DMFieldMap fields = new DMFieldMap();
          fields.ADD_ID_FIELD("entity", entityId._id);
          _book.getDebugger().RecordEvent(EEventType.kEVT_ENTITY_DESTROYED, fields);
      }
    }


    public void Update()throws SB_Exception{
      long currentTime = _scheduler.Tick();

      SB_EntityRecord updateRec = _scheduler.GetFirstEntityToUpdate();

		  if (SIM_Constants.DEBUG_INFO_ON)
			   _book.getLogger().log("\r\nMSG:\tstarting tick " + _scheduler.GetTicks(),SB_Logger.TICK);

      while (updateRec != null){
        _currentEntity = (updateRec.GetEntity().GetId());

        updateRec.Update( currentTime, _book.getDebugger(), _book.getLogger() );

        updateRec = _scheduler.GetNextEntityToUpdate();
      }

		  if (SIM_Constants.DEBUG_INFO_ON)
			   _book.getLogger().log("MSG:\tending tick " + _scheduler.GetTicks(),SB_Logger.TICK);
    }

    public void Update( SB_ID entityId) throws SB_Exception {
      SB_EntityRecord record = GetRecord(entityId);

      if (record != null){
        _currentEntity = (record.GetEntity().GetId());

        if (SIM_Constants.DEBUG_INFO_ON)
            _book.getLogger().log("\r\nMSG:\tupdating single entity, tick" + _scheduler.GetTicks(),SB_Logger.TICK);
        
        record.Update( _scheduler.GetTicks(), _book.getDebugger(), _book.getLogger() );
        
        if (SIM_Constants.DEBUG_INFO_ON)
            _book.getLogger().log("\r\nMSG:\tdone updating single entity, tick " + _scheduler.GetTicks(),SB_Logger.TICK);
      }
    }

    public SB_ID GetCurrentEntity(){
      return _currentEntity;
    }

  public void Shutdown()
	{
  	if (SIM_Constants.AI_DEBUGGER)
  	{
  		Iterator entityIt = _entities.keySet().iterator();
  		while (entityIt.hasNext())
  		{
		    DMFieldMap fields = new DMFieldMap();
		    fields.ADD_ID_FIELD("entity", ((Long)entityIt.next()).longValue());
		    _book.getDebugger().RecordEvent(EEventType.kEVT_ENTITY_DESTROYED, fields);
  		}
  	}

    _entities.clear();
    _scheduler = null;
    _globals = null;
    _commCenter = null;

  }

  public SB_IdDispenser GetIdDispenser()
  {
    return _dispenser;
  }
  
  /**
	 * Trickle down finishing to all SB_EntityRecords, comm center, and scheduler
	 * 
	 * @param book
	 * @throws SB_Exception
	 */
	public void finishDeserialization(SB_SingletonBook book) throws SB_Exception
  {
	    _book = book;
	    
	    Collection values = _entities.values();
	    for (Iterator it = values.iterator(); it.hasNext();) 
	    {
	        ((SB_EntityRecord)it.next()).finishDeserialization(book);
	    }
	    
	    _commCenter.finishDeserialization(book);
	    _scheduler.finishDeserialization(book);  
  }



    public SB_EntityRecord GetRecord( SB_ID entityId){
      SB_EntityRecord it = (SB_EntityRecord)_entities.get(new Long(entityId._id));
      return (it == null) ? null : it;
    }

}