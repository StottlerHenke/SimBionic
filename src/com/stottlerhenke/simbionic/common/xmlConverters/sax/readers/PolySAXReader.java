
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
    

public class PolySAXReader extends Parser {


  /** any order, minOccurs=, type=IndexGroup **/
  public static String indices = "indices";
  /** id to refer to internally refer to the indices tag **/
  public static int indices_ID = 1;

  /** any order, minOccurs=, type=LocalGroup **/
  public static String locals = "locals";
  /** id to refer to internally refer to the locals tag **/
  public static int locals_ID = 2;

  /** any order, minOccurs=, type=NodeGroup **/
  public static String nodes = "nodes";
  /** id to refer to internally refer to the nodes tag **/
  public static int nodes_ID = 3;

  /** any order, minOccurs=, type=ConditionGroup **/
  public static String conditions = "conditions";
  /** id to refer to internally refer to the conditions tag **/
  public static int conditions_ID = 4;

  /** any order, minOccurs=, type=StartConnectorGroup **/
  public static String connectors = "connectors";
  /** id to refer to internally refer to the connectors tag **/
  public static int connectors_ID = 5;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.Poly readObject;

    
  /** constructor **/
  public PolySAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.Poly ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.Poly getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
     if (PolySAXReader.indices.equals(tag)) {
       stackParser.addParser(new IndexGroupSAXReader (stackParser,tag,tagAttributes,this,PolySAXReader.indices_ID));
     }
    else
    

     if (PolySAXReader.locals.equals(tag)) {
       stackParser.addParser(new LocalGroupSAXReader (stackParser,tag,tagAttributes,this,PolySAXReader.locals_ID));
     }
    else
    

     if (PolySAXReader.nodes.equals(tag)) {
       stackParser.addParser(new NodeGroupSAXReader (stackParser,tag,tagAttributes,this,PolySAXReader.nodes_ID));
     }
    else
    

     if (PolySAXReader.conditions.equals(tag)) {
       stackParser.addParser(new ConditionGroupSAXReader (stackParser,tag,tagAttributes,this,PolySAXReader.conditions_ID));
     }
    else
    

     if (PolySAXReader.connectors.equals(tag)) {
       stackParser.addParser(new StartConnectorGroupSAXReader (stackParser,tag,tagAttributes,this,PolySAXReader.connectors_ID));
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
      
       case 1: //case PolySAXReader.indices_ID
            if (result !=null) {
              readObject.setIndices((List)result);
            }
           
          break;    

       case 2: //case PolySAXReader.locals_ID
            if (result !=null) {
              readObject.setLocals((List)result);
            }
           
          break;    

       case 3: //case PolySAXReader.nodes_ID
           if (result !=null) {
            readObject.setNodes((com.stottlerhenke.simbionic.common.xmlConverters.model.NodeGroup)result);
           }
          
          break;    

       case 4: //case PolySAXReader.conditions_ID
            if (result !=null) {
              readObject.setConditions((List)result);
            }
           
          break;    

       case 5: //case PolySAXReader.connectors_ID
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
 
