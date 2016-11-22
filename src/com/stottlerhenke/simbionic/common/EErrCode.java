package com.stottlerhenke.simbionic.common;

public class EErrCode extends Enum{

  private EErrCode(int err){ super(err); }

  public static final EErrCode kE_OK = new EErrCode(0);
  public static final EErrCode kE_ERROR = new EErrCode(1);	// generic error
  public static final EErrCode kE_FILE = new EErrCode(2);	// file could not be opened
  public static final EErrCode kE_FORMAT = new EErrCode(3);	// file had invalid format
}