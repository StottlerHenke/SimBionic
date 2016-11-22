package com.stottlerhenke.simbionic.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.stottlerhenke.simbionic.api.SB_Config;
import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.engine.core.SB_Entity;
import com.stottlerhenke.simbionic.engine.core.SB_ExecutionFrame;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;

/**
 * JavaScript engine that evaluates string expression for the simbionic engine.
 *
 */
public class SB_JavaScriptEngine {

   private static final String FILE_NAME_KEY = "javax.script.filename";

   private static final String ENTITY_ID = "_entityID";
   private static final String ENTITY_NAME = "_entityName";

   /**
    * JavaScript engine
    */
   private ScriptEngine _jsEngine;

   /**
    * The original bindings from the JavaScript engine.
    */
   private Bindings _origialBindings;

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

      /*
      try {
    	  
    	  _jsEngine.eval("load('nashorn:mozilla_compat.js');");
      }
      catch(Exception e) {
    	  // ignore the exception - perhaps we are running on Rhino! JOptionPane.showMessageDialog(null, "Failed to load nashorn compatibility", "wrapper load error", JOptionPane.WARNING_MESSAGE);
      }
		*/
      _origialBindings = _jsEngine.getBindings(ScriptContext.ENGINE_SCOPE);

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

      //loadScriptFiles(jsFileNames);
   }

   /**
    * Executes the specified expression in the javascript engine.
    *
    * @param expression Expression to be evaluated.
    * @param contextFrame The context frame that manages variables.
    * @return The value returned from the evaluation.
    * @throws Exception thrown if any error occurs during evaluation.
    */
   public Object evaluate(String expression, SB_ExecutionFrame contextFrame)
         throws SB_Exception {

      setBindings(contextFrame);

      try {
         Object value = _jsEngine.eval(expression);
         return value;
      } catch (ScriptException e) {
         e.printStackTrace();
         throw new SB_Exception(e.getMessage());
      }

   }

   /**
    * Executes the specified expression in the javaScript engine without the context frame.
    */
   public Object evaluate(String expression) throws SB_Exception {
	   // use original bindings
	   
	   _jsEngine.setBindings(_origialBindings, ScriptContext.ENGINE_SCOPE);

	   try {
		   Object value = _jsEngine.eval(expression);
		   return value;
	   } catch (ScriptException e) {
		   e.printStackTrace();
		   throw new SB_Exception(e.getMessage());
	   }
   }


   public void put(String key, Object value) {
      _jsEngine.put(key, value);
   }

   /**
    *
    * @param expression
    * @throws Exception
    */
  protected void checkSyntax(String expression) throws Exception {
     _jsEngine.setBindings(_origialBindings, ScriptContext.ENGINE_SCOPE);

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

   private void setBindings(SB_ExecutionFrame contextFrame) {
	  // if (contextFrame == null) return;
      // lazy evaluation of variables: use a VariableManagerBindings object
      // using the context frame  to return the value of variables only when the JS engine calls the get
      // method in this bindings.
      // If in addition the variable manager uses a lazy initialization
      // approach, then the initial value of variables will be calculated only the first time
      // the JS engine uses them.

      VariableManagerBindings lazzyBindings =
            new VariableManagerBindings(_origialBindings, contextFrame);
      
      _jsEngine.setBindings(lazzyBindings, ScriptContext.ENGINE_SCOPE);
      
      

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
			   }
		   }
	   }

	   return javaObj;
   }


   /**
    * Wrapper around the bindings object such that the {@link #get(Object)}
    * method defers to the variable manager to return the value of variables.
    *
    */
   class VariableManagerBindings implements Bindings {
      private Bindings initialBindings;
      private SB_ExecutionFrame frame;

      /**
       * Wrapper around the bindings object such that the {@link #get(Object)}
       * method defers to the variable manager to return the value of
       * variables. Other methods in the {@link Bindings} interface are
       * implemented by calling the method in the bindings object.
       *
       */
      VariableManagerBindings(Bindings bindings, SB_ExecutionFrame frame) {
    	  
         this.initialBindings = bindings;
         this.frame = frame;
         assertVariableManager();
      }

      /**
       * 'declares' the variable manager variable into the bindings object
       */
      void assertVariableManager() {
         // push entity id and name into the bindings.
         SB_Entity entity = frame.GetEntity();
         initialBindings.put(ENTITY_ID, entity.GetId()._id);
         initialBindings.put(ENTITY_NAME, entity.GetName());

         // put local and global variables into the bindings
         // if variables are not pushed into the initial bindigns,
         // 'get' is not called at all
         Collection vars = frame.GetVariableNames();
         Collection globalVars = frame.GetEntity().GetState().GetGlobalNames();

         if (vars == null && globalVars == null) {
            return;
         }

         for (Object var : vars) {
            String varName = (String) var;
            initialBindings.put(varName, null);
         }

         for (Object var : globalVars) {

            String varName = (String) var;
            initialBindings.put(varName, null);
            try {
            //System.err.println("Global " + varName + "  " + frame.GetVariable(varName).getType() + "  " + frame.GetVariable(varName).getValue());
            } catch (Exception ex) {

            }
         }


      }

      /**
       * if the key corresponds to a TG variable then the value is retrieved
       * from the variable manager and added to the {@link #initialBindings}.
       *
       * @param key
       */
      @Override
      public Object get(Object key) {
         if (key instanceof String) {
            String varName = (String) key;

            if (varName.equals(FILE_NAME_KEY) && !initialBindings.containsKey(key)) {
               // TODO: syl
               // don't know why it's trying to get the value of FILE_NAME_KEY,
               // but ignore this for now, until we have time to fix.
               //System.err.println(FILE_NAME_KEY + " is missing");
               return null;
            }

            try {
               SB_Variable var = frame.GetVariable(varName);
               if (var != null) {
                  Object value = var.getValue();
                  initialBindings.put(varName, value);
               }
            } catch (SB_Exception e) {
            	//e.printStackTrace();
               // TODO: syl - what do we do here?
               // Unfortunately we cannot throw the exception here as part of
               // the 'get' implementation.
               if (!initialBindings.containsKey(key) && !isJSReservedVariable(key)) {
                   System.err.println("Variable Initialization Error: "
                         + e.getMessage());
               }
            }

         }
         return initialBindings.get(key);
      }

      protected boolean isJSReservedVariable(Object variableName) {
    	  return (variableName instanceof String) && reservedJSVariables.contains(variableName.toString());
      };
      
      @Override
      public boolean containsKey(Object key) {
         return initialBindings.containsKey(key);
      }

      @Override
      public Object put(String name, Object value) {
         return initialBindings.put(name, value);
      }

      @Override
      public void putAll(Map<? extends String, ? extends Object> toMerge) {
         initialBindings.putAll(toMerge);
      }

      @Override
      public Object remove(Object key) {
         return initialBindings.remove(key);
      }

      @Override
      public void clear() {
         initialBindings.clear();
      }

      @Override
      public boolean containsValue(Object value) {
         return initialBindings.containsValue(value);
      }

      @Override
      public Set<java.util.Map.Entry<String, Object>> entrySet() {
         return initialBindings.entrySet();
      }

      @Override
      public boolean isEmpty() {
         return initialBindings.isEmpty();
      }

      @Override
      public Set<String> keySet() {
         return initialBindings.keySet();
      }

      @Override
      public int size() {
         return initialBindings.size();
      }

      @Override
      public Collection<Object> values() {
         return initialBindings.values();
      }

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
