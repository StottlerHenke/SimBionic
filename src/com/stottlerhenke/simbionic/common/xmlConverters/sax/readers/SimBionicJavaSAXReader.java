
package com.stottlerhenke.simbionic.common.xmlConverters.sax.readers;
 /*
 * class automatically generated using XSLT translator
 *
 */

import com.stottlerhenke.simbionic.common.xmlConverters.sax.Parser;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.StackParser;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import java.awt.Color;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.basicParsers.*;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.readers.*;
    

public class SimBionicJavaSAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:integer **/
  public static String version = "version";
  /** id to refer to internally refer to the version tag **/
  public static int version_ID = 1;

  /** any order, minOccurs=, type=xsd:string **/
  public static String ipAddress = "ipAddress";
  /** id to refer to internally refer to the ipAddress tag **/
  public static int ipAddress_ID = 2;

  /** any order, minOccurs=, type=xsd:boolean **/
  public static String loopBack = "loopBack";
  /** id to refer to internally refer to the loopBack tag **/
  public static int loopBack_ID = 3;

  /** any order, minOccurs=, type=xsd:string **/
  public static String main = "main";
  /** id to refer to internally refer to the main tag **/
  public static int main_ID = 4;

  /** any order, minOccurs=, type=ActionFolderGroup **/
  public static String actions = "actions";
  /** id to refer to internally refer to the actions tag **/
  public static int actions_ID = 5;

  /** any order, minOccurs=, type=PredicateFolderGroup **/
  public static String predicates = "predicates";
  /** id to refer to internally refer to the predicates tag **/
  public static int predicates_ID = 6;

  /** any order, minOccurs=, type=ConstantFolderGroup **/
  public static String constants = "constants";
  /** id to refer to internally refer to the constants tag **/
  public static int constants_ID = 7;

  /** any order, minOccurs=, type=CategoryGroup **/
  public static String categories = "categories";
  /** id to refer to internally refer to the categories tag **/
  public static int categories_ID = 8;

  /** any order, minOccurs=, type=BehaviorFolderGroup **/
  public static String behaviors = "behaviors";
  /** id to refer to internally refer to the behaviors tag **/
  public static int behaviors_ID = 9;

  /** any order, minOccurs=, type=GlobalFolderGroup **/
  public static String globals = "globals";
  /** id to refer to internally refer to the globals tag **/
  public static int globals_ID = 10;

  /** any order, minOccurs=, type=JavaScript **/
  public static String javaScript = "javaScript";
  /** id to refer to internally refer to the javaScript tag **/
  public static int javaScript_ID = 11;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava readObject;

    
  /** constructor **/
  public SimBionicJavaSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
	 if (SimBionicJavaSAXReader.version.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,SimBionicJavaSAXReader.version_ID)) ;
     }
     else    
     
			     if (SimBionicJavaSAXReader.ipAddress.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,SimBionicJavaSAXReader.ipAddress_ID)) ;
			     }
			     else 
     		
	 if (SimBionicJavaSAXReader.loopBack.equals(tag)) {
       stackParser.addParser(new BooleanParser(tag,tagAttributes,this,SimBionicJavaSAXReader.loopBack_ID)) ;
     }
     
     else    
     
			     if (SimBionicJavaSAXReader.main.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,SimBionicJavaSAXReader.main_ID)) ;
			     }
			     else 
     		
     if (SimBionicJavaSAXReader.actions.equals(tag)) {
       stackParser.addParser(new ActionFolderGroupSAXReader (stackParser,tag,tagAttributes,this,SimBionicJavaSAXReader.actions_ID));
     }
    else
    

     if (SimBionicJavaSAXReader.predicates.equals(tag)) {
       stackParser.addParser(new PredicateFolderGroupSAXReader (stackParser,tag,tagAttributes,this,SimBionicJavaSAXReader.predicates_ID));
     }
    else
    

     if (SimBionicJavaSAXReader.constants.equals(tag)) {
       stackParser.addParser(new ConstantFolderGroupSAXReader (stackParser,tag,tagAttributes,this,SimBionicJavaSAXReader.constants_ID));
     }
    else
    

     if (SimBionicJavaSAXReader.categories.equals(tag)) {
       stackParser.addParser(new CategoryGroupSAXReader (stackParser,tag,tagAttributes,this,SimBionicJavaSAXReader.categories_ID));
     }
    else
    

     if (SimBionicJavaSAXReader.behaviors.equals(tag)) {
       stackParser.addParser(new BehaviorFolderGroupSAXReader (stackParser,tag,tagAttributes,this,SimBionicJavaSAXReader.behaviors_ID));
     }
    else
    

     if (SimBionicJavaSAXReader.globals.equals(tag)) {
       stackParser.addParser(new GlobalFolderGroupSAXReader (stackParser,tag,tagAttributes,this,SimBionicJavaSAXReader.globals_ID));
     }
    else
    

     if (SimBionicJavaSAXReader.javaScript.equals(tag)) {
       stackParser.addParser(new JavaScriptSAXReader (stackParser,tag,tagAttributes,this,SimBionicJavaSAXReader.javaScript_ID));
     }
    else
    

      {
      	//signal error
      }
      
  } //end of startElement method

  public void endElement(String tag) throws Exception {
	  if (startTag.equals(tag)) {
			isDone = true;
	  }
	 else {
		//error ?
	 }
  }
  
  /**  set the given field property of the object being read **/
  protected  void receiveParsingResult(int property, Object result) {
  	try{
     switch (property) {
      
	case 1: //case SimBionicJavaSAXReader.version_ID:
       	    readObject.setVersion((Integer)result);
			break;    
     
     		case 2: //case SimBionicJavaSAXReader.ipAddress_ID:
       	    readObject.setIpAddress((String)result);
			break;
     		
	case 3: //case SimBionicJavaSAXReader.loopBack_ID:
       	    readObject.setLoopBack((Boolean)result);
  			break;    
     
     		case 4: //case SimBionicJavaSAXReader.main_ID:
       	    readObject.setMain((String)result);
			break;
     		
       case 5: //case SimBionicJavaSAXReader.actions_ID
           if (result !=null) {
            readObject.setActions((com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolderGroup)result);
           }
          
          break;    

       case 6: //case SimBionicJavaSAXReader.predicates_ID
           if (result !=null) {
            readObject.setPredicates((com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolderGroup)result);
           }
          
          break;    

       case 7: //case SimBionicJavaSAXReader.constants_ID
           if (result !=null) {
            readObject.setConstants((com.stottlerhenke.simbionic.common.xmlConverters.model.ConstantFolderGroup)result);
           }
          
          break;    

       case 8: //case SimBionicJavaSAXReader.categories_ID
            if (result !=null) {
              readObject.setCategories((List)result);
            }
           
          break;    

       case 9: //case SimBionicJavaSAXReader.behaviors_ID
           if (result !=null) {
            readObject.setBehaviors((com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolderGroup)result);
           }
          
          break;    

       case 10: //case SimBionicJavaSAXReader.globals_ID
           if (result !=null) {
            readObject.setGlobals((com.stottlerhenke.simbionic.common.xmlConverters.model.GlobalFolderGroup)result);
           }
          
          break;    

       case 11: //case SimBionicJavaSAXReader.javaScript_ID
           if (result !=null) {
            readObject.setJavaScript((com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript)result);
           }
          
          break;    
     
      default: break;
     }
    }
    catch(Exception e){
    	e.printStackTrace();
    }
  }
  

 } 
 
