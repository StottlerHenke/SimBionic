
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
    

public class NodeGroupSAXReader extends Parser {


  /** any order, minOccurs=, type=xsd:integereger **/
  public static String initial = "initial";
  /** id to refer to internally refer to the initial tag **/
  public static int initial_ID = 1;

  /** any order, minOccurs=, type=ActionNodeGroup **/
  public static String actionNodes = "actionNodes";
  /** id to refer to internally refer to the actionNodes tag **/
  public static int actionNodes_ID = 2;

  /** any order, minOccurs=, type=CompoundActionNodeGroup **/
  public static String compoundActionNode = "compoundActionNode";
  /** id to refer to internally refer to the compoundActionNode tag **/
  public static int compoundActionNode_ID = 3;

  protected String startTag;
  protected Hashtable startTagAttributes;
  protected com.stottlerhenke.simbionic.common.xmlConverters.model.NodeGroup readObject;

    
  /** constructor **/
  public NodeGroupSAXReader (StackParser stackParserController, String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObject = new  com.stottlerhenke.simbionic.common.xmlConverters.model.NodeGroup ();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
   /** returns object read by the parser **/
   public com.stottlerhenke.simbionic.common.xmlConverters.model.NodeGroup getValue () {
	  return readObject;
   }
  
   /** given the start of a tag create a parser to transform the content of the tag into a DM object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {	  
	//big if statement to decide which parser should take care of the new received tag
      
     if (NodeGroupSAXReader.initial.equals(tag)) {
       stackParser.addParser(new IntegerParser (tag,tagAttributes,this,NodeGroupSAXReader.initial_ID));
     }
    else
    

     if (NodeGroupSAXReader.actionNodes.equals(tag)) {
       stackParser.addParser(new ActionNodeGroupSAXReader (stackParser,tag,tagAttributes,this,NodeGroupSAXReader.actionNodes_ID));
     }
    else
    

     if (NodeGroupSAXReader.compoundActionNode.equals(tag)) {
       stackParser.addParser(new CompoundActionNodeGroupSAXReader (stackParser,tag,tagAttributes,this,NodeGroupSAXReader.compoundActionNode_ID));
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
      
       case 1: //case NodeGroupSAXReader.initial_ID
           if (result !=null) {
            readObject.setInitial((Integer)result);
           }
          
          break;    

       case 2: //case NodeGroupSAXReader.actionNodes_ID
            if (result !=null) {
              readObject.setActionNodes((List)result);
            }
           
          break;    

       case 3: //case NodeGroupSAXReader.compoundActionNode_ID
            if (result !=null) {
              readObject.setCompoundActionNodes((List)result);
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
 
