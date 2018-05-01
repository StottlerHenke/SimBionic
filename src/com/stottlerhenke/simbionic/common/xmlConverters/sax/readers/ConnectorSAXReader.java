
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
    

public class ConnectorSAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:integer **/
  public static String id = "id";
  /** id to refer to internally refer to the id tag **/
  public static int id_ID = 1;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String endId = "endId";
  /** id to refer to internally refer to the endId tag **/
  public static int endId_ID = 2;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String endType = "endType";
  /** id to refer to internally refer to the endType tag **/
  public static int endType_ID = 3;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String startX = "startX";
  /** id to refer to internally refer to the startX tag **/
  public static int startX_ID = 4;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String startY = "startY";
  /** id to refer to internally refer to the startY tag **/
  public static int startY_ID = 5;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String endX = "endX";
  /** id to refer to internally refer to the endX tag **/
  public static int endX_ID = 6;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String endY = "endY";
  /** id to refer to internally refer to the endY tag **/
  public static int endY_ID = 7;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String priority = "priority";
  /** id to refer to internally refer to the priority tag **/
  public static int priority_ID = 8;

  /** any order, minOccurs=, type=xsd:boolean **/
  public static String interrupt = "interrupt";
  /** id to refer to internally refer to the interrupt tag **/
  public static int interrupt_ID = 9;

  /** any order, minOccurs=, type=xsd:string **/
  public static String comment = "comment";
  /** id to refer to internally refer to the comment tag **/
  public static int comment_ID = 10;

  /** any order, minOccurs=, type=xsd:integer **/
  public static String labelMode = "labelMode";
  /** id to refer to internally refer to the labelMode tag **/
  public static int labelMode_ID = 11;

  /** any order, minOccurs=, type=BindingGroup **/
  public static String bindings = "bindings";
  /** id to refer to internally refer to the bindings tag **/
  public static int bindings_ID = 12;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.Connector readObject;

    
  /** constructor **/
  public ConnectorSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.Connector ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.Connector getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
	 if (ConnectorSAXReader.id.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ConnectorSAXReader.id_ID)) ;
     }
     else    
     
	 if (ConnectorSAXReader.endId.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ConnectorSAXReader.endId_ID)) ;
     }
     else    
     
	 if (ConnectorSAXReader.endType.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ConnectorSAXReader.endType_ID)) ;
     }
     else    
     
	 if (ConnectorSAXReader.startX.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ConnectorSAXReader.startX_ID)) ;
     }
     else    
     
	 if (ConnectorSAXReader.startY.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ConnectorSAXReader.startY_ID)) ;
     }
     else    
     
	 if (ConnectorSAXReader.endX.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ConnectorSAXReader.endX_ID)) ;
     }
     else    
     
	 if (ConnectorSAXReader.endY.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ConnectorSAXReader.endY_ID)) ;
     }
     else    
     
	 if (ConnectorSAXReader.priority.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ConnectorSAXReader.priority_ID)) ;
     }
     else    
     
	 if (ConnectorSAXReader.interrupt.equals(tag)) {
       stackParser.addParser(new BooleanParser(tag,tagAttributes,this,ConnectorSAXReader.interrupt_ID)) ;
     }
     
     else    
     
			     if (ConnectorSAXReader.comment.equals(tag)) {
			       stackParser.addParser(new StringParser(tag,tagAttributes,this,ConnectorSAXReader.comment_ID)) ;
			     }
			     else 
     		
	 if (ConnectorSAXReader.labelMode.equals(tag)) {
       stackParser.addParser(new IntegerParser(tag,tagAttributes,this,ConnectorSAXReader.labelMode_ID)) ;
     }
     else    
     
     if (ConnectorSAXReader.bindings.equals(tag)) {
       stackParser.addParser(new BindingGroupSAXReader (stackParser,tag,tagAttributes,this,ConnectorSAXReader.bindings_ID));
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
      
	case 1: //case ConnectorSAXReader.id_ID:
       	    readObject.setId((Integer)result);
			break;    
     
	case 2: //case ConnectorSAXReader.endId_ID:
       	    readObject.setEndId((Integer)result);
			break;    
     
	case 3: //case ConnectorSAXReader.endType_ID:
       	    readObject.setEndType((Integer)result);
			break;    
     
	case 4: //case ConnectorSAXReader.startX_ID:
       	    readObject.setStartX((Integer)result);
			break;    
     
	case 5: //case ConnectorSAXReader.startY_ID:
       	    readObject.setStartY((Integer)result);
			break;    
     
	case 6: //case ConnectorSAXReader.endX_ID:
       	    readObject.setEndX((Integer)result);
			break;    
     
	case 7: //case ConnectorSAXReader.endY_ID:
       	    readObject.setEndY((Integer)result);
			break;    
     
	case 8: //case ConnectorSAXReader.priority_ID:
       	    readObject.setPriority((Integer)result);
			break;    
     
	case 9: //case ConnectorSAXReader.interrupt_ID:
       	    readObject.setInterrupt((Boolean)result);
  			break;    
     
     		case 10: //case ConnectorSAXReader.comment_ID:
       	    readObject.setComment((String)result);
			break;
     		
	case 11: //case ConnectorSAXReader.labelMode_ID:
       	    readObject.setLabelMode((Integer)result);
			break;    
     
       case 12: //case ConnectorSAXReader.bindings_ID
            if (result !=null) {
              readObject.setBindings((List)result);
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
 
