
package com.stottlerhenke.simbionic.common.xmlConverters.sax.readers;
 /*
 * class automatically generated using XSLT translator
 *
 */

import com.stottlerhenke.simbionic.common.xmlConverters.sax.Parser;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.StackParser;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.basicParsers.*;
import com.stottlerhenke.simbionic.common.xmlConverters.sax.readers.*;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import java.awt.Color;
    

public class ActionSAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:string **/
  public static String name = "name";
  /** id to refer to internally refer to the name tag **/
  public static int name_ID = 1;

  /** any order, minOccurs=, type=xsd:string **/
  public static String description = "description";
  /** id to refer to internally refer to the description tag **/
  public static int description_ID = 2;

  /** any order, minOccurs=, type=xsd:boolean **/
  public static String core = "core";
  /** id to refer to internally refer to the core tag **/
  public static int core_ID = 3;

  /** any order, minOccurs=, type=ParameterGroup **/
  public static String parameters = "parameters";
  /** id to refer to internally refer to the parameters tag **/
  public static int parameters_ID = 4;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.Action readObject;

    
  /** constructor **/
  public ActionSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.Action ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.Action getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
			     if (ActionSAXReader.name.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ActionSAXReader.name_ID)) ;
			     }
			     else 
     		
			     if (ActionSAXReader.description.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ActionSAXReader.description_ID)) ;
			     }
			     else 
     		
	 if (ActionSAXReader.core.equals(tag)) {
       stackParser.addParser(new BooleanParser(tag,tagAttributes,this,ActionSAXReader.core_ID)) ;
     }
     
     else    
     
     if (ActionSAXReader.parameters.equals(tag)) {
       stackParser.addParser(new ParameterGroupSAXReader (stackParser,tag,tagAttributes,this,ActionSAXReader.parameters_ID));
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
      
     		case 1: //case ActionSAXReader.name_ID:
       	    readObject.setName((String)result);
			break;
     		
     		case 2: //case ActionSAXReader.description_ID:
       	    readObject.setDescription((String)result);
			break;
     		
	case 3: //case ActionSAXReader.core_ID:
       	    readObject.setCore((Boolean)result);
  			break;    
     
       case 4: //case ActionSAXReader.parameters_ID
            if (result !=null) {
              readObject.setParameters((List)result);
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
 
