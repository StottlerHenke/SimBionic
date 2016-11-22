package com.stottlerhenke.simbionic.api;

import java.io.Serializable;

import com.stottlerhenke.simbionic.common.EErrCode;


/**
 * Base SimBionic exception class.
 */
public class SB_Exception extends Exception implements Serializable
{
  protected EErrCode _code;

  public SB_Exception() {
  }

  public SB_Exception(String msg)
	{
  	super(msg);
    _code = EErrCode.kE_ERROR;
  }

  public SB_Exception(String msg, EErrCode code)
	{
  	super(msg);
    _code =code;
  }

  public SB_Exception(String msg, Throwable cause)
  {
      super(msg, cause);
  }
  
  /**
   * @return the error code for this exception
   */
  public EErrCode GetErrorCode() { return _code; }

}