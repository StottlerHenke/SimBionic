package com.stottlerhenke.simbionic.engine.parser;

import com.stottlerhenke.simbionic.api.SB_Exception;

/**
 * Represents a single variable of invalid type.
 */
public class SB_VarInvalid extends SB_Variable 
{
  public SB_VarInvalid() {
  }

  public SB_Variable Clone() throws SB_Exception{
    return new SB_VarInvalid(this);
  }

  public SB_VarInvalid(SB_VarInvalid var2){
    Copy(var2);
  }
  protected void Copy( SB_VarInvalid var) 
  {
     _value = var.getValue();
  }

  
    
  protected void Free(){
    // nothing to free
  }

  
  public String toString(){
    return new String("(INVALID_VAR)");
  }
  public SB_Variable opOr( SB_Variable arg2) throws SB_Exception  {
          // result of OR depends entirely on the second argument
          if(arg2 != null) {
             SB_Variable result = new SB_VarClass();
             result.setValue(arg2.bool());
             return result;
             //return new SB_VarBoolean(arg2.bool()); 
          }
          else {
             SB_Variable result = new SB_VarClass();
             result.setValue(new Boolean(false));
             return result;
            //return new SB_VarBoolean(false);
            
          }
  }

  public SB_Variable opAND( SB_Variable arg2) throws SB_Exception  {
     // anything ANDed with an invalid yields false
     SB_Variable result = new SB_VarClass();
     result.setValue(new Boolean(false));
     return result;
     //return new SB_VarBoolean(false);
  }

  public SB_Variable opGRTEq( SB_Variable arg2) throws SB_Exception  {
     // relational operations always yield false, not invalid
     SB_Variable result = new SB_VarClass();
     result.setValue(new Boolean(false));
     return result;
     //return new SB_VarBoolean(false);
  }

  public SB_Variable opLSSEq( SB_Variable arg2) throws SB_Exception  {
     // relational operations always yield false, not invalid
     SB_Variable result = new SB_VarClass();
     result.setValue(new Boolean(false));
     return result;
     //return new SB_VarBoolean(false);
  }

  public SB_Variable opNEQ( SB_Variable arg2)  {
    SB_Variable result = new SB_VarClass();
    if(arg2 instanceof SB_VarInvalid) {
       result.setValue(new Boolean(false));
      //result = new SB_VarBoolean( false );
    }
    else {
      result.setValue(new Boolean(true));
      //result = new SB_VarBoolean( true );
    }
    return result;
  }

  public SB_Variable equals( SB_Variable arg2) throws SB_Exception  {
    SB_Variable result = new SB_VarClass();
    if(arg2 instanceof SB_VarInvalid) {
      result.setValue(new Boolean(true));
      //result = new SB_VarBoolean( true );
    }
    else {
      result.setValue(new Boolean(false)); 
      //result = new SB_VarBoolean( false );
    }
    return result;
  }

  public SB_Variable opGRT( SB_Variable arg2) throws SB_Exception  {
          // relational operations always yield false, not invalid
         // return new SB_VarBoolean(false);
     SB_Variable result = new SB_VarClass();
     result.setValue(new Boolean(false));
     return result;
  }

  public SB_Variable opLSS( SB_Variable arg2) throws SB_Exception  {
          // relational operations always yield false, not invalid
          //return new SB_VarBoolean(false);
     SB_Variable result = new SB_VarClass();
     result.setValue(new Boolean(false));
     return result;
  }

  public SB_Variable opMOD( SB_Variable arg2) throws SB_Exception  {
          return new SB_VarInvalid();
  }

  public SB_Variable opEXP( SB_Variable arg2) throws SB_Exception  {
          return new SB_VarInvalid();
  }

  public SB_Variable opDIV( SB_Variable arg2) throws SB_Exception  {
          return new SB_VarInvalid();
  }

  public SB_Variable opMUL( SB_Variable arg2) throws SB_Exception  {
          return new SB_VarInvalid();
  }

  public SB_Variable opSUB( SB_Variable arg2) throws SB_Exception  {
          return new SB_VarInvalid();
  }

  public SB_Variable opADD( SB_Variable arg2) throws SB_Exception  {
    SB_Variable result = null;
    if (arg2.getValue().getClass() == String.class) {
       result = new SB_VarClass();
       result.setValue( "<INVALID>" + arg2.getValue().toString());
    }
    /*if(arg2.GetType() == SB_VarType.kString)
      result = new SB_VarString( "<INVALID>" + SB_VarString.GET_STRING(arg2) );
      */
    return result;
  }

  public SB_Variable opNOT() throws SB_Exception  {
    //return new SB_VarBoolean(true);
     SB_Variable result = new SB_VarClass();
     result.setValue(new Boolean(true));
     return result;
  }


  public boolean bool() throws SB_Exception {
    return false;
  }
  
  

    /* (non-Javadoc)
     * @see com.stottlerhenke.simbionic.engine.parser.SB_Variable#opAssign(com.stottlerhenke.simbionic.engine.parser.SB_Variable)
     */
    public SB_Variable opAssign(SB_Variable var) throws SB_Exception
    {
        //_parameterValue = var.getSB_Param();
       _value = var.getValue();

        return this;
    }
}