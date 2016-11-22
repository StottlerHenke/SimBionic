package com.stottlerhenke.simbionic.common;

import com.stottlerhenke.simbionic.api.SB_Exception;


public class SB_FileException extends SB_Exception {
  public SB_FileException() {
  }
  public SB_FileException(String msg, EErrCode code){
    super(msg, code);
  }
  public SB_FileException(String msg){
    super(msg);
  }
}