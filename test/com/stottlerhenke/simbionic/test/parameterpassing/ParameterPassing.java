package com.stottlerhenke.simbionic.test.parameterpassing;

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
 * A test of parameter passing in SimBionic
 * 
 */
public class ParameterPassing extends TestCase
{
	protected SB_Engine engine;
	
	@Override 
	protected void tearDown() throws Exception {
		engine.terminate();
	}
	
	@Override
	protected void setUp() throws Exception {
		// must reset the singleton instance for each test.
		ActionPredicateAPI.resetInstance();

		TestEngine.buffer.delete(0, TestEngine.buffer.length());
		
		//Create a new engine
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
	    engine = new SB_Engine();

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
	}

	protected Long createAgent(String name, String behavior, Object param) {
	    
		long agentOne = engine.makeEntity( name );
	    if (agentOne == -1 )
	    {
	      fail("Entity creation error: " + engine.getLastError());
	      return null;
	    }

	    ArrayList<SB_Param> params = new ArrayList<SB_Param>();
	    params.add(new SB_Param(param));

	    SB_Error errCode = engine.setBehavior( agentOne, behavior, params);
	    if (errCode != SB_Error.kOK)
	    {
	    	fail("Error setting behavior: " + engine.getLastError());
	    	return null;
	    }
	    
	    return agentOne;
	}

	public void testBasicParameters()
	{
	    // create an entity
	    long friendlyGuy = createAgent( "Friendly Guy", "Sub", new MyModel());

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
	}
	
	/**
	 * Test the evaluation of local variables to see if assignment in JS expressions is properly
	 * handled.
	 * 
	 */
	public void testLocalEvaluation()
	{

	    long agentOne = createAgent( "Agent1", "LocalEvaluationSB", new Integer(1));
	    long agentTwo = createAgent( "Agent2", "LocalEvaluationSB", new Integer(2));
	    engine.setUpdatePriority(100, agentTwo);
	    
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
	    assertTrue(TestEngine.buffer.toString().equals("1212"));
	}
	
	/**
	 * Test the evaluation of global variables to see if assignment in JS expressions is properly
	 * handled.
	 */
	public void testGlobalEvaluation()
	{

	    long agentOne = createAgent( "Agent1", "GlobalEvaluationSB", new Integer(1));
	    long agentTwo = createAgent( "Agent2", "GlobalEvaluationSB", new Integer(2));
	    engine.setUpdatePriority(100, agentTwo);
	    
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
	    assertTrue(TestEngine.buffer.toString().equals("112222"));
	    
	    TestEngine.buffer.delete(0, TestEngine.buffer.length());
	    ArrayList<SB_Param> list = new ArrayList();
	    list.add( new SB_Param( new Integer(1)));
	    engine.setBehavior(agentOne, "GlobalEvaluationSB", list);
	    
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
	    assertTrue(TestEngine.buffer.toString().equals("233"));
	    
	}
	
	/**
	 * Ensure local strings are handles similar to local integers
	 */
	public void testStringEvaluation()
	{

	    long agentOne = createAgent( "Agent1", "StringEvaluationSB", new String("1"));
	    long agentTwo = createAgent( "Agent2", "StringEvaluationSB", new String("2"));
	    engine.setUpdatePriority(100, agentTwo);
	    
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
	    assertTrue(TestEngine.buffer.toString().equals("1219929912"));
	}
	
	/**
	 * Ensure local classes are handles similar to local integers
	 */
	public void testClassEvaluation()
	{

	    long agentOne = createAgent( "Agent1", "ClassEvaluationSB", new Integer(1));
	    long agentTwo = createAgent( "Agent2", "ClassEvaluationSB", new Integer(2));
	    engine.setUpdatePriority(100, agentTwo);
	    
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
	    assertTrue(TestEngine.buffer.toString().equals("121001011992100"));
	}
}