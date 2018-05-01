
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
    

public class StartSAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:integer **/
  public static String id = "id";
  /** id to refer to internally refer to the id tag **/
  public static int id_ID = 1;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String type = "type";
  /** id to refer to internally refer to the type tag **/
  public static int type_ID = 2;

  /** any order, minOccurs=, type=ConnectorGroup **/
  public static String connectors = "connectors";
  /** id to refer to internally refer to the connectors tag **/
  public static int connectors_ID = 3;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.Start readObject;

    
  /** constructor **/
  public StartSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.Start ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.Start getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
	 if (StartSAXReader.id.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,StartSAXReader.id_ID)) ;
     }
     else    
     
	 if (StartSAXReader.type.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,StartSAXReader.type_ID)) ;
     }
     else    
     
     if (StartSAXReader.connectors.equals(tag)) {
       stackParser.addParser(new ConnectorGroupSAXReader (stackParser,tag,tagAttributes,this,StartSAXReader.connectors_ID));
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
      
	case 1: //case StartSAXReader.id_ID:
       	    readObject.setId((Integer)result);
			break;    
     
	case 2: //case StartSAXReader.type_ID:
       	    readObject.setType((Integer)result);
			break;    
     
       case 3: //case StartSAXReader.connectors_ID
            if (result !=null) {
              readObject.setConnectors((List)result);
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
 
