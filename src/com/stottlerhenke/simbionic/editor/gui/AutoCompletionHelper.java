package com.stottlerhenke.simbionic.editor.gui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.stottlerhenke.simbionic.common.classes.SB_ClassMap;
import com.stottlerhenke.simbionic.editor.SB_Variable;


/**
 * Helper class to calculate completions for methods and member attributes
 * associated with an object variable.
 * <br>
 * Usage:<br>
 * <ol>
 *  <li> call {@link #initializeContent(Vector, List<String>, List<String>)} to set the 
 *  known variables and class specifications
 *  <li> call {@link #matchPartialDot(Vector, String, String)} to get any
 *  partial match associated with a method/member reference
 *  <li> call {@linkplain #parseDot(String, int, ParseInfo)}} to know if a 
 *  an expression represents a valid method call.
 * </ol>
 * <br>
 * Limitations:<br>
 * <ol>
 *  <li>Only completions for variable methods/members are calculated. For instance,
 *  there will not be completions for something like "myvar.myMethod().x", where the
 *  x refers to the a method that applies to the results of "myvar.myMethod()".
 * </ol>
 * 
 */
public class AutoCompletionHelper {

   /**
    * Helper class to calculate completions for methods and member attributes
    * associated with an object variable.
    * <br>
    * Usage:<br>
    * <ol>
    *  <li> call {@link #initializeContent(Vector, List<String>, List<String>)} to set the 
    *  known variables and class specifications
    *  <li> call {@link #matchPartialDot(Vector, String, String)} to get any
    *  partial match associated with a method/member reference
    *  <li> call {@linkplain #parseDot(String, int, ParseInfo)}} to know if a 
    *  an expression represents a valid method call.
    * </ol>
    * <br>
    * Limitations:<br>
    * <ol>
    *  <li>Only completions for variable methods/members are calculated. For instance,
    *  there will not be completions for something like "myvar.myMethod().x", where the
    *  x refers to the a method that applies to the results of "myvar.myMethod()".
    * </ol>
    *
    */
   public AutoCompletionHelper() {
   }
   
   /** clears the variables and class specifications previously set by 
    * {@link #initializeContent(Vector, List<String>, List<String>)} **/
   public void clearContent() {
     variablesMap.clear();
     importedClasses.clear();
     knownClasses.clear();
   }
   
   /**
    * defines the variables and class specifications the helper knows about. This
    * data is used when calling {@link #parseDot(String, int, ParseInfo)} and 
    * {@link #matchPartialDot(Vector, String, String)}
    * 
    * @param variables
    * @param importedClasses 
    */
   public void initializeContent(Vector<SB_Variable> variables, List<String> importedClasses) {
      clearContent();
      if (variables!=null) {
         for (SB_Variable var: variables) {
            variablesMap.put(var.getName(), var);
         }
      }
      
     
      if (importedClasses != null) {
         this.importedClasses.addAll(importedClasses);
      }
      
      initKnownClasses();
   }
   
   /**
    * cache the name of all classes the helper knows about. These classes are
    * derived from {@link #importedPackages} and {@link #importedClasses}. These
    * classes are used by {@link #parseDot(String, int, ParseInfo)} to include classes
    * along with variable when performing autocompletion.
    */
   protected void initKnownClasses() {
      for (String importedClass : importedClasses) {
         int i = importedClass.lastIndexOf('.');
         if (i >= 0) {
            knownClasses.add(importedClass.substring(i+1));
         }
         else {
            knownClasses.add(importedClass);
         }
      }

   }

   
   /**
    * Generate completions for "variableName.dotArg".&nbsp;Returns true if some completions where added to matchList. 
    * 
    * 
    * @param matchList - new completions will be added to this list
    * @param variableName
    * @param dotArg
    * @return
    */
   public boolean  matchPartialDot(Vector matchList, String variableName, String dotArg) {
      try {
         SB_Variable var = getVariable(variableName);

         if (var == null) {
            return matchPartialDotStaticMethod(matchList,variableName,dotArg);
         }

         //access the list of possible methods for the given variable
         String type = var.getFullTypeName(); 
         
         if (type == null) {
            return false;
         }
         
         Class varClass = null;
         try {
            varClass = Class.forName(type);
         }
         catch (Exception e) {
            e.printStackTrace();
            return false; // no match
         }
   
      
         if (varClass == null) {
            return false;
         }
         
         //Map m = desc.getMembers(); map FieldName to class
         //List l = desc.getMethods();
         
         //Map<String,Class> members = (Map<String,Class>)desc.getMembers();
         //Set<SB_ClassMethod> methods = new HashSet<SB_ClassMethod>(desc.getMethods());
         Field[] members = varClass.getFields();//only the public fields //fixme. Does this include superclass members?
         Method[] methods = varClass.getMethods();//public methods

         
      
         boolean foundCompletions = false;
         
         Vector<String> matchedMembers = new Vector<String>();
         //for (String fieldName : members.keySet()) {
         for (Field field : members) {
            String fieldName = field.getName();
            if (fieldName.startsWith(dotArg)) {
               //matchedMembers.add(getMemberDisplayName(fieldName,(Class)members.get(fieldName)));
               matchedMembers.add(getMemberDisplayName(fieldName, field.getType().getName()));
               foundCompletions = true;
            }
         }
         
         Vector<String> matchedMethods = new Vector<String>();
         //for (SB_ClassMethod method : methods) {
         for (Method method : methods) {
            if (method.getName().startsWith(dotArg)) {
               matchedMethods.add(getMethodDisplayName(method));
               foundCompletions = true;
            }
         }
         
         //sort the completion
         Comparator<String> stringComparator = new Comparator<String>(){
            @Override
            public int compare(String o1, String o2) {
               return o1.compareTo(o2);
            }
            
         };
         
         Collections.sort(matchedMembers, stringComparator);
         Collections.sort(matchedMethods, stringComparator);
         
         matchList.addAll(matchedMembers);
         matchList.addAll(matchedMethods);
         
         return foundCompletions;  
      }
      catch (Exception e) {
         //e.printStackTrace();
      } 
      
      return false;
   }
   
   
   /**
    * 
    * @param matchList
    * @param className
    * @param dotArg
    * @return
    */
   public boolean  matchPartialDotStaticMethod(Vector matchList, String className, String dotArg) {
      try { 
         Class varClass = this.getClass(className);

         if (varClass == null) {
            return false;
         }
         
         Field[] members = varClass.getFields();//only the public fields //fixme. Does this include superclass members?
         Method[] methods = varClass.getMethods();//public methods

         
      
         boolean foundCompletions = false;
         
         Vector<String> matchedMembers = new Vector<String>();
         //for (String fieldName : members.keySet()) {
         for (Field field : members) {
            if (Modifier.isStatic(field.getModifiers())) {
               String fieldName = field.getName();
               if (fieldName.startsWith(dotArg)) {
                  //matchedMembers.add(getMemberDisplayName(fieldName,(Class)members.get(fieldName)));
                  matchedMembers.add(getMemberDisplayName(fieldName,field.getType().getName()));
                  foundCompletions = true;
               }
            }
         }
         
         Vector<String> matchedMethods = new Vector<String>();
         //for (SB_ClassMethod method : methods) {
         for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
               if (method.getName().startsWith(dotArg)) {
                  matchedMethods.add(getMethodDisplayName(method));
                  foundCompletions = true;
               }
            }
         }
         
         //sort the completion
         Comparator<String> stringComparator = new Comparator<String>(){
            @Override
            public int compare(String o1, String o2) {
               return o1.compareTo(o2);
            }
            
         };
         
         Collections.sort(matchedMembers, stringComparator);
         Collections.sort(matchedMethods, stringComparator);
         
         matchList.addAll(matchedMembers);
         matchList.addAll(matchedMethods);
         
         return foundCompletions;
      }
      catch (Exception e) {
         //e.printStackTrace();
      }
      
      return false;
   }
   
   /**
    * returns variable named varName.&nbsb;Returns null is not such variable exists
    * @param varName
    * @return
    */
   private SB_Variable getVariable(String varName) {
      return variablesMap.get(varName);
   }
   
   /**
    * returns info.index == -1 if not dot completion might be possible. Otherwise, 
    * info.funcName is the name of the variable on which the dot is being applied, and
    * info.paramName is the string after the dot.
    * 
    * Limitations:<br>
    * <ol>
    *  <li>Only completions for variable methods/members are calculated. For instance,
    *  there will not be completions for something like "myvar.myMethod().x", where the
    *  x refers to the a method that applies to the results of "myvar.myMethod()".
    * </ol>
    * @param expr expression being parsed
    * @param pos index in the expression at which the completion should be done 
    * @param info returned object
    */
   
   public void parseDot(String expr, int pos, ParseInfo info) {   
      //find the candidate for a variable, which is the string
      //before a dot. First find the dot going backward from the current
      //position
      String delimitersChars = "\n\"(), &!=<>|-+/";
      info.index = -1; //no dot completion possible
      int dotPosition = -1;
      for (int i = pos-1 ; i >= 0 ; i--) {
         char c =  expr.charAt(i);
         if (delimitersChars.indexOf(c) != -1) {
            return; //there is a dot invocation
         }
         
         if (c=='.') {
            dotPosition =i;
            break;
         }
      }
      
      if (dotPosition ==-1) {
         return; //no dot completion possible
      }
      
      //now try to find the variable name going backwards from the dotPosition
      //until the beginning of the expression of until finding a delimiter
      
      delimitersChars +="."; //add the dot a new delimiter
      int varNameStartPosition=0;//beginning of the expression
      for (int i= dotPosition-1 ; i >=0 ; i--) {
         char c = expr.charAt(i);
         if (delimitersChars.indexOf(c)!=-1) {
            varNameStartPosition=i+1;
            break;
         }
      }
      
      //else
      String varName = expr.substring(varNameStartPosition,dotPosition);
      
      //check we have a variable and not some garbage string
      if (getVariable(varName)==null) {
         //see if there is a class we could load
         if (getClass(varName)== null) {
            return;//there was not variable before the dot
         }
      }
      String methodName = expr.substring(dotPosition+1,pos);
      info.index = dotPosition;
      info.funcName = varName;
      info.paramName = methodName;
      
   }
   
   
   /**
    * if a known java class starts with classNamePrefix add the complete class
    * name to matchList
    * @param matchList
    * @param classNamePrefix
    */
   public void matchPartialClassName(Vector matchList, String classNamePrefix){
     if (classNamePrefix == null || classNamePrefix.length() == 0) return;

     String aKnonwClassName;
     int size = knownClasses.size();
     for (int i = 0; i < size; i++){
        aKnonwClassName = knownClasses.get(i);

        if (classNamePrefix.regionMatches(0, aKnonwClassName, 0, classNamePrefix.length())) {
           /*
           String type = var.getType().toString();
           if (var.getType() == VariableType.OBJECT) {
              type =  ((ObjectVariable)var).getClassName();
           }
           matchList.add(varName + " : " + type);
           */
           matchList.add(aKnonwClassName);
      }
     }
   }
   /**
    * returns the class 
    * @param className
    * @return
    */
   protected Class getClass(String className) {
      //try one of the imported classes
      for(String importedClass : this.importedClasses) {
         //importedClass has the form package.Name
         //we need to remove class the package
         try {
            int i = importedClass.lastIndexOf(".");
            String importedClassName = (i>=0)? importedClass.substring(i+1) : importedClass;
            if (importedClassName.equals(className)==true) {
               Class varClass =  Class.forName(importedClass);//verify the class exists
               return varClass;
            }
         }
         catch (Exception e) {

         }
      }
      
      return null;
   }
   
   /** 
    * returns string to display in the autocompletion list.&nbsp;Members are displayed as
    * "memberName : type"
    * @param memberName
    * @param type
    * @return
    */
   private String getMemberDisplayName(String memberName, String type) {
      return memberName + " : " + type;
   }
   
   /**
    * returns string to display in the autocompletion list.&nbsp;Methods are displayed as
    * "methodName(type_1,...,type_n)"
    * 
    * @param method
    * @return
    */
   private String getMethodDisplayName(Method method) {
      String str = method.getName() + "(";
      Class[] params = method.getParameterTypes();
      int n = (params == null) ? 0 : params.length;
      //usually the params name is null
      for(int i=0 ; i < (n-1) ; i++) {
         Class param = params[i];
         /*
         if (param instanceof Class) {
            str += getClassName(param);
         }

         else {
            str += "x_"+i;
         }
         */
         str += getClassName(param);
         str +=",";
      }
      
      if (n>0) {
         /*
         Object param = method._params.get(n-1);
         if (param instanceof Class) {
            str += getClassName((Class)param);
         }
         else {
            str += "x_"+(n-1);
         }
         */
         str += getClassName(params[n-1]);
      }
      str +=")";
      
      return str; 
   }
   /*
   private String getMethodDisplayName(SB_ClassMethod method) {
      String str = method.getName() + "(";
      int n = (method._params == null) ? 0 : method._params.size();
      //usually the params name is null
      for(int i=0 ; i < (n-1) ; i++) {
         Object param = method._params.get(i);
         if (param instanceof Class) {
            str += getClassName((Class)param);
         }
         else {
            str += "x_"+i;
         }
         str +=",";
      }
      
      if (n>0) {
         Object param = method._params.get(n-1);
         if (param instanceof Class) {
            str += getClassName((Class)param);
         }
         else {
            str += "x_"+(n-1);
         }
      }
      str +=")";
      
      return str; 
   }
   */
   
   /**
    * returns the name of the class without the package string
    * @param c
    * @return
    */
   private String getClassName(Class c) {
      if (c.isArray()==true) {
         String className = c.getSimpleName().replaceAll("\\[", " &#91;").replaceAll("\\]", " &#93;");
         return className;

      }
      else {
         String name = c.getName();
         int i = name.lastIndexOf('.');
         if (i == -1) {
            return name;
         }
         else {
            return name.substring(i+1);
         }
      }
   }
   
   private String getClassByName(String name)
   {
      // try to look up class directly, only work if name is the fully qualified name
      SB_ClassMap classMap = ComponentRegistry.getProjectBar().getCatalog().getClassMap();
      return classMap.getJavaClassName(name);
   }

   
   /** map SB_Variable name to variableObject for those variables
    * provided in {@link #initializeContent(Vector, List<String>, List<String>)}**/
   private Map<String, SB_Variable> variablesMap = new HashMap<String,SB_Variable>();
   
   private List<String> importedClasses = new ArrayList<String>();
   
   /** name of all known classes. See {@link #initKnownClasses()}**/
   private List<String> knownClasses = new ArrayList<String>();
   
   /**
    * Helper class containing information about a match for an expression.
    * This class used to be declared inside {@link AutoCompletionTextField}. 
    *
    * @author Owner
    *
    */
   public static class ParseInfo{
      public String funcName;
      public String paramName;
      public int index;
      public int paren; //parenthesis?
      public int first;

      public String toString(){
         return "funcName = " + funcName + "; paramName = " + paramName + "; index = " + index + "; paren = " + paren;
      }
   }
}
