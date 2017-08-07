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
 * Q/A regarding evaluation of assignments in JavaScript expressions:
 * 
 * What does assignment do in JS expressions for global and local variables used elsewhere in the BTNS?

Assignment to local variables within a JS expression does not change the SB variable value outside of that expression.
See: ParameterPassing.sbj  LocalEvaluation SB
Assignment to global variables within a JS expression does not change the SB variable outside of that expression.
See: ParameterPassing.sbj GlobalEvaluationSB

Does type matter when assignments are made in JS expressions, i.e. are the known SB types handled differently than Object or defined classes?
No, all assignments are handled the same.
String version, see: ParameterPassing.sbj StringEvaluationSB
Class version, see ParameterPassing.sbj ClassEvaluationSB

Is the JS engine cleared/updated between evaluations within the same BTN? Across BTNs?
The current implementation has includes a separate ScriptContext, Bindings, and ScriptObjectMirror for each SimBionic entity. Assignment values are cleared after evaluation.
The Bindings are set prior to each evaluation from the values of the SB variables, and are part of the ScriptContext
Each evaluation is run with the ScriptContext
After performing an evaluation, and new assignments are removed from the ScriptObjectMirror.

If a JS expression modifies a Java object, does that modification persist elsewhere in the BTNs?
Yes, see: ParameterPassing.sbj ClassEvaluationSB

What happens if an SB variable name collides with a JS variable (global or local)?
Local variables do not interact. All tests in ParameterPassing.sbj include local name collisions on the variable a.
GLOBAL VARIABLES DO INTERACT.
If there is a global variable ‘b’ in the ParameterPassing.js file it gets replaced with the variable ‘b’ from SB.
Global namespace collision is a known issue in javascript
See: https://stackoverflow.com/questions/2613310/ive-heard-global-variables-are-bad-what-alternative-solution-should-i-use

When a JS expression is evaluated by the JS engine, does the SB variable named ‘a’ shadow the JS variable named ‘a’, 
such that the SB variable named ‘a’ is set/get during the evaluation, rather than the JS variable?
No. Test show that the SB ‘a’ and JS ‘a’ are distinct.  Assignments to the JS ’a’ are not propagated to the SB ‘a’.
When an expression is evaluated, the three below items are done. If the third item is NOT performed, 
then the changes to the JS ‘a’ are kept by the script engine and will be used instead of the values of the SB ‘a’ copied in step 1.
The result is that it appears to the end user that the SB value has been altered even though it has not - the SB value is simply being ignored. 
1. The Bindings are set prior to each evaluation from the values of the SB variables, and are part of the ScriptContext
2. Each evaluation is run with the ScriptContext
3. After performing an evaluation, and new assignments are removed from the ScriptObjectMirror.
However, since we perform all three steps, the end user will see JS ‘a’ and SB ‘a’ as distinct objects, with the SB ‘a’ being used at the start of any expression evaluation.
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

	    //System.out.println(TestEngine.buffer.toString());
	    assertTrue(TestEngine.buffer.toString().equals("112.02.022"));
	    
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
	   
	    //System.out.println(TestEngine.buffer.toString());
	    assertTrue(TestEngine.buffer.toString().equals("23.03"));
	    
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