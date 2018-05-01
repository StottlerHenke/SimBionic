
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
    

public class ParameterSAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:string **/
  public static String name = "name";
  /** id to refer to internally refer to the name tag **/
  public static int name_ID = 1;

  /** any order, minOccurs=, type=xsd:string **/
  public static String type = "type";
  /** id to refer to internally refer to the type tag **/
  public static int type_ID = 2;

  /** any order, minOccurs=, type=xsd:string **/
  public static String description = "description";
  /** id to refer to internally refer to the description tag **/
  public static int description_ID = 3;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.Parameter readObject;

    
  /** constructor **/
  public ParameterSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.Parameter ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.Parameter getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
			     if (ParameterSAXReader.name.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ParameterSAXReader.name_ID)) ;
			     }
			     else 
     		
			     if (ParameterSAXReader.type.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ParameterSAXReader.type_ID)) ;
			     }
			     else 
     		
			     if (ParameterSAXReader.description.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ParameterSAXReader.description_ID)) ;
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
      
     		case 1: //case ParameterSAXReader.name_ID:
       	    readObject.setName((String)result);
			break;
     		
     		case 2: //case ParameterSAXReader.type_ID:
       	    readObject.setType((String)result);
			break;
     		
     		case 3: //case ParameterSAXReader.description_ID:
       	    readObject.setDescription((String)result);
			break;
     		     
      default: break;
     }
    }
    catch(Exception e){
    	e.printStackTrace();
    }
  }
  

 } 
 
