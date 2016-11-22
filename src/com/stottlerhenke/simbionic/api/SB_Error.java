package com.stottlerhenke.simbionic.api;

import com.stottlerhenke.simbionic.common.Enum;

public class SB_Error extends Enum {
  private SB_Error(int value) {
    super(value);
  }
  /**
   * success
   */
  public static final SB_Error kOK = new SB_Error(0);

  /**
   * general failure
   */
  public static final SB_Error kFailure = new SB_Error(1);

  /**
   * file could not be opened
   */
  public static final SB_Error kFile = new SB_Error(2);

  /**
   * file has incorrect format
   */
  public static final SB_Error kFormat = new SB_Error(3);

  /**
   * incompatible version of SB_Config or SB_Interface
   */
  public static final SB_Error kVersion = new SB_Error(5);

  public static SB_Error getType(int value){
    switch(value){
      case 0: return kOK;
      case 1: return kFailure;
      case 2: return kFile;
      case 3: return kFormat;
      case 5: return kVersion;
      default: return null;
    }
  }
}