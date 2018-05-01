
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
    

public class ActionFolderGroupSAXReader extends Parser {


  /** sequence ,minOccurs=, type=Action **/
  public static String action = "action";
   /** id to refer to internally refer to the action tag **/
  public static int action_ID = 1;
 
  /** sequence ,minOccurs=, type=ActionFolder **/
  public static String actionFolder = "actionFolder";
   /** id to refer to internally refer to the actionFolder tag **/
  public static int actionFolder_ID = 2;
 
 
  protected String startTag;
  protected Hashtable startTagAttributes;
  com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolderGroup readObjects; 
  
  /** constructor **/
  public ActionFolderGroupSAXReader (StackParser stackParserController,String tag, Hashtable tagAttributes, Parser client, int property) {
     super(stackParserController,client,property);
	 readObjects = new com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolderGroup();
	 startTag = tag;
	 startTagAttributes = tagAttributes;
  }
   
  /** returns array of objects read by the parser **/
  public com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolderGroup getValue () {
	  return readObjects;
  }    
 
 /** given the start of a tag create a parser to transform the content of the tag into a TG object**/
  public void startElement(String tag, Hashtable tagAttributes) throws Exception {
    
    if (ActionFolderGroupSAXReader.action.equals(tag)) {
      stackParser.addParser(new ActionSAXReader(stackParser,tag,tagAttributes,this,0));
      return;
    }
       
    if (ActionFolderGroupSAXReader.actionFolder.equals(tag)) {
      stackParser.addParser(new ActionFolderSAXReader(stackParser,tag,tagAttributes,this,0));
      return;
    }
          
  }
  
    public void endElement(String tag) throws Exception {		
	  if (startTag.equals(tag)) {
			isDone = true;
	  }
	 else {
		//error
	 }
  }
 
  /** collect the result . The property argument is disregarded **/ 
  protected  void receiveParsingResult(int property, Object result) {
   try{
    if (result == null) return;
	 readObjects.getActionOrActionFolder().add(result);
   }
    catch(Exception e){
    	e.printStackTrace();
    }  	
  }
  

 } 
 
