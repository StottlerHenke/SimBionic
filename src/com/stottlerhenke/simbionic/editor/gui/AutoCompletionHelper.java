package com.stottlerhenke.simbionic.editor.gui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * defines the variables and class specifications the helper knows about.
     * This data is used when calling {@link #parseDot(String, int, ParseInfo)}
     * and {@link #matchPartialDot(Vector, String, String)}
     * 
     * @param variables
     * @param importedClasses
     */
    public void initializeContent(List<SB_Variable> variables,
            List<String> importedClasses) {
        clearContent();
        if (variables != null) {
            for (SB_Variable var : variables) {
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
     * Generate completions for "variableName.dotArg".&nbsp;Returns true if some
     * completions where added to matchList.
     * 
     * 
     * @param matchList
     *            - new completions will be added to this list
     * @param variableName
     * @param dotArg
     * @return
     */
    public boolean matchPartialDot(List<SB_Auto_Match> matchList,
            String variableName, String dotArg) {
        try {
            SB_Variable var = getVariable(variableName);

            if (var == null) {
                return matchPartialDotStaticMethod(matchList, variableName,
                        dotArg);
            }

            // access the list of possible methods for the given variable
            String type = var.getFullTypeName();

            if (type == null) {
                return false;
            }

            Class<?> varClass = null;
            try {
                varClass = Class.forName(type);
            } catch (Exception e) {
                e.printStackTrace();
                return false; // no match
            }

            if (varClass == null) {
                return false;
            }

            Field[] members = varClass.getFields();// only the public fields
                                                   // //fixme. Does this include
                                                   // superclass members?
            Method[] methods = varClass.getMethods();// public methods

            Stream<Field> memberStream = Arrays.asList(members).stream();

            Stream<Method> methodStream = Arrays.asList(methods).stream();

            return addMatchesToMatchList(matchList, memberStream, methodStream,
                    dotArg);

        } catch (Exception e) {
            // e.printStackTrace();
        }

        return false;
    }

    /**
     * 
     * @param matchList
     *            The list of matches where matching methods should be added.
     * @param className
     * @param dotArg
     * @return
     */
    public boolean matchPartialDotStaticMethod(
            List<SB_Auto_Match> matchList, String className,
            String dotArg) {
        try {
            Class<?> varClass = this.getClass(className);

            if (varClass == null) {
                return false;
            }

            Field[] members = varClass.getFields();// only the public fields
                                                   // //fixme. Does this include
                                                   // superclass members?
            Method[] methods = varClass.getMethods();// public methods

            Stream<Field> staticMemberStream = Arrays.asList(members).stream()
                    .filter(field -> Modifier.isStatic(field.getModifiers()));

            Stream<Method> staticMethodStream = Arrays.asList(methods).stream()
                    .filter(method
                            -> Modifier.isStatic(method.getModifiers()));

            return addMatchesToMatchList(matchList, staticMemberStream,
                    staticMethodStream, dotArg);

        } catch (Exception e) {
            //2018-05 TODO: Why are exceptions swallowed at this step?
            // e.printStackTrace();
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
     * 
     * @param matchList
     *            The list of matches where matching classes should be added.
     * @param classNamePrefix
     */
    public void matchPartialClassName(
            List<SB_Auto_Match> matchList,
            String classNamePrefix) {
        if (classNamePrefix == null || classNamePrefix.length() == 0)
            return;

        //XXX: Old approach was accidentally resistant to CMEs; the new
        //approach is not.
        for (String aKnownClassName : knownClasses) {
            if (classNamePrefix.regionMatches(0, aKnownClassName, 0,
                    classNamePrefix.length())) {
                matchList.add(SB_Auto_Match
                        .ofSame(aKnownClassName));
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
     * 
     * @return true iff any matches have been added to {@code matchList}. Note
     * that the matches might have been already present.
     * */
    private static boolean addMatchesToMatchList(
            List<SB_Auto_Match> matchList,
            Stream<Field> fieldsToSearch, Stream<Method> methodsToSearch,
            String dotArg) {
        List<SB_Auto_Match> matchedMembers = fieldsToSearch
                .filter(field -> field.getName().startsWith(dotArg))
                .map(field -> getFieldIAD(field))
                .sorted(MATCH_COMPARATOR)
                .collect(Collectors.toList());

        List<SB_Auto_Match> matchedMethods = methodsToSearch
                .filter(method -> method.getName().startsWith(dotArg))
                .map(method -> getMethodIAD(method))
                .sorted(MATCH_COMPARATOR)
                .collect(Collectors.toList());

        matchList.addAll(matchedMembers);
        matchList.addAll(matchedMethods);

        boolean noAddedCompletions
                = matchedMembers.isEmpty() && matchedMethods.isEmpty();

        return !noAddedCompletions;
    }

    private static SB_Auto_Match getFieldIAD(Field field) {
        String fieldName = field.getName();
        String fieldType = field.getType().getName();
        String display = fieldName + " : " + fieldType;
        return SB_Auto_Match.of(fieldName, display);
    }

    private static SB_Auto_Match getMethodIAD(Method method) {
        return SB_Auto_Match.ofSame(getMethodDisplayName(method));
    }

    /**
     * returns string to display in the autocompletion list.&nbsp;Methods are
     * displayed as "methodName(type_1,...,type_n)"
     * 
     * @param method
     * @return
     */
    private static String getMethodDisplayName(Method method) {
        String str = method.getName() + "(";
        Class<?>[] params = method.getParameterTypes();
        // usually the params name is null

        if (params != null) {
            List<String> classStrings = Arrays.asList(params).stream()
                    .map(param -> getClassName(param))
                    .collect(Collectors.toList());

            String paramNamesString = String.join(",", classStrings);
            str += paramNamesString;
        }

        str += ")";

        return str;
    }

    /**
     * returns the name of the class without the package string
     * 
     * @param c
     * @return
     */
    private static String getClassName(Class<?> c) {
        if (c.isArray() == true) {
            String className = c.getSimpleName().replaceAll("\\[", " &#91;")
                    .replaceAll("\\]", " &#93;");
            return className;

        } else {
            return c.getSimpleName();
        }
    }


   /** map SB_Variable name to variableObject for those variables
    * provided in {@link #initializeContent(Vector, List<String>, List<String>)}**/
   private Map<String, SB_Variable> variablesMap = new HashMap<String,SB_Variable>();


    /**
     * XXX: calls to
     * {@link #getClass(String)} and {@link #initKnownClasses()} must
     * be synchronized with calls to {@link #initializeContent(Vector, List)
     * initializeCotent} and
     * {@link #clearContent()} to avoid a CME on this collection.
     */
    private List<String> importedClasses = new ArrayList<String>();

    /**
     * name of all known classes. See {@link #initKnownClasses()}
     * <br>
     * XXX: calls to
     * {@link #matchPartialClassName(List, String) matchPartialClassName} must
     * be synchronized with calls to {@link #initKnownClasses()} and
     * {@link #clearContent()} to avoid a CME on this collection.
     */
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

   /**
    * Helper class that associates the string shown in autocomplete with the
    * string that should be inserted into the edited area by autocomplete.
    * <br>
    * Previously named InsertionAndDisplayStrings; the shorter name is more
    * general but may be misleading.
    * <br>
    * TODO: It may be appropriate to have a more general "match object" with
    * customizable "rendering" to produce the insertion and display strings.
    * */
   protected static final class SB_Auto_Match {

       /**
        * The string that autocomplete should insert into the edited text
        * component.
        * */
       private final String insertion;
       /**
        * The string that autocomplete should display in the autocomplete
        * SB_GlassPane.
        * */
       private final String display;

       SB_Auto_Match(String insertion, String display) {
           this.insertion = Objects.requireNonNull(insertion);
           this.display = Objects.requireNonNull(display);
       }

       public String getStringToInsert() {
           return insertion;
       }

       public String getDisplay() {
           return display;
       }

       /***/
       public static SB_Auto_Match of(String insertion,
               String display) {
           return new SB_Auto_Match(insertion, display);
       }

       /**
        * This convenience method simplifies the case where the string to be
        * inserted is identical to the string to be displayed.
        * */
       public static SB_Auto_Match ofSame(String insertion) {
           return new SB_Auto_Match(insertion, insertion);
       }

   }

    protected static final Comparator<SB_Auto_Match> MATCH_COMPARATOR
            = (match1, match2) -> match1.getDisplay().compareTo(
                    match2.getDisplay());

}
