package com.stottlerhenke.simbionic.engine.core;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Binding;
import com.stottlerhenke.simbionic.engine.SB_JavaScriptEngine;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.parser.SB_VarClass;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;

/**
 * Represents a single variable binding, complete with the capability
 * to evaluate itself.
 */
public class SB_VarBinding  
{
  @Override
	public String toString() {
		return "SB_VarBinding [_varName=" + _varName + ", _expressStr=" + _expressStr + ", _bVarBinding=" + _bVarBinding
				+ "]";
	}


// variable name
  protected String		_varName;
  // right-hand expression
  protected String		_expressStr;
  // cached information for efficiency
  protected boolean		_bVarBinding;

  public SB_VarBinding() 
  {
     // always variable binding instead of SB_VarClassBinding.
     // we don't save _varMember for SB_VarClassBinding in the new project file (JavaScript version)
    _bVarBinding = true;
  }
// Accessors

  /**
   * @return true if this binding binds a variable, false if it binds a parameter
   */
  public boolean IsVarBinding()  { return _bVarBinding; }

  
  /**
   * @return the name of the bound (left-hand) side variable
   */
  public String GetBoundName()  { return _varName; }

  /**
   * Loads a single variable binding from a specification.
   * @param reader the stream reader from which to read it
   */
  public void load(Binding binding,SB_SingletonBook book)
		throws SB_FileException
	{
		// get name of bound variable
		_varName = binding.getVar();

		// get right-hand expression
		_expressStr = binding.getExpr();
	} 

	public void initialize(SB_SingletonBook book)
	{
	  if(SIM_Constants.DEBUG_INFO_ON)
	    book.getLogger().log(".Initializing binding " + _varName + " <- " + _expressStr,SB_Logger.INIT);
	}
	
	/**
	 * Replaces all instances of SB_Function in the expression tree that
	 * match the name of newMethod.
	 * @param newMethod the method with which to replace existing methods
	 */
	public void replaceFunction(SB_Function newMethod)
	{	
	   // TODO: syl - what do we do here? - ignore for now
	   // once you run a simbionic engine, not sure if the function name can be changed.
	    //_expressTree.replaceFunction(newMethod);
	}	


	public void Apply(SB_Entity p,SB_ExecutionFrame contextFrame,
	                                                   SB_ExecutionFrame newFrame,
	                                                   SB_Logger logger) throws SB_Exception
	{
		try
		{
			SB_Variable currentValue = newFrame.GetVariable(_varName);
		    // evaluate the right-hand expression in the current execution frame
		    SB_Variable value = evaluateExpression(p, contextFrame, currentValue.getType());
		    
		    if(value == null)
		    	return;
		
		    if(SIM_Constants.DEBUG_INFO_ON)
		      logger.log("[" + p.toString() + ",STK " + contextFrame.GetStackDepth()
		      			+ "] BINDING:\tbinding behavior param " + _varName + " <- " + _expressStr
		      			+ " = " + value.toString(),SB_Logger.BINDING);
		
		    //Create a new SB_Varaible of the same type as the existing one.
		    SB_Variable newValue = currentValue.Clone();
		    newValue.setValue(value.getValue());
		    
		    // assign it to the behavior parameter in the *new* execution frame
		    newFrame.SetVariable(_varName, newValue);
		}
		catch(SB_Exception e)
		{
			throw new SB_Exception("Failed to apply variable binding '" + _expressStr + "' to variable '" + _varName + "'.",e);
		}
	}


	public void Apply(SB_Entity p,SB_ExecutionFrame contextFrame, SB_Logger logger)
		throws SB_Exception
	{
		try
		{	
			// evaluate the right-hand expression in the current execution frame
			SB_Variable currentValue = contextFrame.GetVariable(_varName);
			SB_Variable value = evaluateExpression(p, contextFrame, currentValue.getType());
			
			if( value == null )
				return;
			
			if(SIM_Constants.DEBUG_INFO_ON)
				logger.log("[" + p.toString() + ",STK " + contextFrame.GetStackDepth()
						+ "] BINDING:\t binding behavior var " + _varName + " <- " +
						_expressStr + " = " + value.toString(),SB_Logger.BINDING);
			
			//  Create a new SB_Varaible of the same type as the existing
			SB_Variable newValue = currentValue.Clone();
			newValue.setValue(value.getValue());
			
			// assign it to the behavior variable
			contextFrame.SetVariable(_varName,newValue);
		}
		catch(SB_Exception e)
		{
			throw new SB_Exception("Failed to apply variable binding '" + _expressStr + "' to variable '" + _varName + "'.",e);
		}
	}

	
	 protected SB_Variable evaluateExpression(SB_Entity entity, SB_ExecutionFrame contextFrame,
			 String expectedType) throws SB_Exception {
		 try {
			 // evaluate the right hand expression in the js engine.
			 String script = _expressStr;
			 SB_JavaScriptEngine javaScriptEngine = contextFrame.GetBook().getJavaScriptEngine();
			 Object evalReturn = javaScriptEngine.evaluate(script, contextFrame);
			 
			 Object returnObj = evalReturn;
			 if (evalReturn != null) {
				// cast if not null
			    returnObj = SB_JavaScriptEngine.castToJavaObject(evalReturn, expectedType);
			 }

			 // create SB_Variable.
			 SB_Variable param = new SB_VarClass();
			 param.setValue(returnObj);
			 return param;
		 } catch (SB_Exception ex) {
			 SB_EntityData state = entity.GetState();
			 state.GetExecStack().HandleException(ex, contextFrame);
			 // return null if any error occurs during evaluation.
			 return null;
		 }
	 }

}