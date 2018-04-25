
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
    

public class GlobalSAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:string **/
  public static String name = "name";
  /** id to refer to internally refer to the name tag **/
  public static int name_ID = 1;

  /** any order, minOccurs=, type=xsd:string **/
  public static String type = "type";
  /** id to refer to internally refer to the type tag **/
  public static int type_ID = 2;

  /** any order, minOccurs=, type=xsd:string **/
  public static String initial = "initial";
  /** id to refer to internally refer to the initial tag **/
  public static int initial_ID = 3;

  /** any order, minOccurs=, type=xsd:boolean **/
  public static String polymorphic = "polymorphic";
  /** id to refer to internally refer to the polymorphic tag **/
  public static int polymorphic_ID = 4;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.Global readObject;

    
  /** constructor **/
  public GlobalSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.Global ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.Global getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
			     if (GlobalSAXReader.name.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,GlobalSAXReader.name_ID)) ;
			     }
			     else 
     		
			     if (GlobalSAXReader.type.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,GlobalSAXReader.type_ID)) ;
			     }
			     else 
     		
			     if (GlobalSAXReader.initial.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,GlobalSAXReader.initial_ID)) ;
			     }
			     else 
     		
	 if (GlobalSAXReader.polymorphic.equals(tag)) {
       stackParser.addParser(new BooleanParser(tag,tagAttributes,this,GlobalSAXReader.polymorphic_ID)) ;
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
      
     		case 1: //case GlobalSAXReader.name_ID:
       	    readObject.setName((String)result);
			break;
     		
     		case 2: //case GlobalSAXReader.type_ID:
       	    readObject.setType((String)result);
			break;
     		
     		case 3: //case GlobalSAXReader.initial_ID:
       	    readObject.setInitial((String)result);
			break;
     		
	case 4: //case GlobalSAXReader.polymorphic_ID:
       	    readObject.setPolymorphic((Boolean)result);
  			break;    
          
      default: break;
     }
    }
    catch(Exception e){
    	e.printStackTrace();
    }
  }
  

 } 
 
