package com.stottlerhenke.simbionic.editor.parser;

public class SB_ErrorNode extends SB_ParseNode {
  private String _msg;
  private String _badName;

  public SB_ErrorNode(String msg, String badName) {
    _msg = msg;
    _badName = badName;
  }
  public SB_ErrorNode GetNextError(SB_ErrorNode lastError){
        if (lastError == null)
        {
           // previous error already found, return this one
           return this;
        }
        // otherwise, is this one we're searching for?
        if (lastError == this)
        {
           // yes, indicate that the search for the next error can proceed
           lastError = null;
           return null;
        }

        // nope, we've already seen this error
        return null;
  }

  public String ToString()
  {
        return new String("[ERROR: ") + _msg + "]";
  }

  /**
   * @return true if this is a valid expression
   */
   public boolean IsValid(){ return false; }

   /**
    * @return the error message for this node
    */
   public String GetMsg(){ return _msg; }

   /**
    * @return the invalid name (function or variable) associated with this error
    */
   public String GetInvalidName() { return _badName; }

   /**
    * @return true if the parse tree rooted at this node references the given variable,
    *	false otherwise
    */
   public boolean ContainsVariable( String varName) { return false; }

   /**
    * @return true if the parse tree rooted at this node references the given function,
    *	false otherwise
    */
   public boolean ContainsFunction(String funcName) { return false; }

}