package com.stottlerhenke.simbionic.engine.core;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.engine.parser.SB_VarClass;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;


/**
 * An instance of this class contains a list of parameters for a behavior,
 * action, or predicate.
 */
public class SB_ParamList  
{
  protected ArrayList<SB_Parameter> _params;
  public SB_ParamList() {
    _params = new ArrayList<SB_Parameter>();
  }

  public void add(SB_Parameter param)
  {
    _params.add(param);
  }

  public int GetNumParams() { return _params.size(); }

  public SB_Parameter GetParam(int index) { return  _params.get(index); }


  public void Instantiate(SB_VariableMap vars){
    for (int i = 0; i < _params.size(); i++){
      SB_Parameter parmIt = (SB_Parameter) _params.get(i);
      try{
        SB_VarClass var = new SB_VarClass();
        var.setType(parmIt.GetType());
        vars.AddVariable(parmIt.GetName(), var);
      }catch(SB_Exception e){
    	  e.printStackTrace();
      }
    }
  }
  
  public boolean contains(String paramName){
	  return _params.stream().anyMatch(p -> p._name.equals(paramName));
  }


  /**
   * TODO For TT Bug 926, we need to compare the given args with the expected parameter
   * types to determine if the args are valid.
   * 
   * @param frame
   * @param args
   */
  public void UnpackArgs(SB_ExecutionFrame frame, List<SB_Variable> args){
    SB_Variable argIt;
    for (int j = 0; j < _params.size(); j++) {
      argIt = (SB_Variable) args.get(j);
      frame.SetVariable( ( _params.get(j)).GetName(), argIt);

      if(SIM_Constants.DEBUG_INFO_ON)
        frame.GetLogger().log("[ID*,STK " + frame.GetStackDepth() + "] BINDING:\tsyncing behavior param "
        					+ ((SB_Parameter) _params.get(j)).toString() + " = " + argIt.toString(),SB_Logger.BINDING);
    }
  }

  public SB_Parameter GetParam( String name){
    return _params.stream().filter(p -> p.GetName().equals(name)).findFirst().orElse(null);
  }


  public String toString (){
    String out;
    out = new String( "ParamList (" + Integer.toString(_params.size()) +  " params)" );
    for(int i = 0; i < _params.size(); i++){
      out = out + "/t" + (_params.get(i)).toString();
    }
    return out;
  }

public void print() {
	System.out.println(_params.stream().map(p -> p.toString()).reduce("Parameters: ", (f, s) -> f + "\n\t" + s));
	// TODO Auto-generated method stub
	
}

}