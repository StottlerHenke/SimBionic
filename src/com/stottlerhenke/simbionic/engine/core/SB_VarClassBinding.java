package com.stottlerhenke.simbionic.engine.core;


import java.lang.reflect.Field;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.EErrCode;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.engine.SB_SimInterface;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;

/**
 * @author ludwig
 *
 *  This class binds members of user defined classes to expressions
 *  
 *  Note: currently this class is not used at all.  all variable bindings are SB_VarBinding
 */
public class SB_VarClassBinding extends SB_VarBinding
{
	/**
	 * 
	 */
	public SB_VarClassBinding(int kind)
	{
		//super(kind);
		
	}
	
	public void Apply(SB_Entity p,SB_ExecutionFrame contextFrame, SB_ExecutionFrame newFrame, SB_Logger logger) 
		throws SB_Exception
	{
		// evaluate the right-hand expression in the current execution frame
		SB_Variable classVar = contextFrame.GetVariable(_varName);
		SB_Variable value = evaluateExpression(p, contextFrame, classVar.getType());
		
		if(value == null)
		return;
		
		if(SIM_Constants.DEBUG_INFO_ON)
			logger.log("[" + p.toString() + ",STK " + contextFrame.GetStackDepth()
			+ "] BINDING:\tbinding behavior param " + _varName + "." + _varMember + " <- " + _expressStr
			+ " = " + value.toString(),SB_Logger.BINDING);
		
		SetVariable(newFrame, value);
	}
	
	
	public void Apply(SB_Entity p,SB_ExecutionFrame contextFrame, SB_Logger logger)
		throws SB_Exception
	{
		// evaluate the right-hand expression in the current execution frame
		SB_Variable classVar = contextFrame.GetVariable(_varName);
		SB_Variable value = evaluateExpression(p, contextFrame, classVar.getType());
		
		if( value == null )
			return;
		
		if(SIM_Constants.DEBUG_INFO_ON)
			logger.log("[" + p.toString() + ",STK " + contextFrame.GetStackDepth()
			+ "] BINDING:\t binding behavior var " + _varName + "." + _varMember + " <- " +
			_expressStr + " = " + value.toString(),SB_Logger.BINDING);
		
		SetVariable(contextFrame, value);
	}
	
	/**
	 * Do nothing here since we already edited the object directly in apply
	 */
	public void ReverseApply(SB_Entity p,SB_ExecutionFrame contextFrame, SB_ExecutionFrame sourceFrame, SB_Logger logger) 
		throws SB_Exception
	{
	}
	
	/**
	 * Do nothing here since we already edited the object directly in apply
	 */
	public void ReverseApply(SB_Entity p,SB_ExecutionFrame contextFrame, SB_Variable param, SB_Logger logger)
	{
	}
  
	/**
	 * Get the class variable (_varName) from the variableMap and then set the 
	 * value of the field (_varMember) to be an object created from an SB_Variable (var).
	 * 
	 * @param var
	 */
	protected void SetVariable(SB_ExecutionFrame contextFrame, SB_Variable var)
	  throws SB_Exception
	{
		SB_Variable classVar = contextFrame.GetVariable(_varName);
				
		if( classVar != null)
		{
		   Object obj = classVar.getValue();
			Class cls = obj.getClass();
			
			if( !SetClassMemberField(cls, obj, _varMember, SB_SimInterface.ConvertObject(var)))
			{
			  throw new SB_Exception("Can't find " + _varName +"." + _varMember);
			}
		}
	}
	
  /**
   * @return false if a public field of the same name does not exist, true otherwise
   */
  protected boolean SetClassMemberField(Class cls, Object obj, String fieldName, Object value)
  	throws SB_Exception
  {
  	boolean result = false;
  	
  	try
  	{
  	  Field f = cls.getDeclaredField(fieldName);   
  	  f.set(obj, value);
   	  result = true;
		}
  	catch(NoSuchFieldException ex)
  	{
  	  return result;
  	}
   	catch(Exception ex)
		{
   		throw new SB_Exception("Failed to bind class member field ( "
   		    + cls.getName() + "." + fieldName + " ); "
   		    + ex.toString(), EErrCode.kE_ERROR);
		}
   	
  	return result;
  }
  
  protected String _varMember;
}
