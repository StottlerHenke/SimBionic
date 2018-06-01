package com.stottlerhenke.simbionic.common;

/**
 * This class holds the current SimBionic JavaScript version information.
 * 
 * WARNING: Do not edit Version.java directly. Change the version by editing build/version.properties
 *          and change the format of this file by editing Version.java.template in the same package
 */
public class Version
{
	public final static String SIMBIONIC_VERSION;

	static {
		SIMBIONIC_VERSION = "3.0.15"; 
	}

	public final static String SIMBIONIC_BUILD_DATE;
	
	static {
		SIMBIONIC_BUILD_DATE = "2018-06-01";
	}
	
	
	/**
	 * The interface version is stored in the SB_Config. 
	 * The engine, or debugging, will only work if this interface version matches.
	 * Under normal circumstances, this should not change.
	 */
	public final static String SIMBIONIC_INTERFACE_VERSION;
	static {
		SIMBIONIC_INTERFACE_VERSION = "3.0"; 
	}


	public final static String COPYRIGHT = "Copyright 2005-2018 Stottler Henke Associates, Inc.";
	public static final String APPLICATION_NAME = "SimBionic JavaScript";
	public final static String SYSTEM_NAME = "SimBionic Runtime Engine";
	
	public final static int FILE_FORMAT_MIN_VERSION = 1;
	public final static int FILE_FORMAT_MAX_VERSION = 1;
	public final static int FILE_VERSION = 1;
	
	 public final static String SYSTEM_INFO = (SIM_Constants.AI_DEBUGGER ? "(Development)" : "(Release)") + " v" + SIMBIONIC_VERSION + " build " + SIMBIONIC_BUILD_DATE;

}
