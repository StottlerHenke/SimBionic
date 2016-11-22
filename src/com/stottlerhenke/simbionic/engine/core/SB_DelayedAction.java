package com.stottlerhenke.simbionic.engine.core;

import java.io.Serializable;
import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_ID;
import com.stottlerhenke.simbionic.engine.ActionPredicateAPI;
import com.stottlerhenke.simbionic.engine.manager.SB_EntityManager;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;

/**
 * A class to keep track of and perform delayed actions 
 */

public class SB_DelayedAction implements Serializable 
{

  private int _id;
  private ArrayList _variables; //Array of SB_Variable objects
  private boolean _predicate;

  public SB_DelayedAction(int id, ArrayList variables, boolean bPredicate)
  {
    _id = id;
    _variables = variables;
    _predicate = bPredicate;
  }

  public void DoDelayedAction(SB_EntityManager mgr) throws SB_Exception
  {
    //@todo Add debugging message
  	//@todo Modify parameters such that SB_Param, not SB_Variables are passed in to avoid translating twice

    if( _predicate )
    {
      if(_id == ActionPredicateAPI.PRED_CreateEntity)
      {
    	  int freq = (Integer) _variables.get(3);
    	  int priority = (Integer) _variables.get(4);
    	  CreateEntity(
    			  mgr,
    			  (SB_Variable)_variables.get(5),
    			  (String)_variables.get(0),
    			  (String)_variables.get(1),
    			  (ArrayList) _variables.get(2),
    			  freq,
    			  priority
    	  );
      }
      else
        throw new SB_Exception("Call to illegal core delayed predicate ID %d." + _id);
    }
    else
    {
      if(_id == ActionPredicateAPI.ACTION_SetEntityGlobal )
      {
    	  long entityId = (Long) _variables.get(0);
    	  String varName = (String) _variables.get(1);
        SetEntityGlobal(mgr, entityId, varName, (SB_Variable)_variables.get(2));
      }
      else
          if(_id == ActionPredicateAPI.ACTION_PushBehavior)
          {
        	  long entityId = (Long) _variables.get(0);
        	  String behavior = (String) _variables.get(1);
        	  ArrayList params = (ArrayList) _variables.get(2);
        	  PushBehavior(mgr, entityId, behavior, params);
          }
      else
      if(_id == ActionPredicateAPI.ACTION_SetBehavior)
      {
    	  long entityId = (Long) _variables.get(0);
    	  String behavior = (String) _variables.get(1);
    	  ArrayList params = (ArrayList) _variables.get(2);
    	  SetBehavior(mgr, entityId, behavior, params);
      }
      else
      if(_id == ActionPredicateAPI.ACTION_SetUpdateFrequency)
      {
        int freq = (Integer) _variables.get(0);
        long entityId = (Long) _variables.get(1);
        SetUpdateFreq(mgr, freq, entityId);
      }
      else
      if(_id == ActionPredicateAPI.ACTION_SetUpdatePriority)
      {
        int priority = (Integer) _variables.get(0);
        long entityId = (Long) _variables.get(1);
        SetUpdatePriority(mgr, priority, entityId);
      }
      else
      if(_id == ActionPredicateAPI.ACTION_DestroyEntity)
      {
    	  long entityId = (Long) _variables.get(0);
    	  DestroyEntity( mgr, entityId );
      }
      else
        throw new SB_Exception("Call to illegal core delayed action ID %d." + _id);
    }
  }

  
  void CreateEntity( SB_EntityManager mgr, SB_Variable newId, String entityName, String behaviorId, ArrayList params,
		  int updateFreq,int updatePriority) throws SB_Exception
  {
     mgr.createEntity((SB_ID)newId.getValue(), entityName, behaviorId, params, updateFreq, updatePriority);
  }

  void SetEntityGlobal(SB_EntityManager mgr, long entityId, String varName, SB_Variable value) throws SB_Exception
  {
    mgr.SetGlobal(new SB_ID(entityId), varName, value);
  }

  void PushBehavior(SB_EntityManager mgr, long entityId, String behavior, ArrayList params) throws SB_Exception
  {
    mgr.PushBehavior(new SB_ID(entityId), behavior, params);
  }

  void SetBehavior(SB_EntityManager mgr, long entityId, String behavior, ArrayList params) throws SB_Exception
  {
    mgr.SetBehavior(new SB_ID(entityId), behavior, params);
  }

  void SetUpdateFreq(SB_EntityManager mgr, int newFreq, long entityId) throws SB_Exception
  {
    mgr.SetUpdateFreq(newFreq, new SB_ID(entityId));
  }

  void SetUpdatePriority(SB_EntityManager mgr, int newPriority, long entityId) throws SB_Exception
  {
    mgr.SetUpdatePriority(newPriority, new SB_ID(entityId));
  }

  void DestroyEntity(SB_EntityManager mgr, long entityId) throws SB_Exception
  {
    mgr.DestroyEntity( new SB_ID(entityId) );
  }
  
  public String toString()
  {
      return convertIdToString() + _variables.toString();
  }
  
  public String convertIdToString()
  {
      switch(_id)
      {
      case ActionPredicateAPI.PRED_CreateEntity:
          return "CreateEntity";
      case ActionPredicateAPI.ACTION_SetEntityGlobal:
          return "SetEntityGlobal";
      case ActionPredicateAPI.ACTION_PushBehavior:
          return "PushBehavior";
      case ActionPredicateAPI.ACTION_SetBehavior:
          return "SetBehavior";
      case ActionPredicateAPI.ACTION_SetUpdateFrequency:
          return "SetUpdateFrequency";
      case ActionPredicateAPI.ACTION_SetUpdatePriority:
          return "SetUpdatePriority";
      case ActionPredicateAPI.ACTION_DestroyEntity:
          return "DestroyEntity";
      }
      
      return "Unknown action/predicate";
  }
}