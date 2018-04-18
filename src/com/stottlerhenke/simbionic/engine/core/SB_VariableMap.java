package com.stottlerhenke.simbionic.engine.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_ID;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.debug.DMFieldMap;
import com.stottlerhenke.simbionic.engine.SB_SimInterface;
import com.stottlerhenke.simbionic.engine.debug.EEventType;
import com.stottlerhenke.simbionic.engine.debug.SB_Debugger;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;


/**
 * This class stores a collection of entity variables, providing read and write access 
 * to them.  It also provides a fast cloning operation to allow copies of itself to be 
 * made efficiently (through lazy copying of variables). 
 */
public class SB_VariableMap implements Serializable  
{
  private HashMap _variables = new HashMap();
  private HashMap _savedVars = new HashMap();
  private HashMap _deepSavedVars = new HashMap();
  private boolean _bSaving;

  public SB_VariableMap() {
    _bSaving = false;
  }

  /**
   * Add the variable with the given name and value a clone of the variable
   * @param name
   * @param value
   * @throws SB_Exception
   */
  public void AddVariable(String name, SB_Variable value) throws SB_Exception
  {
    _variables.put(name, value.Clone());
  }
  
  /**
   * add all the variables in otherVariable to the set of variables associated with
   * this map.&nbsp;The value of previous variables with the same name as added variables
   * gets replaced.
   * @param otherVariables
   */
  public void addVariables(SB_VariableMap otherVariables) {
	  if (otherVariables == null) return;
	  _variables.putAll(otherVariables._variables);
  }
  

  public SB_VariableMap Clone()
  throws SB_Exception
  {
    // #OPT: implement lazy cloning

    SB_VariableMap newMap = new SB_VariableMap();
    Iterator it = _variables.keySet().iterator();
    while(it.hasNext()){
      String key = (String)it.next();
      SB_Variable value = (SB_Variable)_variables.get(key);
      newMap.AddVariable(key, value.Clone());
    }
    return newMap;
  }
  public ArrayList GetVariableNames(){

    ArrayList names = new ArrayList();
    Iterator it = _variables.keySet().iterator();
    while(it.hasNext()){
      String key = (String)it.next();
      names.add(key);
    }
    return names;
  }

  public boolean IsVariable(String name){
    return _variables.get(name) != null;
  }

  public void SetVariable(String name, SB_Variable value, SB_Logger logger) throws SB_Exception{

    SB_Variable it = (SB_Variable)_variables.get(name);
    if (it == null)
      throw new SB_Exception("Variable " + name + " does not exist.");

    //Make sure the new value is the same type as the old value
    //If not, cast it to the correct type
    if(!it.getType().equals(value.getType()))
    {
      SB_Variable tempValue = it.Clone();
      tempValue.setValue(value.getValue());
      tempValue.setType(it.getType());
      value = tempValue;
    } 
    
    if (_bSaving)
    {
      // save the old value instead of deleting it.  Note that insert() only
      // inserts the item if no other variable with that name exists in the
      // map, so subsequent calls to SetVariable() for the same variable will
      // not affect the saved variable (ie only the original value is saved, which
      // is exactly the behavior we want)

      if( !_savedVars.containsKey(name) )
      {
          _savedVars.put( name, it);
      }

      if( !_deepSavedVars.containsKey(name))
      {
          _deepSavedVars.put( name, it.Clone());
      }

//      if(SIM_Constants.DEBUG_INFO_ON)
//         logger.log("ENTITY[*]: SAVING variable " + name + " = " + it);
    }
    
    
    // set the new value, making a deep copy
    _variables.put(name, value.Clone());
  }
  public SB_Variable GetVariable(String name) throws SB_Exception {
    SB_Variable it = (SB_Variable)_variables.get(name);
    if (it == null)
      throw new SB_Exception("Variable " + name + " does not exist.");
    return it;
  }
  public SB_Variable GetVariableOrNull(String name){
    return (SB_Variable)_variables.get(name);
  }

  public void SaveVariables(boolean deepSave ){
    _bSaving = true;

    if (!deepSave)
    {
      // always want to start from scratch when doing shallow
      // (ie single-edge evaluation) variable saves
      ClearVarMap(_savedVars);
    }
  }

  public void RestoreVariables(boolean deepRestore,boolean isGlobal,SB_ID entityID,int frameDepth, SB_Debugger debugger) throws SB_Exception {
    _bSaving = false;

    HashMap restoreVars = deepRestore ? _deepSavedVars : _savedVars;

    // copy the saved vars back to the regular var collection
    Iterator it = restoreVars.keySet().iterator();
    while(it.hasNext())
    {
      String key = (String)it.next();
      SB_Variable savedIt = (SB_Variable)restoreVars.get(key);
      SB_Variable foundedIt = (SB_Variable)_variables.get(key);
      if(foundedIt == null)
        throw new SB_Exception("Error while restoring variable " + key + ".");

	  if( SIM_Constants.AI_DEBUGGER )
	  {
		DMFieldMap fields = new DMFieldMap();
		fields.ADD_ID_FIELD("entity",  entityID._id );
		fields.ADD_INT_FIELD( "frame", frameDepth );
		fields.ADD_STR_FIELD( "varName", key );
		fields.ADD_PARAM_FIELD( "value", SB_SimInterface.ConvertVariable(savedIt) );
		if (isGlobal)
			debugger.RecordEvent(EEventType.kEVT_GLOBAL_CHANGED, fields);
		else
			debugger.RecordEvent(EEventType.kEVT_VAR_CHANGED, fields);
	  }

     _variables.put(key, savedIt);
    }
    restoreVars.clear();
  }
  public String toString(){
    String result;
    result = new String("VariableMap (" + Integer.toString(_variables.size()) + " vars)");
    Iterator it = _variables.keySet().iterator();
    while (it.hasNext()) {
      result = result + new String("\tVar " + (String) it.next() + " = " +
                  ((SB_Variable) _variables.get(it)).toString());
    }
    return result;
  }
  public void ClearSavedVariables(SB_Logger logger){
     // make sure that saving is turned off at the end of each tick
     _bSaving = false;

     if ((_savedVars.size() + _deepSavedVars.size()) > 0)
     {
       if(SIM_Constants.DEBUG_INFO_ON)
         logger.log("CLEARING unused saved variables");
       ClearVarMap(_savedVars);
       ClearVarMap(_deepSavedVars);
     }
   }
   public void ClearVarMap(HashMap varmap){
     varmap.clear();
   }

}