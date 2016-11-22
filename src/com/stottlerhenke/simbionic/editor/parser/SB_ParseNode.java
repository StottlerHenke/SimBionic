package com.stottlerhenke.simbionic.editor.parser;

import com.stottlerhenke.simbionic.common.parser.SB_ExpressionNode;

public abstract class SB_ParseNode extends SB_ExpressionNode {
  //protected String _type;
  public SB_ParseNode() {}
  /*public SB_ParseNode(String type){
    _type = type;
  }*/
  public abstract SB_ErrorNode GetNextError(SB_ErrorNode lastError);
  public abstract boolean ContainsVariable( String varName);
  public abstract boolean ContainsFunction( String funcName);
  public abstract String ToString();
 // public String GetType(){
 //   return _type;
 // }
}