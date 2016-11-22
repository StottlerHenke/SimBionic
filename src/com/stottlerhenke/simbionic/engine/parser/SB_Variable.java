package com.stottlerhenke.simbionic.engine.parser;
import java.io.Serializable;

import com.stottlerhenke.simbionic.api.SB_Exception;

/**
 * Base class for variables of all types.
 * 
 */
public abstract class SB_Variable implements Serializable  
{
  // The value of this variable. 
  protected Object _value;
  
  // The type of this variable.
  protected String _type;
  
  public SB_Variable() {
  }


  public SB_Variable Clone() throws SB_Exception
  {
        return null;
  }
  
  public Object getValue() {
     return _value;
  }
  
  public void setValue(Object newValue){
     _value = newValue;
  }
  
  public String getType() {
     if (_type == null && _value != null) {
        _type = _value.getClass().getName();
     }
     return _type;
  }
  
  public void setType(String type) {
     _type = type;
  } 

  public String toString(){
      if (_value == null) {
         return "";
      }
      return _value.toString();
  }


  public boolean bool() throws SB_Exception
	{
  	return false;
  }

  public SB_Variable opSUB(SB_Variable arg2) throws SB_Exception
	{
  	return null;
  }

  public SB_Variable opNOT() throws SB_Exception
	{
  	return null;
  }

  public SB_Variable opNEG() throws SB_Exception
	{
  	return null;
  }

  public SB_Variable opADD( SB_Variable arg2) throws SB_Exception
	{
  	return null;
  }

  public SB_Variable opMUL( SB_Variable arg2) throws SB_Exception
	{
  	return null;
  }

  public SB_Variable opDIV( SB_Variable arg2) throws SB_Exception
	{
  	return null;
  }

  public SB_Variable opEXP( SB_Variable arg2) throws SB_Exception
	{
  	return null;
  }

  public SB_Variable opMOD( SB_Variable arg2) throws SB_Exception
	{
  	return null;
  }

  public SB_Variable opLSS( SB_Variable arg2) throws SB_Exception  
	{
    return null;
  }

  public SB_Variable opGRT( SB_Variable arg2) throws SB_Exception  
	{
    return null;
  }

  public SB_Variable equals( SB_Variable arg2) throws SB_Exception  
	{
    return null;
  }

  public SB_Variable opNOTEq( SB_Variable arg2) throws SB_Exception  
	{
    return null;
  }

  public SB_Variable opLSSEq( SB_Variable arg2) throws SB_Exception  
	{
    return null;
  }

  public SB_Variable opGRTEq( SB_Variable arg2) throws SB_Exception  
	{
    return null;
  }

  public SB_Variable opAND( SB_Variable arg2) throws SB_Exception  
	{
    return null;
  }

  public SB_Variable opOr( SB_Variable arg2) throws SB_Exception  
	{
    return null;
  }

  public SB_Variable opAssign( SB_Variable var) throws SB_Exception 
	{
    return null;
  }

}