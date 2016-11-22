package com.stottlerhenke.simbionic.engine.file;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.Version;
import com.stottlerhenke.simbionic.common.classes.SB_ClassMap;
import com.stottlerhenke.simbionic.common.xmlConverters.XMLObjectConverter;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Action;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolderGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior;
import com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolderGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Category;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Global;
import com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Predicate;
import com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolderGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava;
import com.stottlerhenke.simbionic.engine.SB_JavaScriptEngine;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.core.SB_Action;
import com.stottlerhenke.simbionic.engine.core.SB_BehaviorClass;
import com.stottlerhenke.simbionic.engine.core.SB_Function;
import com.stottlerhenke.simbionic.engine.core.SB_ParamList;
import com.stottlerhenke.simbionic.engine.core.SB_TypeHierarchy;
import com.stottlerhenke.simbionic.engine.core.SB_VariableMap;
import com.stottlerhenke.simbionic.engine.parser.SB_VarClass;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;


/**
 * This class loads a project file.
 */
public class SB_ProjectSpec extends SB_Specification
{
   private SimBionicJava _dataModel;
	/**
	 * Constructor
	 * @param reader the stream reader from which to read the specification
	 * @param projectName the name of the project
	 */
	public SB_ProjectSpec(URL projectName)
	{
		super(projectName.toString());
		try {
		   _dataModel = XMLObjectConverter.getInstance().zippedXMLToObject(projectName);
		} catch (Exception ex) {
		   System.err.println("Couldn't load the project : " + projectName + " " + ex.getMessage());
		}
	}

	/**
	 * Reads the entire specification from the loaded data model.
	 * @param book container for all engine singletons
	 * @throws SB_FileException on failure
	 */
	public void read(SB_SingletonBook book) throws SB_FileException
	{
	  if (SIM_Constants.DEBUG_INFO_ON)
	  	book.getLogger().log("** Reading project spec '" + _specName + "'...");

		book.getFileRegistry().setProjectFilename(_specName);
		
		// verify a compatible sim-file format
		int version =_dataModel.getVersion(); 
		if (!isCompatibleSpecFormat(version))
		{
			throw new SB_FileException("Incompatible project file format: engine version "
										+ Version.FILE_FORMAT_MAX_VERSION + ", file version " + version);
		}

		book.getFileRegistry().setFormatVersion(version);

	  if (SIM_Constants.DEBUG_INFO_ON)
	  	book.getLogger().log("** Specification format version " + version,SB_Logger.INIT);

		// read file version information
		book.getFileRegistry().setSpecVersion(version);

		readDescriptors(book);
		readClasses(book);
		readActions(book);
		readPredicates(book);

		try
		{
		  readGlobals(book);
		}
		catch(SB_Exception ex)
		{
		  throw new SB_FileException(ex.toString());
		}
		
		readBehaviors(book);
		
		readJavaScript(book);

	  if (SIM_Constants.DEBUG_INFO_ON)
	  	book.getLogger().log("** Project spec file '" + _specName + "' loaded.",SB_Logger.INIT);
	}

	/**
	 * Loads descriptor hierarchies from the loaded data model.
	 * @param book container for all engine singletons
	 * @throws SB_FileException on failure
	 */
	private void readDescriptors(SB_SingletonBook book) throws SB_FileException
	{
		int numHiers = _dataModel.getCategories().size();

	  if (SIM_Constants.DEBUG_INFO_ON)
	  	book.getLogger().log(".Loading " + numHiers + " hierarchies...",SB_Logger.INIT);

		for (Category category : _dataModel.getCategories())
		{
			SB_TypeHierarchy hier = new SB_TypeHierarchy();
			hier.load(category,book);

			book.getBehaviorRegistry().addHierarchy(hier);

		  if (SIM_Constants.DEBUG_INFO_ON)
		  	book.getLogger().log(hier.toString(),SB_Logger.INIT);
		}
	}

	/**
	 *  Read the class types from the loaded data model.
	 * 
	 * @param book
	 * @throws SB_FileException
	 */
	private void readClasses(SB_SingletonBook book) throws SB_FileException
	{
	   for (Class javaClass : SB_ClassMap.getBaseJavaClasses()) {
	      addJavaClass(book, javaClass.getSimpleName(), javaClass.getName());
	   }
	   
	   List<String> importedClasses = _dataModel.getJavaScript().getImportedJavaClasses();
		for( String javaClassName : importedClasses) {
			String classPackage = javaClassName;
			String className = javaClassName.substring(javaClassName.lastIndexOf('.') + 1);
			addJavaClass(book, className, classPackage);
	    }
		
		//Now that all classes read in, convert class descriptions
		try
		{
		  book.getUserClassMap().convertClassDescriptions(book);
		}
		catch(SB_Exception ex)
		{
		  throw new SB_FileException(ex.toString());
		}
	}
	
	private void addJavaClass(SB_SingletonBook book, String className, String classPackage)
	      throws SB_FileException {
      book.getUserClassMap().addJavaClass(className, classPackage);
      
      if (SIM_Constants.DEBUG_INFO_ON)
      book.getLogger().log("Loaded class: " + classPackage, SB_Logger.INIT);
    
	}
	
	
	/**
	 * Loads actions from the loaded data model. 
         *
	 * @param book container for all engine singletons
	 * @throws SB_FileException on failure
	 */
	private void readActions(SB_SingletonBook book) throws SB_FileException
	{

	   if(SIM_Constants.DEBUG_INFO_ON)
	      book.getLogger().log(".Loading Actions...",SB_Logger.INIT);

	   readActions(book, _dataModel.getActions()); 

	   if(SIM_Constants.DEBUG_INFO_ON)
	      book.getLogger().log(".Loaded Actions.",SB_Logger.INIT);

	   SB_Action none = new SB_Action("None", new SB_ParamList());
	   book.getBehaviorRegistry().addAction(none);
	}
	
	private void readActions(SB_SingletonBook book, ActionFolderGroup actions) {
	   for (Object actionOrFolder : actions.getActionOrActionFolder()) {
         if (actionOrFolder instanceof Action) {
            readAction(book, ((Action)actionOrFolder));
         } else if (actionOrFolder instanceof ActionFolder) {
            ActionFolder actionFolder = (ActionFolder)actionOrFolder;
            readActions(book, actionFolder.getActionChildren());
         }
      }
	}
	
	private void readAction(SB_SingletonBook book, Action action) {
	   SB_Action sbAction = new SB_Action(action);
       book.getBehaviorRegistry().addAction(sbAction);

      if(SIM_Constants.DEBUG_INFO_ON)
         book.getLogger().log(action.toString());
	}

	/**
	 * Loads predicates from the loaded data model.
	 * @param book container for all engine singletons
	 * @throws SB_FileException on failure
	 */
	private void readPredicates(SB_SingletonBook book) throws SB_FileException
	{
	   if(SIM_Constants.DEBUG_INFO_ON)
	      book.getLogger().log(".Loading  predicates...",SB_Logger.INIT);

	   readPredicates(book, _dataModel.getPredicates());

	   if(SIM_Constants.DEBUG_INFO_ON)
	      book.getLogger().log(".Loaded predicates.",SB_Logger.INIT);
	}
	
	private void readPredicates(SB_SingletonBook book, PredicateFolderGroup predicates) {
	   for (Object predicateOrFolder : predicates.getPredicateOrPredicateFolder()) {
	      if (predicateOrFolder instanceof Predicate) {
	         readPredicate(book, ((Predicate)predicateOrFolder));
	      } else if (predicateOrFolder instanceof PredicateFolder) {
	         PredicateFolder predicateFolder = (PredicateFolder)predicateOrFolder;
	         readPredicates(book, predicateFolder.getPredicateChildren());
	      }
	   }
	}

	private void readPredicate(SB_SingletonBook book, Predicate predicate) {
	   SB_Function sbFunction = new SB_Function(predicate);
	   book.getBehaviorRegistry().addPredicate(sbFunction);

	   if(SIM_Constants.DEBUG_INFO_ON)
	      book.getLogger().log(sbFunction.toString());
	}

	/**
	 * Loads global variables from the loaded data model.
	 * @param book container for all engine singletons
	 * @throws SB_FileException on failure
	 */
	private void readGlobals(SB_SingletonBook book) throws SB_FileException, SB_Exception
	{
		SB_VariableMap globals = new SB_VariableMap();
		
	   if(SIM_Constants.DEBUG_INFO_ON)
	       book.getLogger().log(".Loading global variables...",SB_Logger.INIT);
	   
	   for (Global global : _dataModel.getGlobals()) {
	      String name = global.getName();
	      SB_Variable var = new SB_VarClass();

	      String type = global.getType();
	      String initialValue = global.getInitial();
	      
	      Object value = initialValue;
	      boolean isNull = initialValue == null || initialValue.equalsIgnoreCase("null");
	      if (!isNull && !type.equals(String.class.getName())) {
	    	  // evaluate the initial value if it's not string and null.
	    	  SB_JavaScriptEngine javaScriptEngine = book.getJavaScriptEngine();
	    	  Object evalReturn = javaScriptEngine.evaluate(initialValue);
	    	  // cast the initial value to Java object.
	    	  value = SB_JavaScriptEngine.castToJavaObject(evalReturn, type);
	      } 

	      
	      var.setValue(value);
	      var.setType(type);
	     
	      if(SIM_Constants.DEBUG_INFO_ON) {
	        String varVal = null;
	        if (var.getValue() != null) {
	           varVal = var.getValue().getClass().getName();
	        }
	        book.getLogger().log(".\tLoaded variable " + name + " (" + varVal
	              + ")",SB_Logger.INIT);
	      }

	      globals.AddVariable(name, var);
	     }
		
		book.getEntityManager().setGlobalTemplate(globals);
	}

	/**
	 * Loads behaviors from the loaded data model.
	 * @param book container for all engine singletons
	 * @throws SB_FileException on failure
	 */
	private void readBehaviors(SB_SingletonBook book) throws SB_FileException
	{
	   readBehaviors(book, _dataModel.getBehaviors());
	}
	
	private void readBehaviors(SB_SingletonBook book, BehaviorFolderGroup behaviors)
	  throws SB_FileException {
      for (Object behaviorOrFolder : behaviors.getBehaviorOrBehaviorFolder()) {
         if (behaviorOrFolder instanceof Behavior) {
            readBehavior(book, ((Behavior)behaviorOrFolder));
         } else if (behaviorOrFolder instanceof BehaviorFolder) {
            BehaviorFolder behaviorFolder = (BehaviorFolder)behaviorOrFolder;
            readBehaviors(book, behaviorFolder.getBehaviorChildren());
         }
      }
   }
   
	private void readBehavior(SB_SingletonBook book, Behavior behavior) throws SB_FileException {
	   // set the behavior's unique ID on creation
	   SB_BehaviorClass sbBehavior = new SB_BehaviorClass( book.getBehaviorRegistry().requestBehaviorId() );

	   sbBehavior.load(behavior, book);

	   book.getBehaviorRegistry().addBehavior(sbBehavior);
	   book.getFileRegistry().associatePackage(_specName, sbBehavior.getName());

	}

	private void readJavaScript(SB_SingletonBook book) throws SB_FileException {
	   
	   JavaScript javaScript = _dataModel.getJavaScript();
      SB_JavaScriptEngine jsEngine = book.getJavaScriptEngine();
      List<String> allJavaClasses = new ArrayList<String>();
      allJavaClasses.addAll(javaScript.getImportedJavaClasses());
      try {
         jsEngine.init(javaScript.getJsFiles(), new ArrayList<String>(),allJavaClasses);
      } catch (Exception ex) {
         throw new SB_FileException("Error reading javaScript : " + ex.getMessage());
      }
      
	}
	
	


}
