package com.stottlerhenke.simbionic.test.engine;


import java.util.ArrayList;
import java.util.Vector;

import junit.framework.TestCase;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.api.SB_Param;
import com.stottlerhenke.simbionic.api.SB_ParamType;
import com.stottlerhenke.simbionic.engine.ActionPredicateAPI;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TestEngine extends TestCase
{

  public static StringBuffer buffer = new StringBuffer(10000);

  private TestWrapper _wrapper;


  @Override
protected void setUp() throws Exception {
	  // must reset the singleton instance for each test.
	 ActionPredicateAPI.resetInstance();

     buffer.delete(0, buffer.length());
     String testPath = "./samples/JavaTestEngine";
     String file = "JavaTestEngine.sbj";
     _wrapper = new TestWrapper();

     if (!Initialize(_wrapper, testPath, file)) {
        throw new Exception("Couldn't initialize the engine");
     }
  }

///*
  public void testScheduler()
  {
    _wrapper.logTestStart("Scheduler");
    ArrayList paramsA = new ArrayList();
    paramsA.add(new SB_Param("A"));
    long entityA = _wrapper.CreateEntity("A", "Schedule", paramsA, 1, 3);
    ArrayList paramsB = new ArrayList();
    paramsB.add(new SB_Param("B"));
    long entityB = _wrapper.CreateEntity("B", "Schedule", paramsB, 1, 2);
    ArrayList paramsC = new ArrayList();
    paramsC.add(new SB_Param("C"));
    long entityC = _wrapper.CreateEntity("C", "Schedule", paramsC, 2, 10);
    ArrayList paramsD = new ArrayList();
    paramsD.add(new SB_Param("D"));
    long entityD = _wrapper.CreateEntity("D", "Schedule", paramsD, 5, 1);
    for (int update = 0; update < 5; update++)
    {
       _wrapper.Update();
      buffer.append(":");
    }
    assertEquals("B1A1:B2A2C1:B2A2:B2A2C2:D1B2A2:", buffer.toString());
    _wrapper.DestroyEntity(entityA);
    _wrapper.DestroyEntity(entityB);
    _wrapper.DestroyEntity(entityC);
    _wrapper.DestroyEntity(entityD);
  }



   // Tests the API method DestroyEntity.
  public void testDestroyEntity()
  {
    _wrapper.logTestStart("DestroyEntity");
    long id = _wrapper.CreateEntity("TEST", "DestroyEntityBehavior",
        new ArrayList());
    for (int update = 0; update < 2; update++)
       _wrapper.Update();
    _wrapper.DestroyEntity(id);
    for (int update2 = 0; update2 < 2; update2++)
       _wrapper.Update();
    assertEquals("A1A2", buffer.toString());
    _wrapper.DestroyEntity(id);
  }

  public void testGetEntityGlobalBehavior() {
     _wrapper.logTestStart("GetEntityGlobal");
     long id = _wrapper.CreateEntity("TEST", "GetEntityGlobalBehavior",
         new ArrayList());
     for (int update = 0; update < 5; update++)
        _wrapper.Update();
     SB_Param value = _wrapper.GetEntityGlobal(id, "test_global");
     try {
        assertTrue(value.getInteger() == 45);
     } catch (SB_Exception ex) {
        assertTrue(false);
     }
     _wrapper.DestroyEntity(id);
  }

   // Tests that the termination of the bottom most behavior on the stack doesn't
   // cause a crash, and correctly sets the IsFinished flag
  public void testIsEntityFinishedBehavor()
  {
     _wrapper.logTestStart("IsEntityFinished");
    long id = _wrapper.CreateEntity("TEST", "IsEntityFinishedBehavior",
        new ArrayList());
    for (int update = 0; update < 5; update++)
       _wrapper.Update();
    assertTrue(_wrapper.IsEntityFinished(id));
    _wrapper.DestroyEntity(id);
  }


   // Tests the API method MakeEntity.
  public void testMakeEntity()
  {
    _wrapper.logTestStart("MakeEntity");
    long id = _wrapper.MakeEntity("TEST");
    _wrapper.SetBehavior(id, "MakeEntityBehavior", new ArrayList<SB_Param>());
    for (int update = 0; update < 5; update++)
       _wrapper.Update();
    assertEquals("A1A2A3", buffer.toString());
    _wrapper.DestroyEntity(id);
  }



  // Tests the API method SetBehavior.
  public void testSetBehavior()
  {
     _wrapper.logTestStart("SetBehaviorBehavior");
    long id = _wrapper.CreateEntity("TEST", "SetBehaviorBehavior",
        new ArrayList());
    for (int update = 0; update < 3; update++)
       _wrapper.Update();
    _wrapper.SetBehavior(id, "SetBehaviorBehavior3", new ArrayList<SB_Param>());
    for (int update2 = 0; update2 < 3; update2++)
       _wrapper.Update();

    assertEquals("B1B2B3C1C2", buffer.toString());
    _wrapper.DestroyEntity(id);
  }

  // Tests the API method SetEntityGlobal.
  public void testSetEntityGlobal()
  {
     _wrapper.logTestStart("SetEntityGlobalBehavior");
    long id = _wrapper.CreateEntity("TEST", "SetEntityGlobalBehavior",
        new ArrayList());
    _wrapper.SetEntityGlobal(id, "test_global", new SB_Param(45));
    for (int update = 0; update < 5; update++)
       _wrapper.Update();

    assertEquals("45", buffer.toString());
    _wrapper.DestroyEntity(id);
  }

  public void testBindings() {
     test_SimpleBehavior(_wrapper, "Bindings", 10, "A1A3A4A9");
  }


  public void testClassBinding() {
     test_SimpleBehavior(_wrapper, "ClassBinding", 1, "105");
  }

  public void testClassConstructor() {
     test_SimpleBehavior(_wrapper, "ClassConstructor", 1, "5");
  }

  public void testClassMethod() {
     test_SimpleBehavior(_wrapper, "ClassMethod", 1, "10523undefined");
  }

  public void testClassNesting() {
     test_SimpleBehavior(_wrapper, "ClassNesting", 1, "53");
  }

  public void testClassNull() {
     test_SimpleBehavior(_wrapper, "ClassNull", 1, "null");
  }

  public void testClassStatic() {

	  //Java 1.7 Rhino
     //test_SimpleBehavior(_wrapper, "ClassStatic", 1, "7111262.03612.03910.011");

     //Java 1.8 Nashorn
     test_SimpleBehavior(_wrapper, "ClassStatic", 1, "71112623612.03910.011");

  }

  public void testCompoundBasicTest() {
     test_SimpleBehavior(_wrapper, "CompoundBasicTest", 1, "123");
  }

  public void testCompoundBindingTest() {
     test_SimpleBehavior(_wrapper, "CompoundBindingTest", 1, "onetwothreeend");
  }

  public void testCompoundBindingTest2() {
     test_SimpleBehavior(_wrapper, "CompoundBindingTest2", 4, "012345end");
  }

  public void testCompoundBehaviorTest() {
     test_SimpleBehavior(_wrapper, "CompoundBehaviorTest", 1, "1abc3");
  }

  public void testCompoundEmbeddedTest() {
     test_SimpleBehavior(_wrapper, "CompoundEmbeddedTest", 3, "ab123c");
  }

  public void testCompoundExecutionModeTest() {
     test_SimpleBehavior(_wrapper, "CompoundExecutionModeTest", 6, "def01def2345abc");
  }

  public void testCompoundMultipleTest() {
     test_SimpleBehavior(_wrapper, "CompoundMultipleTest", 1, "012abc345end");
  }



  public void testActionParams() {
	  String expected = "1.5,test,347,[],false,:Any";
	  test_SimpleBehavior(_wrapper, "ActionParams", 10, expected);
  }

  public void testBehaviorParams1() {
	  test_SimpleBehavior(_wrapper, "BehaviorParams1", 10, "in string test:");
  }

  // Tests the passing of initial parameters into behaviors.
  public void testInitialParams()
  {
	  _wrapper.logTestStart("InitialParams");
    ArrayList params = new ArrayList();
    params.add(3.5f);
    params.add("test");
    Vector v = new java.util.Vector();
    v.add(4.0f);
    v.add(5.0f);
    v.add(7.0f);
    params.add(v);
    params.add(new SB_Param((Object) null));
    params.add(new SB_Param(15));
    params.add(new SB_Param(true));
    long id = _wrapper.CreateEntity("TEST", "InitialParams", params);
    for (int update = 0; update < 5; update++)
    	_wrapper.Update();
    assertEquals("3.5,test,[4.0, 5.0, 7.0],null,15,true", buffer.toString());
    _wrapper.DestroyEntity(id);
  }

  public void testPredParams() {
	  String expected = "1.5,test,[5.0, 10.0, 15.0],null,347,false,:";
	  test_SimpleBehavior(_wrapper, "PredParams", 10, expected);
  }

  public void testMultipleInterrupts() {
	  test_SimpleBehavior(_wrapper, "MultipleInterrupts", 25, "A1B1C1C2D1D2E1E2E3D3C3B2A2");
  }

  public void testTestInterrupts() {
	  test_SimpleBehavior(_wrapper, "TestInterrupt", 10, "B1-1C1B1-2B1-3");
  }

  public void testNumTicks() {
	  test_SimpleBehavior(_wrapper, "NumTicks", 4, "A1B1B2A2");
  }

  public void testNumTicksInterrupt() {
	  test_SimpleBehavior(_wrapper, "NumTicksInterrupt", 3, "A1B1C1C2A2");
  }

  public void testSubComplex() {
	  test_SimpleBehavior(_wrapper, "SubComplex", 2, "A1A2B1C1D1C3B2A3");
  }

  public void testSubOneTick() {
	  test_SimpleBehavior(_wrapper, "SubOneTick", 5, "A1A2B1B2A4A5A6A7A8");
  }

  public void testSubMultiTick() {
	  test_SimpleBehavior(_wrapper, "SubMultiTick", 5, "A1A2B1B2A4A5");
  }

  public void testSubMixed() {
	  test_SimpleBehavior(_wrapper, "SubMixed", 6, "A1A2B1B2A4A5A6");
  }

  public void testComplexNonInt() {
	  test_SimpleBehavior(_wrapper, "ComplexNonInt", 35, "A1B1C1C2D1E1F1F2G1G2G3F3E2D2B3A2");
  }

  public void testInterruptNonInt() {
	 test_SimpleBehavior(_wrapper, "InterruptNonInt", 25, "A1B1C1C2D1E1E2A3");
  }

  public void testNonIntInterrupt() {
	  test_SimpleBehavior(_wrapper, "NonIntInterrupt", 25, "A1B1C1C2D1D2A3");
  }

  public void testAtomicNoDisrupt() {
	  test_SimpleBehavior(_wrapper, "AtomicNoDisrupt", 2, "C1B1C1B2A1");
  }

  public void testMultiTickNonInt() {
	  test_SimpleBehavior(_wrapper, "MultiTickNonInt", 15, "B1B2B3A2");
  }

  public void testNestedAtomicNoDisrupt() {
	  test_SimpleBehavior(_wrapper, "NestedAtomicNoDisrupt", 2, "B1B2A1");
  }

  public void testOneTickDisrupt() {
	  test_SimpleBehavior(_wrapper, "OneTickDisrupt", 2, "C1B1A2");
  }


  public void testTestOneTick() {
	  test_SimpleBehavior(_wrapper, "TestOneTick", 1, "C1B1");
  }


  public void testBasicHierarchical() {
	  test_SimpleBehavior(_wrapper, "BasicHierarchical", 10, "B1B1A1");
  }

  public void testDisruptBehavior() {
	  test_SimpleBehavior(_wrapper, "DisruptBehavior", 10, "A1B1B2A3");
  }

  public void testStackTransitions() {
	  System.out.println("testStackTransitions start");
	  test_SimpleBehavior(_wrapper, "StackTransitions", 10, "A1A2A1A2B1B2C1A1A2B1B2C2C3C1A1A2B1B2C2C3C1A1A2A3");
	  System.out.println("testStackTransitions finish");
  }


  public void testCompoundExecutionModeTest2() {
	  System.out.println("testCompoundExecutionModeTest2 start");
	  test_SimpleBehavior(_wrapper, "CompoundExecutionModeTest2", 3, "012end");
	  System.out.println("testCompoundExecutionModeTest2 finish");
  }

  public void testGetEntityNameTest() {
	  test_SimpleBehavior(_wrapper, "GetEntityNameTest", 1, "1");
  }

  public void testAlwaysTestOTNI() {
	  test_SimpleBehavior(_wrapper, "AlwaysNodeOTNI", 3, "onetwothree");
  }


  public void testPolyExact() {
	  test_SimpleBehavior(_wrapper, "PolyExact", 5, "A21B2C2");
  }

  public void testPolyInterit() {
	  test_SimpleBehavior(_wrapper, "PolyInherit", 5,  "A3B21C2");
  }

  public void testPolyOrder() {
	  test_SimpleBehavior(_wrapper, "PolyOrder", 5, "A1B2C");
  }

  public void testBasicBehavior() {
	  test_SimpleBehavior(_wrapper, "BasicBehavior", 1, "worked");
  }

  public void testCatchNode() {
	  test_SimpleBehavior(_wrapper, "CatchNode", 3, "caught1caught2caught3onetwothree");
  }

  public void testParentCatch() {
	  test_SimpleBehavior(_wrapper, "ParentCatch", 20, "onetwothreecaughtp1p2p3");
  }

  public void testParentCatchI() {
	  test_SimpleBehavior(_wrapper, "ParentCatchI", 20,  "onetwothreecaught");
  }

  public void testRetryException() {
	  test_SimpleBehavior(_wrapper, "RetryException", 20, "1");
  }


  public void testCorePreds() {
	  test_SimpleBehavior(_wrapper, "CorePreds", 10, "If 20:IsEntityFinished true");
  }


  public void testIsDoneTest() {
	  test_SimpleBehavior(_wrapper, "IsDoneTest", 10, "L4L3L2L1");
  }

  public void testResumeException() {
	  test_SimpleBehavior(_wrapper, "ResumeException", 20, "12");
  }


  public void testResumeExceptionComplicated() {
	  test_SimpleBehavior(_wrapper, "ResumeExceptionComplicated", 20, "onetwothreeabcdefgalways");
  }

  public void testRetryExceptionComplicated() {
	  test_SimpleBehavior(_wrapper, "RetryExceptionComplicated", 20, "onetwothreeworkedabcdefgalways");
  }

  public void testBehaviorParams3() {
	  // original test expects '<INVALID>testtest' but "testtesttest" is right
	  // result because of JavaScript engine's eval.
	  test_SimpleBehavior(_wrapper, "BehaviorParams3", 1, "testtesttest");
  }

  public void testCatchNodeBinding() {
	  test_SimpleBehavior(_wrapper, "CatchNodeBinding", 3, "caught1caught2caught3onetwothree");
  }

  public void testRethrowException() {
	  test_SimpleBehavior(_wrapper, "RethrowException", 20, "caughtparentcaught");
  }

  public void testCatchNodePredicate() {
	  test_SimpleBehavior(_wrapper, "CatchNodePredicate", 3, "caught1caught2caught3onetwothree");
  }


  public void testPushBehavior() {
	  // TODO: syl - PushBehavior works only in non-interruptible mode.
	  _wrapper.logTestStart("PushBehaviorTest");
	  long id = _wrapper.CreateEntity("TEST", "PushBehaviorTest", new ArrayList());
	  for (int update = 0; update < 10; update++) {
		  _wrapper.Update();
	  }

	  // verify result
	  assertEquals("15hello:A3:in:", buffer.toString());
	  _wrapper.DestroyEntity(id);
  }


  public static boolean Initialize(TestWrapper wrapper, String testPath,
      String file)
  {
    if (!wrapper.Initialize(testPath + "/test.log",
        testPath + "/" + file))
    {
      System.out.println("ERROR: unable to load test file '" + file + "'");
      return false;
    }
    return true;
  }

  /**
   * Runs any test that can be encapsulated in a single behavior run on one
   * entity and verified by checking the output against a fixed key.
   */
  public static void test_SimpleBehavior(TestWrapper wrapper, String behavior,
      int numUpdates, String expected)
  {
    wrapper.logTestStart(behavior);
    long id = wrapper.CreateEntity("TEST", behavior, new ArrayList());
    for (int update = 0; update < numUpdates; update++) {
      wrapper.Update();
    }

    // verify result
    assertEquals(expected, buffer.toString());
    wrapper.DestroyEntity(id);
  }

  public static void test_SimpleBehaviorTwoEntitiesOffset(TestWrapper wrapper, String behavior,
          int numUpdates, String expected)
      {
        wrapper.logTestStart(behavior);
        long id1 = wrapper.CreateEntity("TEST1", behavior, new ArrayList());
        long id2 = wrapper.CreateEntity("TEST2", behavior, new ArrayList());

        for (int update = 0; update < numUpdates + 1; update++)
        {
            if(update == 0)
                wrapper.UpdateEntity(id1);
            else
            if(update == numUpdates)
                wrapper.UpdateEntity(id2);
            else
            {
               wrapper.UpdateEntity(id1);
               wrapper.UpdateEntity(id2);
            }
        }
        wrapper.verifyResult(expected);
        wrapper.DestroyEntity(id1);
        wrapper.DestroyEntity(id2);
      }


  /**
   * Runs any test that can be encapsulated in a single behavior run on one
   * entity and verified by checking the output against a fixed key.
   */
  public static void test_SimpleBehaviorNoDestroy(TestWrapper wrapper,
      String behavior, int numUpdates, String expected)
  {
    wrapper.logTestStart(behavior);
    long id = wrapper.CreateEntity("TEST", behavior, new ArrayList());
    for (int update = 0; update < numUpdates; update++)
      wrapper.Update();
    wrapper.verifyResult(expected);
  }


  /**
   * Auxiliary function. Appends the given data value to the output buffer
   */
  public static void appendToBuffer(SB_ParamType type, SB_Param param)
   throws SB_Exception
  {

    if (type == SB_ParamType.kSB_Data)
    {
      if (param.getData() == null)
        buffer.append("null");
      else
        buffer.append(param.getData().toString());
    }
    buffer.append(",");
  }

}