package com.stottlerhenke.simbionic.test.engine;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Config;
import com.stottlerhenke.simbionic.api.SB_Engine;
import com.stottlerhenke.simbionic.api.SB_Error;
import com.stottlerhenke.simbionic.api.SB_Param;
import com.stottlerhenke.simbionic.api.SB_ParamType;

/**
 * Helper file for TestEngine
 */

public class TestWrapper {
  private SB_Engine _engine;
  private int _success;
  private int _failure;

  public TestWrapper() {
  }

  public SB_Engine GetEngine() { return _engine; }

  public boolean Initialize(String logFileName,String fullPathFileName)
  {
    _success = 0;
    _failure = 0;

    // set configuration parameters for the engine
    SB_Config config = new SB_Config();
    config.maxUpdatePeriod = 100;
    config.debugEnabled = false;
    config.debugConnectTimeout = 60;

    try
    {
      URL fileURL = new URL("file", "localhost", fullPathFileName);
      config.fileURL = fileURL;
    }
    catch(Exception e)
    {
      System.out.println("Exception creating a URL from the sim file name: " + e.toString());
    }
    // create an instance of the engine
    _engine = new SB_Engine();

    //Setup logging for the engine
    PrintStream logPrintStream = null;
    try
    {
      logPrintStream = new PrintStream( new FileOutputStream(logFileName) );
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }

    if( logPrintStream != null )
    {
      _engine.registerLogPrintStream( logPrintStream );
    }

    // attempt to load and initialize the engine
    SB_Error errCode = _engine.initialize(config );
    if (errCode != SB_Error.kOK)
    {
      System.out.println("Initialize() error: " + _engine.getLastError());
       System.out.println("FAILED\n");
       return false;
    }
    
    return true;
  }
  
  public boolean Finish()
  {
    _engine.log("************************ It's Log! ************************");

     // shut down and unload SimBionic
     if (_engine.terminate() != SB_Error.kOK)
     {
         System.out.println("Terminate() error: " + _engine.getLastError());
     }

      System.out.println(_success + " tests passed");
      System.out.println(_failure + " tests failed");

     if (DidPass())
     {
        System.out.println("PASSED\n");
        return true;
     }
     else
     {
        System.out.println("FAILED\n");
        return false;
     }
  }
  
  public void verifyResult(String answerString)
   {
     System.out.println("TestWrapper VerifyResult: " + TestEngine.buffer.toString());
     
     if (answerString.compareTo( TestEngine.buffer.toString()) == 0)
     {
         System.out.println("PASSED");
         PassedTest();
     }
     else
     {
         System.out.println("FAILED");
         FailedTest();
         System.out.println(TestEngine.buffer);
     }   
  }
  
  public void logTestStart(String testName)
   {
    // clear the answer buffer
    TestEngine.buffer.delete(0, TestEngine.buffer.length());
  }

  public void FailedTest()
  {
          _failure++;
  }

  public int NumTestsFailed()
  {
          return _failure;
  }

  public void PassedTest()
  {
          _success++;
  }

  public int NumTestsPassed()
  {
          return _success;
  }

  public boolean DidPass()
  {
          return _failure == 0;
  }

  public void reportAPIError(String methodName)
   {
   System.out.println(methodName + " error: " + _engine.getLastError());
    FailedTest();
    Finish();
  }
  

  public void Update()
  {
     if (_engine.update() != SB_Error.kOK)
     {
      reportAPIError("Update");
     }
  }

  public void UpdateEntity( long entityId)
  {
     if (_engine.updateEntity(entityId) != SB_Error.kOK)
     {
      reportAPIError("UpdateEntity");
     }
  }

  public long CreateEntity( String entityName, String behaviorId,ArrayList params){
    return CreateEntity(entityName, behaviorId, params,0, 0);
  }
  
  public long CreateEntity( String entityName, String behaviorId,ArrayList params,
                                                                          int updateFreq,int updatePriority)
  {
     long entityId = _engine.createEntity(entityName,behaviorId,params,updateFreq,updatePriority);
     
     if(entityId == SB_Param.INVALID_ID)
        reportAPIError("CreateEntity");
     
     return entityId;
  }

  public long MakeEntity( String entityName)
  {
     long entityId = _engine.makeEntity( entityName );
     if (entityId == SB_Param.INVALID_ID)
     {
      reportAPIError("MakeEntity");
     }
     return entityId;
  }

  public void SetEntityGlobal( long entityId, String varName,SB_Param value)
  {
     if (_engine.setEntityGlobal(entityId,varName,value) != SB_Error.kOK)
     {
      reportAPIError("SetEntityGlobal");
     }
  }

  public SB_Param GetEntityGlobal( long entityId, String varName)
  {
     SB_Param value = _engine.getEntityGlobal(entityId,varName);
     if (value.getType() == SB_ParamType.kSB_Invalid)
     {
      reportAPIError("GetEntityGlobal");
     }
     return value;
  }

  public void SetBehavior( long entityId, String behaviorId, ArrayList<SB_Param> params)
  {
     SB_Error errCode = _engine.setBehavior( entityId, behaviorId, params);
     if (errCode != SB_Error.kOK)
     {
      reportAPIError("SetBehavior");
     }
  }

  public void SetUpdateFreq(int newFreq, long entityId)
  {
     SB_Error errCode = _engine.setUpdateFreq(newFreq,entityId);
     if (errCode != SB_Error.kOK)
     {
      reportAPIError("SetUpdateFreq");
     }
  }

  public void SetUpdatePriority(int newPriority, long entityId)
  {
     SB_Error errCode = _engine.setUpdatePriority(newPriority,entityId);
     if (errCode != SB_Error.kOK)
     {
      reportAPIError("SetUpdatePriority");
     }
  }

  public SB_Error swapProject(URL projectURL)
   {
     SB_Error errCode = _engine.swapProject(projectURL);
     if (errCode != SB_Error.kOK)
     {
      reportAPIError("SwapProject");
     }
     return errCode;
  }

 

  public void unloadAll()
  {
     SB_Error errCode = _engine.unloadAll();
     if (errCode != SB_Error.kOK)
     {
      reportAPIError("UnloadAll");
     }
  }

  public void DestroyEntity( long entityId)
  {
     SB_Error errCode = _engine.destroyEntity(entityId);
     if (errCode != SB_Error.kOK)
     {
      reportAPIError("DestroyEntity");
     }
  }

  public boolean IsEntityFinished( long entityId)
  {
   return _engine.isEntityFinished(entityId);
  }

}
