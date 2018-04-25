package com.stottlerhenke.simbionic.test.constants;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Config;
import com.stottlerhenke.simbionic.api.SB_Engine;
import com.stottlerhenke.simbionic.api.SB_Error;
import com.stottlerhenke.simbionic.api.SB_Param;

import junit.framework.TestCase;

public class ConstantsTest extends TestCase {
	
	public static Float addNumbersJavaThroughJs(Float a, Float b) {
		return a + b;
	}
	
	public static Float addNumbersDirectlyThroughJava(Float a, Float b) {
		return a + b;
	}
	
	public void testConstants()
	{
		// create the object that holds configuration parameters for the engine
	    SB_Config myConfig = new SB_Config();
	    
	    myConfig.debugConnectTimeout = 90;
	    myConfig.debugEnabled = false;

	    // specify the name of the SIM file to load
	    try
	    {
	       String filename = "samples/Constants/Constants.sbj";
	       myConfig.fileURL = new URL("file", "localhost", filename);
	    }
	    catch(Exception e)
	    {
	      fail("Exception creating URL from the sim file name.");
	      return;
	    }

	    // creates an instance of the engine interface object
	    SB_Engine engine = new SB_Engine();
	  
	   // specify the log file
	    PrintStream logPrintStream = null;
	    try
	    {
	      logPrintStream = new PrintStream( new FileOutputStream("samples/Constants/Constants.log") );
	    }
	    catch(Exception ex)
	    {
	      fail(ex.getMessage());
	      return;
	    }

	    if( logPrintStream != null )
	    {
	      engine.registerLogPrintStream( logPrintStream );
	    }

	    // initialize the engine
	    if (engine.initialize(myConfig) != SB_Error.kOK)
	    {
	      fail("Engine failed to initialize: " + engine.getLastError());
	      return;
	    }

	    // create an entity
	    long friendlyGuy = engine.makeEntity( "Sample Entity" );
	    if (friendlyGuy == -1 )
	    {
	      fail("Entity creation error: " + engine.getLastError());
	      return;
	    }

	    // set Friendly Guy's initial behavior
	    String behavior =  "Main";
	    ArrayList<SB_Param> params = new ArrayList<SB_Param>();
	    params.add(new SB_Param(new Integer(52)));
	    SB_Error errCode = engine.setBehavior( friendlyGuy, behavior, params);
	    if (errCode != SB_Error.kOK)
	    {
	    	System.out.println("Error setting behavior: " + engine.getLastError());
	    	return;
	    }    
    

	    // update the entity several times
	    for (int tick=0; tick < 30; tick++)
	    {
	      if (engine.update() != SB_Error.kOK)
	      {
	        fail("Update error: " + engine.getLastError());
	        return;
	      }
	    }

	    engine.terminate();     // shut down the engine
	}
	
	
}
