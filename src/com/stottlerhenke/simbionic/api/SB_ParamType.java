package com.stottlerhenke.simbionic.api;

import com.stottlerhenke.simbionic.common.Enum;

/**
 * All possible parameter types.
 */

public class SB_ParamType extends Enum {
  private SB_ParamType(int value) {
    super(value);
  }
  
  // Note: should keep only kSB_Invalid and kSB_Data types
  public static final SB_ParamType kSB_Invalid= new SB_ParamType(-1);
 // public static final SB_ParamType kSB_Float = new SB_ParamType(0);
 // public static final SB_ParamType kSB_String = new SB_ParamType(1);
 // public static final SB_ParamType kSB_Entity = new SB_ParamType(2);
 // public static final SB_ParamType kSB_Vector = new SB_ParamType(3);
  public static final SB_ParamType kSB_Data = new SB_ParamType(4);
 // public static final SB_ParamType kSB_StrConst = new SB_ParamType(5);
 // public static final SB_ParamType kSB_Integer = new SB_ParamType(6);
 // public static final SB_ParamType kSB_Boolean = new SB_ParamType(7);
 // public static final SB_ParamType kSB_Array = new SB_ParamType(8);

}