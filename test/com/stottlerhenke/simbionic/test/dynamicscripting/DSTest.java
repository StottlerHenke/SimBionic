package com.stottlerhenke.simbionic.test.dynamicscripting;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.stottlerhenke.dynamicscripting.ChoicePoint;
import com.stottlerhenke.dynamicscripting.DsAction;
import com.stottlerhenke.dynamicscripting.DynamicScriptingWrapper;
import com.stottlerhenke.simbionic.api.SB_Config;
import com.stottlerhenke.simbionic.api.SB_Engine;
import com.stottlerhenke.simbionic.api.SB_Error;
import com.stottlerhenke.simbionic.api.SB_Param;

import junit.framework.TestCase;

public class DSTest extends TestCase
{
	public final static String EXAMPLE_CHOICE_POINT = "ExampleChoicePoint";
	
	protected static DSTestInterface testInterface;
	protected static SB_Engine engine;
	
	/**
	 * Setup dynamic scripting and initialize the SB engine
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{	
		createChoicePoints();
		initializeSBEngine();
	}
	
	/**
	 * Create SB choice points and set their values
	 *
	 */
	protected void createChoicePoints()
	{
		ArrayList<DsAction> actions = new ArrayList<DsAction>();

		//Add actions corresponding to the SB behavior
		for(DSTestActionType action : DSTestActionType.values())
		{
			if(action.equals(DSTestActionType.TWO)) //Lower value = higher priority
				actions.add(new DsAction(action.toString(), 100, 1, action.ordinal() + 1));
			else
				actions.add(new DsAction(action.toString(), 100, 3, action.ordinal() + 1));
		}
		
		DynamicScriptingWrapper.getInstance().addChoicePoint(EXAMPLE_CHOICE_POINT, 
				actions, 3, 0, 200);
		
		//Save and load the choice point to test this
		try {
			DynamicScriptingWrapper.getInstance().saveChoicePoint(EXAMPLE_CHOICE_POINT, (EXAMPLE_CHOICE_POINT + ".txt"));
			ChoicePoint loaded = DynamicScriptingWrapper.getInstance().loadChoicePoint(EXAMPLE_CHOICE_POINT + ".txt");
			System.out.println(loaded.toString());
			DynamicScriptingWrapper.getInstance().addChoicePoint(loaded);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		testInterface = new DSTestInterface(); 
	}
	
	/**
	 * Initialize the SB Engine, called by setUpg
	 *
	 */
	protected void initializeSBEngine()
	{
		// create the object that holds configuration parameters for the engine
		SB_Config myConfig = new SB_Config();
		
		myConfig.debugConnectTimeout = 90;
		myConfig.debugEnabled = false;
		

	    // specify the name of the SIM file to load
	    try
	    {
	       String filename = "samples/DynamicScripting/DynamicScripting.sbj";
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
	      logPrintStream = new PrintStream( new FileOutputStream("samples/DynamicScripting/DynamicScriptingTest.log") );
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
	
	/**
	 * Shut down the SB engine
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
		
		engine.terminate();     // shut down the engine
	}

	/**
	 * Create a simbionic entity with the given behavior
	 * 
	 * @param entityName
	 * @param behavior
	 * @return
	 * @throws Exception
	 */
	protected long createSBEntity(String entityName, String behavior) throws Exception
	{
		// create an entity
		long entity = engine.makeEntity( entityName );
		if (entity == -1 )
		{
			throw new Exception("Entity creation error: " + engine.getLastError());
		}
		
		// set Friendly Guy's initial behavior
		ArrayList params = new ArrayList();
		params.add(new SB_Param(testInterface));
		
		SB_Error errCode = engine.setBehavior( entity, behavior, params);
		if (errCode != SB_Error.kOK)
		{
			throw new Exception("Error setting behavior: " + engine.getLastError());
		}	
		
		return entity;
	}
	
	
	/**
	 * Test selecting an action using a choice node. Action 3 will never be
	 * taken since the condition is always false. 
	 * 
	 * Otherwise, whichever action is first in the script will
	 * be taken repeatedly. If any script contains 2, it will be 
	 * taken more often (higher priority).
	 *
	 */
	public void testActionSelection()
	{
		System.out.println("### Action Selection Test ###");
		System.out.println("Whichever action is first in the script will be taken repeatedly. If any script contains 2, it will be taken more often (higher priority).");

		try
		{	
			// create an entity
			createSBEntity("Friendly Guy" , "Main");
			
			// update the entity several times
			for (int tick=0; tick < 1000; tick++)
			{
				if (engine.update() != SB_Error.kOK)
				{
					throw new Exception("Update error: " + engine.getLastError());
				}				
			}
			
			//Count the actions
			Map<DSTestActionType, Integer> countMap = testInterface.getCountMap();
			
			System.out.print("testActionSelection: ");
			for(DSTestActionType type : DSTestActionType.values()) {
				System.out.print(type.toString() + ", " + countMap.get(type) + ": ");
			}
			System.out.println();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			this.assertTrue(false);
		}
	}
	
	/**
	 * Supply an immediate reward for states 0 and 1 but no reward
	 * for 2 and 3. This should cause the value associated with 0 and 1
	 * to increase and all other values decrease, but you may get different results
	 * based on the use of scripts. That is, scripts with 0 and 3 or 
	 * 1 and 3 may be on top.
	 *
	 */
	public void testImmediateReward()
	{
		System.out.println("### Immediate Reward Test ###");
		System.out.println("The values associated with 0 and 1 should increase -or- scripts with 0 and 2 or 1 and 3 might cause that pair to increase.");
		try
		{
			// create an entity
			createSBEntity("Friendly Guy" , "ImmediateLearning");
			
			// update the entity several times
			for (int tick=0; tick < 1000; tick++)
			{
				if (engine.update() != SB_Error.kOK)
				{
					throw new Exception("Update error: " + engine.getLastError());
				}
			}
			
			//Count the actions
			Map<DSTestActionType, Integer> countMap = testInterface.getCountMap();
			System.out.print("testImmediateReward action selections: ");
			for(DSTestActionType type : DSTestActionType.values()) {
				System.out.print(type.toString() + ", " + countMap.get(type) + ": ");
			}
			System.out.println();
						
			System.out.print("testImmediateReward new values: ");
			ArrayList<DsAction> state = DynamicScriptingWrapper.getInstance().getChoicePoint(EXAMPLE_CHOICE_POINT).getActions();
			System.out.println(state);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			this.assertTrue(false);
		}
		System.out.println();
	}
	
	/**
	 * See immediate reward
	 *
	 */
	public void testEpisodicReward()
	{
		System.out.println("### Episodic Reward Test ###");
		System.out.println("The values associated with 0 and 1 should increase -or- scripts with 0 and 2 or 1 and 3 might cause that pair to increase.");

		
		try
		{
			// create an entity
			createSBEntity("Friendly Guy" , "EpisodicLearning");
			
			// update the entity several times
			for (int tick=0; tick < 1000; tick++)
			{
				if (engine.update() != SB_Error.kOK)
				{
					throw new Exception("Update error: " + engine.getLastError());
				}
			}
			
			System.out.print("testEpisodicReward action selections: ");
			
			//Count the actions
			Map<DSTestActionType, Integer> countMap = testInterface.getCountMap();
			for(DSTestActionType type : DSTestActionType.values()) {
				System.out.print(type.toString() + ", " + countMap.get(type) + ": ");
			}
			System.out.println();
			
			System.out.print("testEpisodicReward new values: ");
			ArrayList<DsAction> state = DynamicScriptingWrapper.getInstance().getChoicePoint(EXAMPLE_CHOICE_POINT).getActions();
			System.out.println(state);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			this.assertTrue(false);
		}
		
		System.out.println();
	}
	
	
}
