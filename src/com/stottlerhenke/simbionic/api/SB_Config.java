package com.stottlerhenke.simbionic.api;
import java.net.URL;

import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.Version;
/**
 * Description: This class encapsulates the configuration parameters passed
 * from the simulation to the SimBionic engine.
 */

public class SB_Config
{
  /**
   * Whether Interactive Debugging (ID) mode is active.
   * When active, the engine will block while waiting for a
   * connection from the debug client.
   */
  public boolean debugEnabled;

  /**
   * The maximum number of ticks that can pass between updates
   * of a single entity [0..max_int]
   */
  public int maxUpdatePeriod;

  /**
   * URL of the SimBionic behavior specification file
   */
  public URL fileURL;

  /**
   * Timeout for Interactive Debugging client connections (in seconds).
   */
  public int debugConnectTimeout;

  /**
   * Retained for code backwards-compatibility.  Non-functional.
   * @deprecated
   */
  public String behaviorFilename;
  
  /**
   * the base URL to resolve the relative URLs against, or null
   */
  private URL _baseURL;
  

  /**
   * Assures reasonable default values.
   */
  public SB_Config() {
    behaviorFilename = "";
    maxUpdatePeriod = 0;
    fileURL = null;
    debugConnectTimeout = 30;
  }
  

   public SB_Config(SB_Config other, SB_Logger logger) {
    behaviorFilename = new String( other.behaviorFilename );
    maxUpdatePeriod = other.maxUpdatePeriod;
    try
    {
      fileURL = new URL ( other.fileURL.toString() );
    }
    catch(java.net.MalformedURLException ex)
    {
      logger.log( "Error in creating one configuration setting from another: " + ex.toString(), SB_Logger.ERROR );
    }

    debugConnectTimeout = other.debugConnectTimeout;
    debugEnabled = other.debugEnabled;
  }
   
   public void setBaseURL(URL baseURL) {
      _baseURL = baseURL;
   }
   
   /**
    * @return the base URL to resolve the relative URLs against, or null
    */
   public URL getBaseURL() {
      return _baseURL;
   }
   

  /**
   * Used for auto-compatibility checking: DO NOT ALTER!
   */
  public static final String getVersion(){ return Version.SIMBIONIC_INTERFACE_VERSION;}
  
  /**
   * The false is default, where arrays contain arrays of objects.
   * Otherwise, all arrays contain SB_Param objects.
   */
  public static boolean USE_SB_PARAM_ARRAYS = false;

}