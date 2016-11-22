package com.stottlerhenke.simbionic.common;


public class SB_Util 
{
	/**
	 * Strips Windows- and Linux-style paths from a path string.
	 * @param path the path to be processed
	 * @return the raw filename, stripped of the path
	 */
	public static String stripPath(String path)
	{
    //NOTE: The following is required for the windows version to work
    int slashPos = path.lastIndexOf("\\");
    if(slashPos != path.length())
    {
      path = path.substring(slashPos + 1);
    }
    // also check for linux-style forward slashes
    slashPos = path.lastIndexOf("/");
    if(slashPos != path.length())
    {
      path = path.substring(slashPos + 1);
    }
    return path;
	}
}