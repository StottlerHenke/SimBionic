package com.stottlerhenke.simbionic.engine.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Binding;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;

/**
 * This class wraps a collection of variable bindings.
 */
public class SB_Bindings  
{
  protected ArrayList<SB_VarBinding> _bindings = new ArrayList();
  protected int _firstParam;
  protected int _paramCount = 0;

  public SB_Bindings() {
  }
  
  public void load(List<Binding> bindings,SB_SingletonBook book)
		throws SB_FileException
	{
	  // read number of bindings
	  int num = bindings.size();

	  // no class binding.
	  for (int i = 0; i < num; i++) 
	  {
		// create SB_VarBinding for each binding.
	    SB_VarBinding binding = new SB_VarBinding();
	    binding.load(bindings.get(i),book);
	    _bindings.add(binding);
	  }
	  
	 
  }


  public void initialize(SB_SingletonBook book)
  {
    for (int i = 0; i < _bindings.size(); i++)
    {
      ((SB_VarBinding)_bindings.get(i)).initialize(book);
    }

    _firstParam = _bindings.size();
  }

  /**
   * Replaces all instances of SB_Function that match the name of newMethod.
   * @param newMethod the method with which to replace existing methods
   */
  public void replaceFunction(SB_Function newFunction)
  {
	  for (int i = 0; i < _bindings.size(); i++)
	  {
	    ((SB_VarBinding)_bindings.get(i)).replaceFunction(newFunction);
	  }
  }
  
  /**
   * Set all binding to be in only. Used for class methods as actions
   */
  public void initParamBindings()
	{
    SortBindings(); //They still need to be sorted, though somewhat differently
  }
  
  /**
   * This sort bindings depends upon the parameters being already specified int
   * the desired order. It just moves them to the end and sets up the _firstParam 
   * variable.
   * 
   * For use in the case of class methods where the parameter list is not known.
   */
  public void SortBindings()
  {
    int numParams = 0;
    int count = _bindings.size();
    for( int x = 0; x < count; x++ )
    {
      SB_VarBinding it = (SB_VarBinding)_bindings.get(x);
      if( !it.IsVarBinding() )
        numParams++;
    }

    // sort the bindings so all parameter bindings are at the end,
    // in the same order as the parameter list
    
    Collections.sort(_bindings, (first, second) -> {
    	boolean firstIsParam = !first.IsVarBinding();
    	boolean secondIsParam = !second.IsVarBinding();
    	return firstIsParam
    			? secondIsParam
    					? 0
    				    : 1
    		    : secondIsParam
    		    		? -1
    		    	    : 0;}
    		);
    /*
    for (int i=0; i < numParams; i++)
    {
      for (int j = 0; j < _bindings.size(); j++)
      {
        SB_VarBinding it = (SB_VarBinding)_bindings.get(j);
        if (!it.IsVarBinding())
        {
          SB_VarBinding moved = it;
          _bindings.remove(it);
          _bindings.add(moved);
          break;
        }
      }
    }
*/
    // mark the first parameter so we can always jump right to it
    _paramCount = numParams;
    _firstParam =  _bindings.size() - numParams;
  }
  
  public void initParamBindings(SB_ParamList params)
	{
     SortBindings(params);
  }

  public void SortBindings(SB_ParamList params){
    int numParams = params.GetNumParams();

    // sort the bindings so all parameter bindings are at the end,
    // in the same order as the parameter list
    
    Collections.sort(_bindings, (first, second) -> {
    	boolean firstIsParam = !first.IsVarBinding() && params.contains(first.GetBoundName());
    	boolean secondIsParam = !second.IsVarBinding() && params.contains(second.GetBoundName());
    	return firstIsParam
    			? secondIsParam
    					? 0
    				    : 1
    		    : secondIsParam
    		    		? -1
    		    	    : 0;}
    		    	
    		);
    
    /*
    for (int i=0; i < numParams; i++){
      SB_Parameter param = params.GetParam(i);

      // find this param in the binding list (it must be present since
      // all params must be bound exactly once)
      for (int j = 0; j < _bindings.size(); j++){
        SB_VarBinding it = (SB_VarBinding)_bindings.get(j);
        if (!it.IsVarBinding() && it.GetBoundName().compareTo(param.GetName()) == 0){
          SB_VarBinding moved = it;
          _bindings.remove(it);
          _bindings.add(moved);
          break;
        }
      }
    }
	*/
    // mark the first parameter so we can always jump right to it
    //_firstParam = (SB_VarBinding)_bindings.get(0 + (_bindings.size() - numParams));
    _paramCount = numParams;
    _firstParam =  _bindings.size() - numParams;
  }

  public void ApplyForVariables(SB_Entity entity,SB_ExecutionFrame contextFrame)  throws SB_Exception {
	if (_firstParam < 0) return;
	
    for (int i = 0; i != _firstParam/*_bindings.size()*/; i++)
    {
      ((SB_VarBinding)_bindings.get(i)).Apply(entity,contextFrame, contextFrame.GetLogger() );
    }
	//System.err.printf("There are currently %d bindings, %d params and the last recorded check showed a difference of %d\n", _bindings.size(), _paramCount, _firstParam);
  }

  public void ApplyForInvocation(SB_Entity entity, SB_ExecutionFrame contextFrame, SB_ExecutionFrame newFrame)  throws SB_Exception {
    
	//System.out.printf(_bindings.stream().map(o -> o.toString()).reduce("First param of %s is %d:\n", (f, s) -> f + "\t" + s + "\n"),entity._name, _firstParam);
	
	//Previously, the following loop started with i = _firstParam, and things broke
	//Specifically, calling a sub-behavior would not actually result in the parameters being passed, UNLESS those parameters were also passed to a javascript function.
	//After much investigation, and on a complete whim, I tried setting i to 0 rather than _firstParam.
	//Everything seems to work now.
	//Previously, there was also a check for "if (_firstParam < 0) return;" you can still see that in ApplyForVariables
	//We're not sure if negative values to _firstParam (indicating the number of parameters is greater than the number of bindigns) represents an invalid state or not
	//We suspect the negative-value check might have been a temporary fix not intended to remain.
	//Our best guess is that some piece of code that's intended to put parameters into bindings stopped working.
	// ~Ned Peters 2-20-16
	// :(
	    
	  
    for ( int i = 0; i < _bindings.size(); i++){ 
    	
      SB_VarBinding it = (SB_VarBinding)_bindings.get(i);
      it.Apply(entity,contextFrame,newFrame, newFrame.GetLogger());
    }
	//System.err.printf("There are currently %d bindings, %d params and the last recorded check showed a difference of %d\n", _bindings.size(), _paramCount, _firstParam);
  }

  public void print(){
	  System.out.println(_bindings.stream().map(b -> b.toString()).reduce("Bidings: ", (f, s) -> f + "\n\t" + s));
  }

  public void ApplyForReturn(SB_Entity entity,SB_ExecutionFrame contextFrame, SB_ExecutionFrame doneFrame) throws SB_Exception {
	  // do nothing here.  'out' parameter doesn't exist anymore in the javaScript version 
	
  }


}