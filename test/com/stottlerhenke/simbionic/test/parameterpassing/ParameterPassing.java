package com.stottlerhenke.simbionic.test.parameterpassing;

/**
 * <p>Title: </p>
 * <p>Description: This is a sample project designed to show you how to build a very simple "Hello World" program using SimBionic.
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Stottler Henke</p>
 * @author Jeremy Ludwig
 * @version 1.0
 */

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Config;
import com.stottlerhenke.simbionic.api.SB_Engine;
import com.stottlerhenke.simbionic.api.SB_Error;
import com.stottlerhenke.simbionic.api.SB_Param;
import com.stottlerhenke.simbionic.engine.ActionPredicateAPI;
import com.stottlerhenke.simbionic.test.engine.TestEngine;

import junit.framework.TestCase;



 /**
 * This class defines a simple interface between SimBionic and
 * our HelloWorld application.
 */
public class ParameterPassing extends TestCase
{
	
	@Override
	protected void setUp() throws Exception {
		// must reset the singleton instance for each test.
		ActionPredicateAPI.resetInstance();

		TestEngine.buffer.delete(0, TestEngine.buffer.length());
	}

	public void testHelloWorld()
	{
		// create the object that holds configuration parameters for the engine
	    SB_Config myConfig = new SB_Config();

	    myConfig.debugConnectTimeout = 90;
	    myConfig.debugEnabled = false;

	    // specify the name of the SIM file to load
	    try
	    {
	       String filename = "samples/ParameterPassing/ParameterPassing.sbj";
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
	      logPrintStream = new PrintStream( new FileOutputStream("samples/ParameterPassing/ParameterPassing.log") );
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
	    long friendlyGuy = engine.makeEntity( "Friendly Guy" );
	    if (friendlyGuy == -1 )
	    {
	      fail("Entity creation error: " + engine.getLastError());
	      return;
	    }
	    
	    System.out.println(TestEngine.buffer.toString());

	    // set Friendly Guy's initial behavior
	    String behavior =  "Sub";
	    ArrayList<SB_Param> params = new ArrayList<SB_Param>();
	    params.add(new SB_Param(new MyModel()));

	    SB_Error errCode = engine.setBehavior( friendlyGuy, behavior, params);
	    if (errCode != SB_Error.kOK)
	    {
	    	System.out.println("Error setting behavior: " + engine.getLastError());
	    	return;
	    }


	    // update the entity several times
	    for (int tick=0; tick < 20; tick++)
	    {
	      if (engine.update() != SB_Error.kOK)
	      {
	        fail("Update error: " + engine.getLastError());
	        return;
	      }
	    }

	    System.out.println(TestEngine.buffer.toString());
	    assertTrue(TestEngine.buffer.toString().equals("FooTWO"));

	    engine.terminate();     // shut down the engine
	}
}