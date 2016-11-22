
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
    

public class PredicateSAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:string **/
  public static String name = "name";
  /** id to refer to internally refer to the name tag **/
  public static int name_ID = 1;

  /** any order, minOccurs=, type=xsd:string **/
  public static String description = "description";
  /** id to refer to internally refer to the description tag **/
  public static int description_ID = 2;

  /** any order, minOccurs=, type=xsd:string **/
  public static String returnType = "returnType";
  /** id to refer to internally refer to the returnType tag **/
  public static int returnType_ID = 3;

  /** any order, minOccurs=, type=xsd:boolean **/
  public static String core = "core";
  /** id to refer to internally refer to the core tag **/
  public static int core_ID = 4;

  /** any order, minOccurs=, type=ParameterGroup **/
  public static String parameters = "parameters";
  /** id to refer to internally refer to the parameters tag **/
  public static int parameters_ID = 5;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.Predicate readObject;

    
  /** constructor **/
  public PredicateSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.Predicate ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.Predicate getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
			     if (PredicateSAXReader.name.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,PredicateSAXReader.name_ID)) ;
			     }
			     else 
     		
			     if (PredicateSAXReader.description.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,PredicateSAXReader.description_ID)) ;
			     }
			     else 
     		
			     if (PredicateSAXReader.returnType.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,PredicateSAXReader.returnType_ID)) ;
			     }
			     else 
     		
	 if (PredicateSAXReader.core.equals(tag)) {
       stackParser.addParser(new BooleanParser(tag,tagAttributes,this,PredicateSAXReader.core_ID)) ;
     }
     
     else    
     
     if (PredicateSAXReader.parameters.equals(tag)) {
       stackParser.addParser(new ParameterGroupSAXReader (stackParser,tag,tagAttributes,this,PredicateSAXReader.parameters_ID));
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
      
     		case 1: //case PredicateSAXReader.name_ID:
       	    readObject.setName((String)result);
			break;
     		
     		case 2: //case PredicateSAXReader.description_ID:
       	    readObject.setDescription((String)result);
			break;
     		
     		case 3: //case PredicateSAXReader.returnType_ID:
       	    readObject.setReturnType((String)result);
			break;
     		
	case 4: //case PredicateSAXReader.core_ID:
       	    readObject.setCore((Boolean)result);
  			break;    
     
       case 5: //case PredicateSAXReader.parameters_ID
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
 
