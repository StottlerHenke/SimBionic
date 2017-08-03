package com.stottlerhenke.simbionic.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import com.stottlerhenke.simbionic.api.SB_Config;
import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_ID;
import com.stottlerhenke.simbionic.engine.core.SB_ExecutionFrame;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * JavaScript engine that evaluates string expression for the simbionic engine.
 *
 */
public class SB_JavaScriptEngine {

   /**
    * JavaScript engine
    */
   private ScriptEngine _jsEngine;
   private Map<SB_ID, ScriptContext> _entityToScriptContext;
   
   private SB_Config _config;

   /** reserved variables for the JS engine**/
   private Set<String> reservedJSVariables ;

   /**
    * Constructor
    *
    */
   public SB_JavaScriptEngine(SB_Config config) {
      _config = config;
      // create a script engine manager and JavaScript engine
      _jsEngine = new ScriptEngineManager().getEngineByName("nashorn");
      
      reservedJSVariables = new HashSet<String>();
      reservedJSVariables.add("nashorn.global");
      
      // Create a distinct script context for each entity
      _entityToScriptContext = new HashMap<>();
      
      // Create a custom bindings object that can take information from the simbionic entity during evaluation
      JavaScriptBindings bindings = new JavaScriptBindings(_jsEngine.createBindings(), reservedJSVariables);
      _jsEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
   }

   /**
    * Initialize the js engine
    *
    * @param jsFileNames The list of javascript file names
    * @param javaPackageNames The list of java package names
    * @param javaClassNames The list of java class names
    */
   public void init(List<String> jsFileNames, List<String> javaPackageNames,
		   List<String> javaClassNames) throws Exception {

	   loadScriptFiles(jsFileNames);

	   for (String packageName: javaPackageNames) {
		   importPackage(packageName);
	   }

	   for (String className: javaClassNames) {
		   importClass(className);
	   }
   }

   /**
    * Get a script context customized for the given execution frame.
    * There should be one script context per entity.
    * 
    * @param frame
    * @return
    */
   protected ScriptContext getScriptContext(SB_ExecutionFrame frame) {
	   SB_ID entityId = frame.GetEntity().GetId();
	   ScriptContext entityContext = _entityToScriptContext.get(entityId);
	   if(entityContext == null) {
		   entityContext = new SimpleScriptContext();
		   
		   //Create an empty binding. This does not include the Nashorn map.
		   JavaScriptBindings entityBindings = new JavaScriptBindings(_jsEngine.createBindings(), this.reservedJSVariables);		   	   
		   entityContext.setBindings(entityBindings, ScriptContext.ENGINE_SCOPE);
		   
		   //Evaluate with the new context to create an empty Nashorn map
		   try {
			   _jsEngine.eval("", entityContext);
		   } catch (ScriptException e) {
			   e.printStackTrace();
		   }
		   //Copy all values from the global map to the local map
		   ScriptObjectMirror global = (ScriptObjectMirror )_jsEngine.getBindings(ScriptContext.ENGINE_SCOPE).get("nashorn.global");
		   ScriptObjectMirror local = (ScriptObjectMirror ) entityBindings.get("nashorn.global");
		   for(Entry<String, Object> entry : global.entrySet()) {
			   local.put(entry.getKey(), entry.getValue());
		   }

		   _entityToScriptContext.put(entityId, entityContext);
	   }
	   return entityContext;
   }
   
   /**
    * Executes the specified expression in the javascript engine.
    * 
    * Note that the JavaScriptEngine relies on bindings stored in a ScriptObjetMirror, which takes precedence over bindings found 
    * in the script context. While each entity has its own ScriptContext and Bindings, all entities share the same ScriptObjectMirror because
    * they share the same javascript engine. The code in getScriptContext gets around this by creating an empty
    * ScriptObjectMirror and then copying values into it from the global ScriptObjectMirror.
    * 
    * IF any assignment is made in an expression, then that value is stored in the nashorn.global ScriptObjectMirror. 
    * 	See: https://wiki.openjdk.java.net/display/Nashorn/Nashorn+jsr223+engine+notes
    * The stored value will then be used for every expression with the same variable name, for all agents. This does not extend
    * to variables with the same name in a JavaScript method called by the expression. This collision is limited to the evaluated expression.  
    * 
    * The solution for now is to manually remove any variables found in nahsorn.global that did not exist prior to the expression evaluation.
    * 
    * We might want to clone the entire ScriptObjectMirror or even javascript engine in the future.
    * 	See: https://stackoverflow.com/questions/11116120/clone-entire-javascript-scriptengine
    *
    * @param expression Expression to be evaluated.
    * @param contextFrame The context frame that manages variables.
    * @return The value returned from the evaluation.
    * @throws Exception thrown if any error occurs during evaluation.
    */
   public Object evaluate(String expression, SB_ExecutionFrame contextFrame) throws SB_Exception {
	
	   //Get the context and bindings for the entity
	   ScriptContext entityContext = getScriptContext(contextFrame);
	   
	   //Copy the frame variables into the context bindings
	   JavaScriptBindings entityBindings = (JavaScriptBindings) entityContext.getBindings(ScriptContext.ENGINE_SCOPE);
	   entityBindings.setExecutionFrame(contextFrame);
	   
	   //Get a list of the initial keys in nashorn global
	   ScriptObjectMirror global = (ScriptObjectMirror )entityBindings.get("nashorn.global");
	   Set<String> originalKeys = new HashSet<>(global.keySet());
	   
	   try {
		   //Evaluate the expression with the entity context
		   Object value = _jsEngine.eval(expression, entityContext);
		   
		   //Remove any newly created keys
		   //If we wanted to keep assignments made in expressions, this is where would copy information from the javascript engine back into simbionic
		   List<String> toRemove = new ArrayList<>();
		   for(String key : global.keySet()) {
			   if(!originalKeys.contains(key)) {
				   toRemove.add(key);
			   }
		   }
		   for(String key : toRemove) global.remove(key);
		   
		   return value;
		   
	   } catch (ScriptException e) {
		   throw new SB_Exception(getMessage(e, true) + " in expression: \n" + expression);
	   }

   }

   /**
    * Executes the specified expression in the javaScript engine without the context frame.
    * 
    * Used only to initialize global variables.
    */
   public Object evaluate(String expression) throws SB_Exception {
	  
	   try {
		   Object value = _jsEngine.eval(expression); 
		   return value;
	   } catch (ScriptException e) {
		   e.printStackTrace();
		   throw new SB_Exception(e.getMessage());
	   }
   }

   public void put(SB_ExecutionFrame contextFrame, String key, Object value) {
	   
	   ScriptContext entityContext = getScriptContext(contextFrame);
	   entityContext.getBindings(ScriptContext.ENGINE_SCOPE).put(key, value);
   }

   /**
    *
    * @param expression
    * @throws Exception
    */
  protected void checkSyntax(String expression) throws Exception {
    // _jsEngine.setBindings(_originalBindings, ScriptContext.ENGINE_SCOPE);

      String lambdaFunction = "function _lambda() {"+ expression + ";}";
      _jsEngine.eval(lambdaFunction);
  }

  /**
   * Load the list of javascript files
   * @param jsFiles The list of java script files
   * @throws Exception Thrown if any error occurs during loading.
   */
   private void loadScriptFiles(List<String> jsFiles) throws Exception  {
      if (jsFiles == null) {
         return;
      }

      for (String fileName : jsFiles ) {
         try {
            URL url;
            // use base URL if specified.
            if (_config.getBaseURL() != null) {
               url = new URL(_config.getBaseURL(), fileName);
            } else {
               url = new File(fileName).toURI().toURL();
            }
            loadJsFile(url);
         } catch (ScriptException e) {
            String errorMsg = "Problem  loading script file '" + fileName + "'";
            errorMsg += ": " + getMessage(e, false);
            throw new RuntimeException(errorMsg);
         }
      }
   }

   /**
    * imports the given javaClassName. Throws exception if the class does not exist
    *
    * @param javaClassName Java class name to import.
    * @throws Exception Thrown if the class doesn't exist.
    */
   private void importClass(String javaClassName) throws Exception {
      try {
         Class.forName(javaClassName);
         String path[] = javaClassName.split("\\.");
         _jsEngine.eval(path[path.length-1] + " = Java.type(\"" + javaClassName + "\")" );
      } catch (Exception e) {
         System.err.println("Error importing class: " + javaClassName);
         System.err.println(e.getMessage());
         throw e;
      }
   }

   /**
    * imports the given java package. Throws exception if the package does not exist
    *
    * @param packageName Java package to import.
    * @throws Exception Thrown if the package doesn't exist.
    */
   private void importPackage(String packageName) throws Exception {
      try {
         String exp = "importPackage(" + packageName + ");";
         _jsEngine.eval(exp);
      }  catch (Exception e) {
         System.err.println("Error importing pakage: "+ packageName);
         throw e;
      }
   }


   /**
    * Load the specified JavaScript file
    *
    * @param url
    *           The URL of the JavaScript file to load.
    * @throws Exception
    *            Thrown if any error occurs during load.
    */
   private void loadJsFile(URL url) throws Exception {
      int bufferSize = 1024 * 100;
      BufferedReader reader =
            new BufferedReader(new InputStreamReader(url.openStream()),
                  bufferSize);
      String line;
      StringBuffer sb = new StringBuffer(bufferSize);
      while ((line = reader.readLine()) != null) {
         sb.append(line).append("\n");
      }

      reader.close();

      _jsEngine.eval(new StringReader(sb.toString()));
   }

   /**
    * Compile the specified expression in the js engine.
    * @param expression Expression to compile
    * @throws Exception thrown if any error occurs.
    */
   public static void compile(String expression) throws Exception {
      SB_JavaScriptEngine engine = new SB_JavaScriptEngine(new SB_Config());
      engine.checkSyntax(expression);
   }


   /**
    * Return an informative error message associated with a ScriptException.
    *
    * @param e  script engine exception
    * @param includeLineNumber whether the line information in the error exception should be included. This should
    *        be only the case when parsing files.
    * @return
    */
   public static String getMessage(ScriptException e, boolean includeLineNumber) {
      String originalMsg = e.getMessage();
      // the script exception looks like
      // javax.script.ScriptException: sun.org.mozilla.javascript.internal.EvaluatorException: syntax error (<Unknown source>#2) in <Unknown source> at line number 2
      // we want to produce
      // syntax error  at line number 2
      // java.lang.RuntimeException: Problem  loading script file 'datamontage.js' sun.org.mozilla.javascript.internal.EcmaError: ReferenceError: "aa" is not defined. (<Unknown source>#3) in <Unknown source> at line number 3

      String[] prefixes = new String[]{"EvaluatorException:","EcmaError:", "WrappedException:"};
      int i = -1;
      String prefix = null;
      for (String cue : prefixes) {
         prefix = cue;
         i = originalMsg.indexOf(prefix);
         if (i>=0) {
            break;
         }
      }


      if (i < 0) {
         return originalMsg;
      }

      String msg = originalMsg.substring(i+prefix.length()).trim();
      if (msg.startsWith("Wrapped")) {
         msg = msg.replaceFirst("Wrapped", "").trim();
      }

      if (msg.startsWith("java.io.FileNotFoundException:")) {
         msg = msg.replaceFirst("java.io.FileNotFoundException:", "").trim();
         msg = "File not found: " + msg;
      }

      // to remove (<Unknown source>#2) in <Unknown source>
      // find first occurrence of ( and last occurrence of > and remove that substring

      i = msg.indexOf('(');
      if (i < 0) {
         return msg;
      }

      if (includeLineNumber == false) {
         return msg.substring(0,i);
      }

      msg = msg.replace("at line", "Line ");

      int j = msg.lastIndexOf('>');
      if (j < 0) {
         return msg;
      }

      msg = msg.substring(0,i) + msg.substring(j+1);

      return msg;
   }


   /**
    * Cast the specified javaScript engine's return object to the expected type of java obj.
    */
   public static Object castToJavaObject(Object jsEngineReturnObj, String expectedType)
		   throws SB_Exception {
	   if (jsEngineReturnObj == null) return jsEngineReturnObj;

	   String returnType = jsEngineReturnObj.getClass().getName();
	   Object javaObj = jsEngineReturnObj;
	   // check type
	   if (!returnType.equals(expectedType)) {
		   if (jsEngineReturnObj instanceof java.lang.Double) { // js engine always returns Double for numbers.
			   Double doubleValue = (Double)jsEngineReturnObj;
			   if (expectedType.equals(Integer.class.getName())) {
				   javaObj = doubleValue.intValue();
			   } else if (expectedType.equals(Float.class.getName())) {
				   javaObj = doubleValue.floatValue();
			   } else if (expectedType.equals(Byte.class.getName())) {
				   javaObj = doubleValue.byteValue();
			   } else if (expectedType.equals(Long.class.getName())) {
				   javaObj = doubleValue.longValue();
			   } else if (expectedType.equals(Short.class.getName())) {
				   javaObj = doubleValue.shortValue();
			   } else {
				   throw new SB_Exception("Unsupported type found: " + expectedType);
			   }
		   } else {
			   // cast to the expected Java object
			   try {
				   javaObj = Class.forName(expectedType).cast(jsEngineReturnObj);
			   } catch (ClassNotFoundException cnfe) {
				   throw new SB_Exception("Couldn't find the class: " + cnfe.getMessage());
			   } catch (ClassCastException ex) {
				   throw new SB_Exception("Couldn't cast return type: " + ex.getMessage());
			   }
		   }
	   }

	   return javaObj;
   }


   public static void main(String[] args) {
	   ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
	   try {
	   Object obj = Class.forName("java.util.ArrayList").newInstance();
	   String expr = "new java.util.ArrayList(java.util.Arrays.asList([15, 'hello']))";
	   Object returnVal = engine.eval(expr);
	   Object cast = Class.forName("java.util.ArrayList").cast(returnVal);

	   //Error found Cannot cast java.lang.Double to java.lang.Integer
	   System.out.println("Object: " + obj + " returnVal: " + returnVal + " cast: " + cast);

	   // Double to Integer doesn't work
	   expr = "3";
	   returnVal = engine.eval(expr);
	   //cast = Class.forName("java.lang.Integer").cast(returnVal);
	   if (returnVal instanceof Double) {
		   cast = ((Double)returnVal).intValue();

	   }
	   System.out.println("ReturnVal: " + returnVal + " cast: " + cast);

	   // Error found Cannot cast java.lang.Double to java.lang.Float
	   expr = "3.9";
	   returnVal = engine.eval(expr);
	   if (returnVal instanceof Double) {
		   cast = ((Double)returnVal).floatValue();
	   }
	   //cast = Class.forName("java.lang.Float").cast(returnVal);
	   System.out.println("ReturnVal: " + returnVal + " cast: " + cast);


	   expr = "'hello world'";
	   returnVal = engine.eval(expr);
	   cast = Class.forName("java.lang.String").cast(returnVal);
	   System.out.println("ReturnVal: " + returnVal + " cast: " + cast);

	   expr = "true";
	   returnVal = engine.eval(expr);
	   cast = Class.forName("java.lang.Boolean").cast(returnVal);
	   System.out.println("ReturnVal: " + returnVal + " cast: " + cast);

	   expr = "new java.util.Vector(java.util.Arrays.asList([15, 'hello', 100]))";
	   returnVal = engine.eval(expr);
	   cast = Class.forName("java.util.Vector").cast(returnVal);
	   System.out.println("ReturnVal: " + returnVal + " cast: " + cast);

	   expr = "new com.stottlerhenke.simbionic.common.Table()";
	   returnVal = engine.eval(expr);
	   cast = Class.forName("com.stottlerhenke.simbionic.common.Table").cast(returnVal);
	   System.out.println("ReturnVal: " + returnVal + " cast: " + cast);

	   expr = "new com.stottlerhenke.simbionic.api.SB_Config()";
	   returnVal = engine.eval(expr);
	   cast = Class.forName("com.stottlerhenke.simbionic.api.SB_Config").cast(returnVal);
	   System.out.println("ReturnVal: " + returnVal + " cast: " + cast);

	   } catch (Exception es) {
		   System.err.println("Error found " + es.getMessage());
		   es.printStackTrace();
	   }


	   try {
		   System.out.println(engine.eval("count = 0"));
		   System.out.println(engine.eval("count = count + 1"));
		   System.out.println(engine.eval("count = count + 1"));
		   System.out.println(engine.eval("counter = 1"));
		   System.out.println(engine.eval("counter += 2"));
		   System.out.println(engine.eval("counter += 3"));
		   Object result = engine.eval("new java.util.ArrayList(java.util.Arrays.asList([15, 'hello']))");
		   System.out.println(result + " Class: " + result.getClass().getName());


		   String jsFunction = "function BehaviorParams2(param) {\n" +
				   "var params = new Array(); \n" +
				   "params[0] = param; \n" +
				   " _behaviorNode.runBehavior(_behaviorEntity, _behaviorContextFrame, _behaviorBook, params); \n" +
				   "} ";

		   result = engine.eval(jsFunction);

		   result = engine.eval("new java.util.Vector( java.util.Arrays.asList([1, 2]))");

	   } catch (Exception ex) {
		   ex.printStackTrace();
	   }



   }


}
