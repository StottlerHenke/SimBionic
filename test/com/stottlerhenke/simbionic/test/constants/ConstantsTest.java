package com.stottlerhenke.simbionic.test.constants;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import com.stottlerhenke.simbionic.api.SB_Config;
import com.stottlerhenke.simbionic.api.SB_Engine;
import com.stottlerhenke.simbionic.api.SB_Error;
import com.stottlerhenke.simbionic.api.SB_Param;

import junit.framework.TestCase;

public class ConstantsTest extends TestCase {

	public static Map<Object, Object> map;

	public static Float addNumbersJavaThroughJs(Float a, Float b) {
		return a + b;
	}

	public static Float addNumbersDirectlyThroughJava(Float a, Float b) {
		return a + b;
	}

	@Before
	protected void setUp() {
		map = new HashMap<>();
	}
	
	public static final String StringConstant 	= "StringConstant";
	public static final String TrueConstant	 	= "TrueConstant";
	public static final String FalseConstant 	= "FalseConstant";
	public static final String IntegerConstant 	= "IntegerConstant";
	public static final String FloatConstant 	= "FloatConstant";
	public static final String ListConstant 	= "ListConstant";
	public static final String ObjectString 	= "ObjectString";
	public static final String ObjectInteger 	= "ObjectInteger";

	private void runSimbionic(String behavior) {
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

	@Test
	public void testConstants()
	{
		runSimbionic("ConstantsCheck");
		
		assertEquals("ABC", 		map.get(StringConstant));
		assertEquals(true, 			map.get(TrueConstant));
		assertEquals(false, 		map.get(FalseConstant));
		assertEquals(5,				map.get(IntegerConstant));
		assertEquals(3.5f,			(Double)map.get(FloatConstant),0.00001);
		
		assertTrue(map.get(ListConstant) instanceof ArrayList);
		ArrayList<Object> list = new ArrayList<>(Arrays.asList(1, 2, 3));
		assertEquals(list,			map.get(ListConstant));
		
		assertTrue(map.get(ObjectString) instanceof String);
		assertEquals("Hello World",	map.get(ObjectString));
		
		assertTrue(map.get(ObjectInteger) instanceof Integer);
		assertEquals(15,			map.get(ObjectInteger));
		
		
	}
	
	
	private static final String SUM_SB 				= "Add using SB";
	private static final String SUM_JAVASCRIPT 		= "Add using JS";
	private static final String SUM_JAVA 			= "Add using Java";
	private static final String SUM_JAVA_THROUGH_JS = "Add using Java via JS";
	
	@Test
	public void testArithmeticOperations() {
		runSimbionic("Arithmetic");
		
		assertEquals(8.5f,		(Double)map.get(SUM_SB),0.00001);
		assertEquals(8.5f,		(Double)map.get(SUM_JAVASCRIPT),0.00001);
		assertEquals(8.5f,		(Double)map.get(SUM_JAVA),0.000001);
		assertEquals(8.5f,		(Double)map.get(SUM_JAVA_THROUGH_JS),0.00000001);
		
//		for (Entry<Object, Object> entry : map.entrySet()) {
//			System.out.println(entry.getKey() + " -> " + entry.getValue());
//		}
	}


}
